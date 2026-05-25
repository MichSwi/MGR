package mgr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class stanRealTime {

    public int wartosc;
    public int numerPaska;
    public int procent;

    stanRealTime(int wartosc, int procent, int numerPaska) {
        this.wartosc = wartosc;
        this.procent = procent;
        this.numerPaska = numerPaska;
    }
}

public class WatekPobierz extends SwingWorker<Void, stanRealTime> {

    private static final String API_KEY = "opXIV-5dVqM7Ia_DQmr9L2giM4hKSk07FwlE7zi2_h0";

    private Map<Long, Punkt> punktyLista;

    private JProgressBar pasekPostepuTF;
    private JProgressBar pasekPostepuOSM;
    private JProgressBar pasekPostepuCzytajOSM;
    private JProgressBar pasekPostepuCzytajTF;

    private Map<Long, Punkt> allNodes = new HashMap<>();
    private List<TrafficSegment> ruchUliczny = new ArrayList<>();

    infoTXT info = new infoTXT(
            DANE.nazwaPliku,
            DANE._4_N_maxLAT,
            DANE._3_E_maxLON,
            DANE._1_W_minLON,
            DANE._2_S_minLAT
    );

    public WatekPobierz(
            List<Droga> drogi,
            Map<Long, Punkt> punktyLista,
            JProgressBar pasekPostepuOSM,
            JProgressBar pasekPostepuTF,
            JProgressBar pasekPostepuCzytajOSM,
            JProgressBar pasekPostepuCzytajTF) {

        this.punktyLista = punktyLista;
        this.pasekPostepuOSM = pasekPostepuOSM;
        this.pasekPostepuTF = pasekPostepuTF;
        this.pasekPostepuCzytajOSM = pasekPostepuCzytajOSM;
        this.pasekPostepuCzytajTF = pasekPostepuCzytajTF;
    }

    @Override
    protected Void doInBackground() throws Exception {

        // pobieranie OSM
        if (DANE.coZaznaczone.get(0)) {
            pobierzOSM();
        }

        // czytanie OSM
        if (DANE.coZaznaczone.get(1)) {
            info.wczytajPlik(DANE.nazwaPliku + ".txt");

            DANE._1_W_minLON = info.get1_W_LAT();
            DANE._2_S_minLAT = info.get2_S_LON();
            DANE._3_E_maxLON = info.get3_E_SZER_R();
            DANE._4_N_maxLAT = info.get4_N_WYS();

            System.out.println("W=" + DANE._1_W_minLON);
            System.out.println("S=" + DANE._2_S_minLAT);
            System.out.println("E=" + DANE._3_E_maxLON);
            System.out.println("N=" + DANE._4_N_maxLAT);

            czytajOSM();
        }

        // pobieranie HERE Traffic
        if (DANE.coZaznaczone.get(2)) {
            pobierzTF();
        }

        // czytanie HERE Traffic
        if (DANE.coZaznaczone.get(3)) {
            czytajTF();
        }

        // zapis dodatkowych informacji
        if (DANE.coZaznaczone.get(4)) {
            info.zapiszPlik();
        }

        return null;
    }

    @Override
    protected void process(List<stanRealTime> stan) {
        stanRealTime ostatnia = stan.get(stan.size() - 1);

        int wartosc = ostatnia.wartosc;
        int procent = ostatnia.procent;
        int numerPaska = ostatnia.numerPaska;

        String tekst = "";

        if (numerPaska == 1) {
            tekst = formatujBajty(wartosc);
            pasekPostepuOSM.setString(tekst);

        } else if (numerPaska == 2) {
            pasekPostepuCzytajOSM.setString("Wczytano: " + wartosc);
            pasekPostepuCzytajOSM.setValue(procent);

        } else if (numerPaska == 3) {
            tekst = formatujBajty(wartosc);
            pasekPostepuTF.setString(tekst);
            pasekPostepuTF.setValue(procent);

        } else if (numerPaska == 4) {
            pasekPostepuCzytajTF.setString("Wczytano: " + wartosc);
            pasekPostepuCzytajTF.setValue(procent);
        }
    }

    @Override
    protected void done() {
        punktyLista.clear();
        punktyLista.putAll(allNodes);

        DANE.ruchUliczny.clear();
        DANE.ruchUliczny.addAll(this.ruchUliczny);

        DANE.ustawToryWode();
        DANE.ustawOdleglosci();
        DANE.ustawStartKoniec();
        DANE.ustawPolaczenia();
        DANE.ustawRuchUliczny();
        DANE.budujWezlyZDrog();
        DANE.ustawMaxSpeed();

        System.out.println("DONE");
    }

    private String formatujBajty(int wartosc) {
        if (wartosc < 1024) {
            return "Pobrano: " + wartosc + " B";
        } else if (wartosc < 1024 * 1024) {
            return "Pobrano: " + (wartosc / 1024) + " KB";
        } else {
            return "Pobrano: "
                    + (wartosc / (1024 * 1024))
                    + " MB, "
                    + wartosc % (1024 * 1024) / 1024
                    + " KB";
        }
    }

    private void pobierzOSM() {
        pasekPostepuOSM.setIndeterminate(true);

        String query = String.format(Locale.US,
                "[out:xml][timeout:60];"
                + "("
                + "way[\"highway\"]"
                + "[\"highway\"!~\"^(footway|path|cycleway|bridleway|steps|pedestrian|track|elevator|platform|driveway)$\"]"
                + "(%.6f,%.6f,%.6f,%.6f);"
//                + "way[\"railway\"~\"^(rail|tram|light_rail|subway)$\"]"
//                + "(%.6f,%.6f,%.6f,%.6f);"
//                + "way[\"natural\"=\"water\"][\"water\"!=\"ditch\"]"
//                + "(%.6f,%.6f,%.6f,%.6f);"
//                + "relation[\"natural\"=\"water\"][\"water\"!=\"ditch\"]"
//                + "(%.6f,%.6f,%.6f,%.6f);"
//                + "way[\"waterway\"][\"waterway\"!=\"ditch\"]"
//                + "(%.6f,%.6f,%.6f,%.6f);"
//                + "relation[\"waterway\"][\"waterway\"!=\"ditch\"]"
//                + "(%.6f,%.6f,%.6f,%.6f);"
                + ");"
                + "(._;>;);"
                + "out body;",
                //DANE._2_S_minLAT, DANE._1_W_minLON, DANE._4_N_maxLAT, DANE._3_E_maxLON,
//                DANE._2_S_minLAT, DANE._1_W_minLON, DANE._4_N_maxLAT, DANE._3_E_maxLON,
//                DANE._2_S_minLAT, DANE._1_W_minLON, DANE._4_N_maxLAT, DANE._3_E_maxLON,
//                DANE._2_S_minLAT, DANE._1_W_minLON, DANE._4_N_maxLAT, DANE._3_E_maxLON,
//                DANE._2_S_minLAT, DANE._1_W_minLON, DANE._4_N_maxLAT, DANE._3_E_maxLON,
                DANE._2_S_minLAT, DANE._1_W_minLON, DANE._4_N_maxLAT, DANE._3_E_maxLON
        );

        String[] endpoints = new String[]{
            "https://overpass-api.de/api/interpreter",
            "https://overpass.kumi.systems/api/interpreter",
            "https://overpass.openstreetmap.fr/api/interpreter"
        };

        File out = new File("POBRANE_PLIKI", DANE.nazwaPliku + ".osm");

        System.out.println("Zapisuję do: " + out.getAbsolutePath());

        for (String endpoint : endpoints) {
            String urlStr = endpoint + "?data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            System.out.println("➡ Pobieram z: " + endpoint);
            System.out.println(urlStr);

            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                conn.setRequestProperty("User-Agent", "MGR-Downloader/1.0 (Java)");

                int code = conn.getResponseCode();

                InputStream in = (code >= 200 && code < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                int pobraneBajty = 0;

                try (in; FileOutputStream fos = new FileOutputStream(out)) {
                    byte[] buf = new byte[8192];
                    int r;

                    while ((r = in.read(buf)) != -1) {
                        fos.write(buf, 0, r);
                        pobraneBajty += r;
                        publish(new stanRealTime(pobraneBajty, 0, 1));
                    }
                }

                if (code >= 200 && code < 300) {
                    System.out.println("Gotowe: " + out.length() + " bajtów");

                    pasekPostepuOSM.setIndeterminate(false);
                    pasekPostepuOSM.setValue(100);
                    pasekPostepuOSM.setString("Pobieranie zakończone");

                    return;
                } else {
                    System.err.println("Overpass HTTP " + code + " – spróbuję kolejny mirror.");
                }

            } catch (Exception e) {
                System.err.println("Błąd połączenia z " + endpoint + ": " + e.getMessage());
            }
        }

        System.err.println("Nie udało się pobrać – spróbuj mniejszego bboxa lub później.");

        pasekPostepuOSM.setIndeterminate(false);
        pasekPostepuOSM.setValue(30);
        pasekPostepuOSM.setString("Błąd pobierania");
    }

    private void czytajOSM() {
        allNodes.clear();

        DANE.drogi.clear();
        DANE.wezly.clear();
        DANE.ALG_SCIEZKA.clear();
        DANE.relacje.clear();

        try {
            System.out.println("Otwieram plik: " + DANE.nazwaPliku + ".osm");

            File file = new File("POBRANE_PLIKI/" + DANE.nazwaPliku + ".osm");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("node");
            NodeList wList = doc.getElementsByTagName("way");
            NodeList rList = doc.getElementsByTagName("relation");

            int iloscWszystkich = nList.getLength() + wList.getLength() + rList.getLength();

            int[] ilosc = {0};

            wczytajPunktyOSM(nList, ilosc, iloscWszystkich);

            System.out.println("➡ Wszystkich node: " + allNodes.size());

            Map<Long, Integer> nodeWayLicznik = policzUzyciePunktowPrzezWay(wList);

            wczytajDrogiOSM(wList, nodeWayLicznik, ilosc, iloscWszystkich);

            wczytajRelacjeOSM(rList, wList, ilosc, iloscWszystkich);

            System.out.println("➡ Wszystkich relation: " + DANE.relacje.size());
            System.out.println("============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wczytajPunktyOSM(NodeList nList, int[] ilosc, int iloscWszystkich) {
        for (int i = 0; i < nList.getLength(); i++) {
            Element e = (Element) nList.item(i);

            long id = Long.parseLong(e.getAttribute("id"));
            double lat = Double.parseDouble(e.getAttribute("lat"));
            double lon = Double.parseDouble(e.getAttribute("lon"));

            Punkt p = new Punkt(lat, lon, id);

            NodeList tags = e.getElementsByTagName("tag");

            for (int t = 0; t < tags.getLength(); t++) {
                Element tag = (Element) tags.item(t);
                p.tags.put(tag.getAttribute("k"), tag.getAttribute("v"));
            }

            allNodes.put(id, p);

            ilosc[0]++;
            publish(new stanRealTime(ilosc[0], procent(ilosc[0], iloscWszystkich), 2));
        }
    }

    private Map<Long, Integer> policzUzyciePunktowPrzezWay(NodeList wList) {
        Map<Long, Integer> nodeWayLicznik = new HashMap<>();

        for (int i = 0; i < wList.getLength(); i++) {
            Element elemWay = (Element) wList.item(i);
            NodeList nds = elemWay.getElementsByTagName("nd");

            Set<Long> unikalneRefWWay = new HashSet<>();

            for (int j = 0; j < nds.getLength(); j++) {
                Element nd = (Element) nds.item(j);

                if (!nd.hasAttribute("ref") || nd.getAttribute("ref").isBlank()) {
                    continue;
                }

                long ref = Long.parseLong(nd.getAttribute("ref"));

                if (unikalneRefWWay.add(ref)) {
                    nodeWayLicznik.merge(ref, 1, Integer::sum);
                }
            }
        }

        return nodeWayLicznik;
    }

    private void wczytajDrogiOSM(
            NodeList wList,
            Map<Long, Integer> nodeWayLicznik,
            int[] ilosc,
            int iloscWszystkich) {

        for (int i = 0; i < wList.getLength(); i++) {
            int licznikSegmentow = 0;

            Element elemWay = (Element) wList.item(i);
            long idOSM = Long.parseLong(elemWay.getAttribute("id"));

            long idSeg = idOSM * 100L + licznikSegmentow;
            Droga biezaca = new Droga(idSeg);

            ustawTagiDrogi(biezaca, elemWay);

            boolean czyDrogaSamochodowa = biezaca.tags.containsKey("highway");

            NodeList nds = elemWay.getElementsByTagName("nd");

            for (int j = 0; j < nds.getLength(); j++) {
                Element nd = (Element) nds.item(j);

                long ref = -1;

                if (nd.hasAttribute("ref") && !nd.getAttribute("ref").isBlank()) {
                    ref = Long.parseLong(nd.getAttribute("ref"));
                }

                Punkt wezel = null;

                if (nd.hasAttribute("lat") && nd.hasAttribute("lon")) {
                    double lat = Double.parseDouble(nd.getAttribute("lat"));
                    double lon = Double.parseDouble(nd.getAttribute("lon"));
                    wezel = new Punkt(lat, lon, ref);

                } else if (ref != -1) {
                    wezel = allNodes.get(ref);
                }

                if (wezel == null) {
                    continue;
                }

                if (!czyPunktWGranicach(wezel)) {
                    continue;
                }
                wezel.ustawXY();

                boolean czyWspolnyNode = (ref != -1) && nodeWayLicznik.getOrDefault(ref, 0) >= 2;
                boolean czySrodek = (j != 0 && j != nds.getLength() - 1);

                if (czyWspolnyNode && czySrodek && czyDrogaSamochodowa) {
                    if (!biezaca.punkty.isEmpty()) {
                        biezaca.punkty.add(wezel);
                        DANE.drogi.put(biezaca.ID, biezaca);

                        ilosc[0]++;
                        publish(new stanRealTime(ilosc[0], procent(ilosc[0], iloscWszystkich), 2));
                    }

                    licznikSegmentow++;

                    long idSegNowy = idOSM * 100L + licznikSegmentow;
                    Droga nowa = new Droga(idSegNowy);

                    nowa.tags.putAll(biezaca.tags);
                    nowa.jednokierunkowa = biezaca.jednokierunkowa;
                    nowa.nazwa = biezaca.nazwa;
                    nowa.maxspeed = biezaca.maxspeed;

                    biezaca = nowa;
                    biezaca.punkty.add(wezel);

                } else {
                    biezaca.punkty.add(wezel);
                }
            }

            if (!biezaca.punkty.isEmpty()) {
                biezaca.IDosm = idOSM;
                DANE.drogi.put(biezaca.ID, biezaca);

                ilosc[0]++;
                publish(new stanRealTime(ilosc[0], procent(ilosc[0], iloscWszystkich), 2));
            }
        }
    }

    private void wczytajRelacjeOSM(
            NodeList rList,
            NodeList wList,
            int[] ilosc,
            int iloscWszystkich) {

        Map<Long, Element> wayXmlPoIdOSM = zbudujIndeksWay(wList);

        for (int i = 0; i < rList.getLength(); i++) {
            Element elemRelation = (Element) rList.item(i);

            long idRelacji = Long.parseLong(elemRelation.getAttribute("id"));
            Relacja relacja = new Relacja(idRelacji);

            NodeList tagi = elemRelation.getElementsByTagName("tag");

            for (int k = 0; k < tagi.getLength(); k++) {
                Element tag = (Element) tagi.item(k);

                String key = tag.getAttribute("k");
                String value = tag.getAttribute("v");

                relacja.tags.put(key, value);
            }

            NodeList members = elemRelation.getElementsByTagName("member");

            for (int j = 0; j < members.getLength(); j++) {
                Element member = (Element) members.item(j);

                String typ = member.getAttribute("type");

                if (!"way".equals(typ)) {
                    continue;
                }

                if (!member.hasAttribute("ref") || member.getAttribute("ref").isBlank()) {
                    continue;
                }

                long refWay = Long.parseLong(member.getAttribute("ref"));

                Element elemWay = wayXmlPoIdOSM.get(refWay);

                if (elemWay == null) {
                    continue;
                }

                Droga droga = utworzDrogeZElementuWay(elemWay);

                if (droga != null && droga.punkty.size() >= 2) {
                    relacja.drogi.add(droga);
                }
            }

            if (!relacja.drogi.isEmpty()) {
                DANE.relacje.put(relacja.ID, relacja);
            }

            ilosc[0]++;
            publish(new stanRealTime(ilosc[0], procent(ilosc[0], iloscWszystkich), 2));
        }
    }

    private Map<Long, Element> zbudujIndeksWay(NodeList wList) {
        Map<Long, Element> wayXmlPoIdOSM = new HashMap<>();

        for (int i = 0; i < wList.getLength(); i++) {
            Element elemWay = (Element) wList.item(i);
            long idOSM = Long.parseLong(elemWay.getAttribute("id"));
            wayXmlPoIdOSM.put(idOSM, elemWay);
        }

        return wayXmlPoIdOSM;
    }

    private void ustawTagiDrogi(Droga droga, Element elemWay) {
        droga.jednokierunkowa = "false";

        NodeList tagi = elemWay.getElementsByTagName("tag");

        for (int k = 0; k < tagi.getLength(); k++) {
            Element tag = (Element) tagi.item(k);

            String key = tag.getAttribute("k");
            String value = tag.getAttribute("v");

            droga.tags.put(key, value);

            if ("oneway".equals(key)) {
                if ("yes".equalsIgnoreCase(value)
                        || "1".equals(value)
                        || "true".equalsIgnoreCase(value)) {

                    droga.jednokierunkowa = "true";

                } else if ("-1".equalsIgnoreCase(value)) {
                    droga.jednokierunkowa = "-1";

                } else if ("no".equalsIgnoreCase(value)
                        || "0".equals(value)
                        || "false".equalsIgnoreCase(value)
                        || "alternating".equalsIgnoreCase(value)) {

                    droga.jednokierunkowa = "false";

                } else {
                    droga.jednokierunkowa = "false";
                    System.out.println("Droga ID=" + droga.ID + " ma nietypowy tag oneway: " + value);
                }
            }

            if ("junction".equals(key) && "roundabout".equalsIgnoreCase(value)) {
                droga.jednokierunkowa = "true";
            }

            if ("name".equals(key)) {
                droga.nazwa = value;
            }

            if ("maxspeed".equals(key)) {
                try {
                    if (value.equalsIgnoreCase("walk")) {
                        droga.maxspeed = 7;
                    } else {
                        droga.maxspeed = Integer.parseInt(value);
                    }
                } catch (NumberFormatException e) {
                    droga.maxspeed = -1;
                }
            }
        }
    }

    private Droga utworzDrogeZElementuWay(Element elemWay) {
        long idOSM = Long.parseLong(elemWay.getAttribute("id"));

        Droga droga = new Droga(idOSM);

        ustawTagiDrogi(droga, elemWay);

        NodeList nds = elemWay.getElementsByTagName("nd");

        for (int j = 0; j < nds.getLength(); j++) {
            Element nd = (Element) nds.item(j);

            if (!nd.hasAttribute("ref") || nd.getAttribute("ref").isBlank()) {
                continue;
            }

            long ref = Long.parseLong(nd.getAttribute("ref"));

            Punkt p = allNodes.get(ref);

            if (p == null) {
                System.out.println("Brak node o ID: " + ref + " dla way: " + elemWay.getAttribute("id"));
                continue;
            }

            if (!czyPunktWGranicach(p)) {
                continue;
            }
            p.ustawXY();
            droga.punkty.add(p);
        }

        return droga;
    }

    private void pobierzTF() {
        System.out.println("[INFO] Start pobierania danych z HERE Traffic API v7...");

        pasekPostepuTF.setIndeterminate(true);

        double minLat = DANE._2_S_minLAT;
        double maxLat = DANE._4_N_maxLAT;
        double minLon = DANE._1_W_minLON;
        double maxLon = DANE._3_E_maxLON;

        String endpoint = "https://data.traffic.hereapi.com/v7/flow"
                + "?in=bbox:" + minLon + "," + minLat + "," + maxLon + "," + maxLat
                + "&locationReferencing=shape"
                + "&apiKey=" + API_KEY;

        try {
            System.out.println("[INFO] Przygotowany URL:");
            System.out.println(endpoint);

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            System.out.println("[INFO] Połączenie otwarte...");

            int code = conn.getResponseCode();

            System.out.println("[INFO] HTTP response = " + code);

            if (code != 200) {
                System.out.println("[ERROR] Pobieranie nie powiodło się, kod odpowiedzi: " + code);
                pasekPostepuTF.setIndeterminate(false);
                pasekPostepuTF.setString("Błąd pobierania");
                return;
            }

            InputStream in = new BufferedInputStream(conn.getInputStream());

            FileOutputStream out = new FileOutputStream(
                    new File("POBRANE_PLIKI", DANE.nazwaPliku + "_traffic.json")
            );

            System.out.println("[INFO] Zapisuję dane do pliku: " + DANE.nazwaPliku + "_traffic.json");

            byte[] buffer = new byte[4096];
            int bytesRead;

            int wielkoscPliku = conn.getContentLength();
            int pobraneBajty = 0;

            pasekPostepuTF.setIndeterminate(false);

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);

                pobraneBajty += bytesRead;

                int procent = 0;

                if (wielkoscPliku > 0) {
                    procent = (int) (100.0 * pobraneBajty / wielkoscPliku);
                }

                publish(new stanRealTime(pobraneBajty, procent, 3));

                System.out.println("[DEBUG] Pobrano " + procent + " %");
            }

            in.close();
            out.close();
            conn.disconnect();

            pasekPostepuTF.setIndeterminate(false);
            pasekPostepuTF.setValue(100);
            pasekPostepuTF.setString("Pobieranie zakończone");

            System.out.println("[INFO] Zakończono pobieranie.");
            System.out.println("[INFO] Łącznie zapisano: " + pobraneBajty + " bajtów.");

        } catch (Exception e) {
            System.out.println("[ERROR] Wystąpił wyjątek PobierzTF!");
            e.printStackTrace();

            pasekPostepuTF.setIndeterminate(false);
            pasekPostepuTF.setString("Błąd pobierania");
        }
    }

    private void czytajTF() throws IOException, JSONException {
        ruchUliczny.clear();

        int ilosc = 0;

        String content = Files.readString(
                Paths.get("POBRANE_PLIKI/" + DANE.nazwaPliku + "_traffic.json")
        );

        JSONObject root = new JSONObject(content);
        JSONArray results = root.getJSONArray("results");

        int iloscWszystkich = results.length();

        for (int i = 0; i < results.length(); i++) {
            JSONObject obj = results.getJSONObject(i);

            JSONObject loc = obj.getJSONObject("location");
            JSONObject flow = obj.getJSONObject("currentFlow");

            String street = loc.optString("description", "");
            String id = loc.optString("id", "");
            double length = loc.optDouble("length", Double.NaN);
            int funcClass = loc.optInt("functionalClass", -1);

            double speed = flow.optDouble("speed", Double.NaN);
            double freeFlow = flow.optDouble("freeFlow", Double.NaN);
            double jam = flow.optDouble("jamFactor", Double.NaN);
            double conf = flow.optDouble("confidence", Double.NaN);

            List<Punkt> points = parseShape(loc);

            ilosc++;
            publish(new stanRealTime(ilosc, procent(ilosc, iloscWszystkich), 4));

            ruchUliczny.add(new TrafficSegment(
                    street,
                    id,
                    length,
                    funcClass,
                    speed,
                    freeFlow,
                    jam,
                    conf,
                    points
            ));
        }
    }

    private List<Punkt> parseShape(JSONObject loc) {
        List<Punkt> pts = new ArrayList<>();

        if (loc.has("shape") && loc.get("shape") instanceof JSONObject) {
            JSONObject shp = loc.getJSONObject("shape");

            if (shp.has("links") && shp.get("links") instanceof JSONArray) {
                JSONArray links = shp.getJSONArray("links");

                for (int i = 0; i < links.length(); i++) {
                    JSONObject link = links.getJSONObject(i);

                    if (link.has("points") && link.get("points") instanceof JSONArray) {
                        JSONArray arr = link.getJSONArray("points");

                        for (int j = 0; j < arr.length(); j++) {
                            JSONObject p = arr.getJSONObject(j);

                            double lat = p.optDouble("lat", Double.NaN);
                            double lon = p.has("lng")
                                    ? p.optDouble("lng", Double.NaN)
                                    : p.optDouble("lon", Double.NaN);

                            if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                                pts.add(new Punkt(lat, lon));
                            }
                        }
                    }
                }

                return pts;
            }
        }

        if (loc.has("shape") && loc.get("shape") instanceof JSONArray) {
            JSONArray arr = loc.getJSONArray("shape");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject p = arr.getJSONObject(i);

                double lat = p.optDouble("lat", Double.NaN);
                double lon = p.has("lng")
                        ? p.optDouble("lng", Double.NaN)
                        : p.optDouble("lon", Double.NaN);

                if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                    pts.add(new Punkt(lat, lon));
                }
            }

            return pts;
        }

        String shapeStr = loc.optString("shape", "").trim();

        if (!shapeStr.isEmpty()) {
            String[] t = shapeStr.replace(',', '.').split("\\s+");

            for (int i = 0; i + 1 < t.length; i += 2) {
                try {
                    double lat = Double.parseDouble(t[i]);
                    double lon = Double.parseDouble(t[i + 1]);

                    pts.add(new Punkt(lat, lon));

                } catch (NumberFormatException ignore) {
                }
            }
        }

        return pts;
    }

    private boolean czyPunktWGranicach(Punkt p) {
        return p.LAT >= DANE._2_S_minLAT
                && p.LAT <= DANE._4_N_maxLAT
                && p.LON >= DANE._1_W_minLON
                && p.LON <= DANE._3_E_maxLON;
    }

    private int procent(int wartosc, int calosc) {
        if (calosc <= 0) {
            return 0;
        }

        return (int) (100.0 * wartosc / calosc);
    }
}
