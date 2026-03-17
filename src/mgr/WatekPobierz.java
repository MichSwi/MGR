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
    int tryb;

    //czytanie osm
    private Map<Long, Punkt> allNodes = new HashMap<>();

    //czytanie TF
    private List<TrafficSegment> ruchUliczny = new ArrayList<>();

    infoTXT info = new infoTXT(DANE.nazwaPliku, DANE._4_N_WYS, DANE._3_E_SZER_R, DANE._1_W_LAT, DANE._2_S_LON, tryb);

    public WatekPobierz(
            List<Droga> drogi,
            Map<Long, Punkt> punktyLista,
            JProgressBar pasekPostepuOSM, JProgressBar pasekPostepuTF,
            JProgressBar pasekPostepuCzytajOSM, JProgressBar pasekPostepuCzytajTF,
            int tryb) {

        this.punktyLista = punktyLista;
        this.pasekPostepuOSM = pasekPostepuOSM;
        this.pasekPostepuTF = pasekPostepuTF;
        this.pasekPostepuCzytajOSM = pasekPostepuCzytajOSM;
        this.pasekPostepuCzytajTF = pasekPostepuCzytajTF;
        this.tryb = tryb;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //pobieranie OSM
        if (DANE.coZaznaczone.get(0)) {
            pobierzOSM();
        }

        //czytanie OSM
        if (DANE.coZaznaczone.get(1)) {
            info.wczytajPlik(DANE.nazwaPliku + ".txt");
            DANE._1_W_LAT = info.get1_W_LAT();
            DANE._2_S_LON = info.get2_S_LON();
            DANE._3_E_SZER_R = info.get3_E_SZER_R();
            DANE._4_N_WYS = info.get4_N_WYS();
            
            System.out.println("W=" + DANE._1_W_LAT);
System.out.println("S=" + DANE._2_S_LON);
System.out.println("E=" + DANE._3_E_SZER_R);
System.out.println("N=" + DANE._4_N_WYS);
            
            czytajOSM();
        }

        //pobieranie HERE
        if (DANE.coZaznaczone.get(2)) {
            pobierzTF();
        }

        //czytanie HERE
        if (DANE.coZaznaczone.get(3)) {
            czytajTF();
        }

        //zapisz dodatkowe info
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
            //POBIERANIE OSM
            if (wartosc < 1024) {
                tekst = "Pobrano: " + wartosc + " B";
            } else if (wartosc < 1024 * 1024) {
                tekst = "Pobrano: " + (wartosc / 1024) + " KB";
            } else {
                tekst = "Pobrano: " + (wartosc / (1024 * 1024)) + " MB, " + wartosc % (1024 * 1024) / 1024 + " KB";
            }
            pasekPostepuOSM.setString(tekst);
        } else if (numerPaska == 2) {
            pasekPostepuCzytajOSM.setString("Wczytano: " + wartosc);
            pasekPostepuCzytajOSM.setValue(procent);
        } else if (numerPaska == 3) {
            if (wartosc < 1024) {
                tekst = "Pobrano: " + wartosc + " B";
            } else if (wartosc < 1024 * 1024) {
                tekst = "Pobrano: " + (wartosc / 1024) + " KB";
            } else {
                tekst = "Pobrano: " + (wartosc / (1024 * 1024)) + " MB, " + wartosc % (1024 * 1024) / 1024 + " KB";
            }
            pasekPostepuTF.setString(tekst);
            pasekPostepuTF.setValue(wartosc);
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

        DANE.ustawOdleglosci();
        DANE.ustawStartKoniec();
        DANE.ustawPolaczenia();
        DANE.ustawRuchUliczny();
        DANE.budujWezlyZDrog();

        System.out.println("DONE");
    }

    ////// FUNKCJE
    private void pobierzOSM() {
        pasekPostepuOSM.setIndeterminate(true);

        String query = "";
        // 1 - granice  2 - obszar  3 - kolo
        if (tryb == 1) {
            query = String.format(Locale.US,
                    "[out:xml][timeout:60];"
                    + "("
                    + "way[\"highway\"]"
                    + "[\"highway\"!~\"^(footway|path|cycleway|bridleway|steps|pedestrian|track|elevator|platform|service|driveway)$\"]"
                    + "(%.6f,%.6f,%.6f,%.6f);"
                    + ");"
                    + "(._;>;);"
                    + "out body;",
                    DANE._2_S_LON, DANE._1_W_LAT, DANE._4_N_WYS, DANE._3_E_SZER_R
            );

//query = String.format(Locale.US,
//    "[out:xml][timeout:60];"
//  + "("
//  + "way(53.8140,20.6760,53.8440,20.7060);"
//  + "node(53.8140,20.6760,53.8440,20.7060);" BARCZEWO WSZYSTKO
//  + "relation(53.8140,20.6760,53.8440,20.7060);"
//  + ");"
//  + "(._;>;);"
//  + "out body;"
//);
        } else if (tryb == 2) {
            query = String.format(Locale.US,
                    "[out:xml][timeout:60];"
                    + "way[\"highway\"](around:%d,%.6f,%.6f);" // promień, lat, lon
                    + "(._;>;);"
                    + "out body;",
                    1000, 52.2297, 21.0122
            );
        } else if (tryb == 3) {

        
        ///////////////////////////wpisz url query
        }

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
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                conn.setRequestProperty("User-Agent", "MGR-Downloader/1.0 (Java)");

                int code = conn.getResponseCode();
                InputStream in = (code >= 200 && code < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream(); // pokaż treść błędu jeśli 4xx/5xx

                int pobraneBajty = 0;
                try (in; FileOutputStream fos = new FileOutputStream(out)) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = in.read(buf)) != -1) {
                        fos.write(buf, 0, r);
                        pobraneBajty = pobraneBajty + 1;
                        publish(new stanRealTime(pobraneBajty, 0, 1));
                    }
                }

                if (code >= 200 && code < 300) {
                    System.out.println("Gotowe: " + out.length() + " bajtów");
                    pasekPostepuOSM.setIndeterminate(false);
                    pasekPostepuOSM.setValue(100);
                    pasekPostepuOSM.setString("Pobieranie zakończone");
                    return; // sukces – kończymy
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
        //setLabel "konwertowanie zmiennych"
        allNodes.clear();
        DANE.drogi.clear();
        DANE.wezly.clear();
        DANE.ALG_SCIEZKA.clear();

        try {
            int ilosc = 0;
            System.out.println("Otwieram plik: " + DANE.nazwaPliku + ".osm");
            File file = new File("POBRANE_PLIKI/" + DANE.nazwaPliku + ".osm");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            // --- NODES (z tagami jeśli są) ---
            NodeList nList = doc.getElementsByTagName("node");
            NodeList wList = doc.getElementsByTagName("way");
            int ilosc_wszystkich = nList.getLength() + wList.getLength();
            for (int i = 0; i < nList.getLength(); i++) {
                Element e = (Element) nList.item(i);

                long id = Long.parseLong(e.getAttribute("id"));
                double lat = Double.parseDouble(e.getAttribute("lat"));
                double lon = Double.parseDouble(e.getAttribute("lon"));

                Punkt p = new Punkt(lat, lon, TypPunkt.DROGA_PKT, id);

                NodeList tags = e.getElementsByTagName("tag");
                for (int t = 0; t < tags.getLength(); t++) {
                    Element tag = (Element) tags.item(t);
                    p.tags.put(tag.getAttribute("k"), tag.getAttribute("v"));
                }

                allNodes.put(id, p);
                ilosc++;
                publish(new stanRealTime(ilosc, (int) (100.0 * ilosc / ilosc_wszystkich), 2));
            }

            System.out.println("➡ Wszystkich node: " + allNodes.size());

            // ===== policz, ile różnych WAY używa danego node =====
            Map<Long, Integer> nodeWayLicznik = new HashMap<>();

            for (int i = 0; i < wList.getLength(); i++) {
                Element elemWay = (Element) wList.item(i);
                NodeList nds = elemWay.getElementsByTagName("nd");

                Set<Long> unikalneRefWWay = new HashSet<>();

                for (int j = 0; j < nds.getLength(); j++) {
                    Element nd = (Element) nds.item(j);
                    long ref = Long.parseLong(nd.getAttribute("ref"));
                    if (unikalneRefWWay.add(ref)) {
                        nodeWayLicznik.merge(ref, 1, Integer::sum);
                    }
                }
            }

// ===== WAYS budowanie segmentow i ciecie w wezlach =====
            for (int i = 0; i < wList.getLength(); i++) {
                int licznikSegmentow = 0;
                Element elemWay = (Element) wList.item(i);
                long idOSM = Long.parseLong(elemWay.getAttribute("id"));

                // pierwszy segment
                long idSeg = idOSM * 100L + licznikSegmentow;
                Droga biezaca = new Droga(idSeg);

                // jednokierunkowosc: wstepnie false
                biezaca.jednokierunkowa = "false";

                // tagi
                NodeList tagi = elemWay.getElementsByTagName("tag");
                for (int k = 0; k < tagi.getLength(); k++) {
                    Element tag = (Element) tagi.item(k);

                    String key = tag.getAttribute("k");
                    String value = tag.getAttribute("v");

                    biezaca.tags.put(key, value);

                    if ("oneway".equals(key)) {
                        if ("yes".equalsIgnoreCase(value) || "1".equals(value) || "true".equalsIgnoreCase(value)) {
                            biezaca.jednokierunkowa = "true";
                        } else if ("-1".equalsIgnoreCase(value)) {
                            biezaca.jednokierunkowa = "-1";
                        } else if ("no".equalsIgnoreCase(value) || "0".equals(value) || "false".equalsIgnoreCase(value)) {
                            biezaca.jednokierunkowa = "false";
                        } else {
                            throw new IllegalArgumentException(biezaca + " ma cos dziwnego w tags oneway");
                        }
                    }

                    // jesli rondo -> dorga jest jednokierunkowa
                    if ("junction".equals(key) && "roundabout".equalsIgnoreCase(value)) {
                        biezaca.jednokierunkowa = "true";
                    }

                    if ("name".equals(key)) {
                        biezaca.nazwa = value;
                    }

                    if ("maxspeed".equals(key)) {
                        try {
                            biezaca.maxspeed = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            biezaca.maxspeed = -1;
                        }
                    }
                }

                // punkty
                NodeList nds = elemWay.getElementsByTagName("nd");
                for (int j = 0; j < nds.getLength(); j++) {
                    Element nd = (Element) nds.item(j);
                    long ref = Long.parseLong(nd.getAttribute("ref"));
                    Punkt wezel = allNodes.get(ref);
                    if (wezel == null) {
                        continue;
                    }

                    wezel.ustawXY();

                    boolean czyWspolnyNode = nodeWayLicznik.getOrDefault(ref, 0) >= 2;
                    boolean czyPoczatekLubKoniec = (j != 0 && j != nds.getLength() - 1);

                    // ciecie na wezle wspolnym jesli to punk nie koniec i nie poczatek
                    if (czyWspolnyNode && czyPoczatekLubKoniec) {

                        // zamkniecie starego segmentu
                        if (!biezaca.punkty.isEmpty()) {
                            biezaca.punkty.add(wezel);
                            DANE.drogi.put(biezaca.ID, biezaca);
                            ilosc++;
                            publish(new stanRealTime(ilosc, (int) (100.0 * ilosc / ilosc_wszystkich), 2));
                        }

                        // nowy segment (węzeł jako POCZĄTEK)
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

                // zapisz ostatni segment
                if (!biezaca.punkty.isEmpty()) {
                    DANE.drogi.put(biezaca.ID, biezaca);
                    ilosc++;
                    publish(new stanRealTime(ilosc, (int) (100.0 * ilosc / ilosc_wszystkich), 2));
                }
            }
            System.out.println("============");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pobierzTF() {
        System.out.println("[INFO] Start pobierania danych z HERE Traffic API v7...");
        pasekPostepuTF.setIndeterminate(true);
        double minLat = 0;
        double maxLat = 0;
        double maxLon = 0;
        double minLon = 0;

        if (tryb == 2) {
            double lat = DANE._1_W_LAT; //polnoc poludnie
            double lon = DANE._2_S_LON; //wschod zachod
            double offset_LON = DANE._3_E_SZER_R / 2;
            double offset_LAT = DANE._4_N_WYS / 2;

            // Wyliczenie granic prostokąta
            minLat = lat - offset_LAT;
            maxLat = lat + offset_LAT;
            minLon = lon - offset_LON;
            maxLon = lon + offset_LON;

        } else if (tryb == 1) {
            minLat = DANE._2_S_LON;
            maxLat = DANE._4_N_WYS;
            minLon = DANE._1_W_LAT;
            maxLon = DANE._3_E_SZER_R;
        }//1 - granice  2 - obszar  3 - kolo

        String endpoint = "https://data.traffic.hereapi.com/v7/flow"
                + "?in=bbox:" + minLon + "," + minLat + "," + maxLon + "," + maxLat
                + "&locationReferencing=shape"
                + "&apiKey=" + API_KEY;

        try {
            // Tworzymy bbox w formacie: minLon,minLat,maxLon,maxLat
            //String bbox = minLon + "," + minLat + "," + maxLon + "," + maxLat;

            // Endpoint API v7 (JSON domyślnie)
//            String endpoint = "https://data.traffic.hereapi.com/v7/flow"
//                    + "?in=bbox:" + bbox
//                    + "&locationReferencing=olr"
//                    + "&apiKey=" + API_KEY;
//endpoint = "https://data.traffic.hereapi.com/v7/flow?in=bbox:13.37,52.50,13.40,52.52&apiKey=" + API_KEY;
            //String endpoint = "https://data.traffic.hereapi.com/v7/flow?in=bbox:13.386969,52.527129,13.424134,52.549420&locationReferencing=shape&apiKey=" + API_KEY;
            System.out.println("[INFO] Przygotowany URL:");
            System.out.println(endpoint);

            // Otwieramy połączenie HTTP
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            System.out.println("[INFO] Połączenie otwarte...");

            // zapisujemy ilosc pobran
            // Sprawdzamy kod odpowiedzi
            int code = conn.getResponseCode();
            System.out.println("[INFO] HTTP response = " + code);

            if (code != 200) {
                System.out.println("[ERROR] Pobieranie nie powiodło się, kod odpowiedzi: " + code);
                return;
            }

            // Pobieramy dane do pliku
            InputStream in = new BufferedInputStream(conn.getInputStream());
            FileOutputStream out = new FileOutputStream(new File("POBRANE_PLIKI", DANE.nazwaPliku + ".xml"));

            System.out.println("[INFO] Zapisuję dane do pliku: " + DANE.nazwaPliku);

            byte[] buffer = new byte[4096];
            int bytesRead;

            int wielkoscPliku = conn.getContentLength();
            int pobraneBajty = 0;
            pasekPostepuTF.setIndeterminate(false);
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                pobraneBajty += bytesRead;
                publish(new stanRealTime(pobraneBajty, (int) (100.0 * pobraneBajty / wielkoscPliku), 3));
                System.out.println("[DEBUG] Pobrano " + (int) (100.0 * pobraneBajty / wielkoscPliku) + " %");
            }

            // Zamykamy strumienie
            in.close();
            out.close();
            conn.disconnect();

            pasekPostepuTF.setIndeterminate(false);
            pasekPostepuTF.setValue(100);
            pasekPostepuTF.setString("Pobieranie zakończone");
            System.out.println("[INFO] Zakończono pobieranie.");
            System.out.println("[INFO] Łącznie zapisano: " + wielkoscPliku + " bajtów.");

        } catch (Exception e) {
            System.out.println("[ERROR] Wystąpił wyjątek PobierzTF!");
            e.printStackTrace();
        }
    }

    private void czytajTF() throws IOException, JSONException {
        int ilosc = 0;
        //String content = Files.readString(Path.of(filePath));
        String content = Files.readString(Paths.get("POBRANE_PLIKI/" + DANE.nazwaPliku + ".xml"));
        JSONObject root = new JSONObject(content);
        JSONArray results = root.getJSONArray("results");
        int ilosc_wszystkich = results.length();
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
            publish(new stanRealTime(ilosc, (int) (100.0 * ilosc / ilosc_wszystkich), 4));

            ruchUliczny.add(new TrafficSegment(street, id, length, funcClass,
                    speed, freeFlow, jam, conf, points));
        }
    }

    private List<Punkt> parseShape(JSONObject loc) {
        List<Punkt> pts = new ArrayList<>();

        // 1) NOWE: shape jako obiekt z links[].points[]
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
                            double lon = p.has("lng") ? p.optDouble("lng", Double.NaN)
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

        // 2) shape jako tablica punktów [{lat,lng},...]
        if (loc.has("shape") && loc.get("shape") instanceof JSONArray) {
            JSONArray arr = loc.getJSONArray("shape");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject p = arr.getJSONObject(i);
                double lat = p.optDouble("lat", Double.NaN);
                double lon = p.has("lng") ? p.optDouble("lng", Double.NaN)
                        : p.optDouble("lon", Double.NaN);
                if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                    pts.add(new Punkt(lat, lon));
                }
            }
            return pts;
        }

        // 3) shape jako string "lat lon lat lon ..."
        String shapeStr = loc.optString("shape", "").trim();
        if (!shapeStr.isEmpty()) {
            String[] t = shapeStr.replace(',', '.').split("\\s+");
            for (int i = 0; i + 1 < t.length; i += 2) {
                try {
                    pts.add(new Punkt(Double.parseDouble(t[i]), Double.parseDouble(t[i + 1])));
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return pts;
    }

//    private void zapiszInfoTXT() {
//        try (PrintWriter pw = new PrintWriter(new FileWriter("POBRANE_PLIKI\\" + nazwaPliku))) {
//
//            LocalDateTime teraz = LocalDateTime.now();
//
//            String godzina = teraz.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//            String data = teraz.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//            pw.println("" + data);
//            pw.println("" + godzina);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
