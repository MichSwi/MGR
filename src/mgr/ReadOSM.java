package mgr;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;


// ---- Parser ----
public class ReadOSM {

    private Map<Long, Punkt> allNodes = new HashMap<>();
    private List<Droga> ways = new ArrayList<>();

    public void readOSM(String fileName) {
        try {
            System.out.println("➡ Otwieram plik: " + fileName);
            File file = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            // --- NODES (z tagami jeśli są) ---
            NodeList nList = doc.getElementsByTagName("node");
            for (int i = 0; i < nList.getLength(); i++) {
                Element e = (Element) nList.item(i);
                long id = Long.parseLong(e.getAttribute("id"));
                double lat = Double.parseDouble(e.getAttribute("lat"));
                double lon = Double.parseDouble(e.getAttribute("lon"));
                //OSMNode node = new OSMNode(id, lat, lon);
                Punkt Punkt = new Punkt(lat,lon,TypPunkt.DROGA_PKT, id);

                // sprawdź tagi w node
                NodeList tags = e.getElementsByTagName("tag");
                for (int t = 0; t < tags.getLength(); t++) {
                    Element tag = (Element) tags.item(t);
                    Punkt.tags.put(tag.getAttribute("k"), tag.getAttribute("v"));
                }

                allNodes.put(id, Punkt);
            }
            System.out.println("➡ Wszystkich node: " + allNodes.size());

            // --- WAYS (z punktami i tagami) ---
            NodeList wList = doc.getElementsByTagName("way");
            for (int i = 0; i < wList.getLength(); i++) {
                Element w = (Element) wList.item(i);
                long id = Long.parseLong(w.getAttribute("id"));
                Droga way = new Droga(id);

                // punkty z ref
                NodeList nds = w.getElementsByTagName("nd");
                for (int j = 0; j < nds.getLength(); j++) {
                    Element nd = (Element) nds.item(j);
                    long ref = Long.parseLong(nd.getAttribute("ref"));
                    Punkt node = allNodes.get(ref);
                    if (node != null) {
                        way.punkty.add(node);
                    }
                }

                // tagi dla way
                NodeList tags = w.getElementsByTagName("tag");
                for (int k = 0; k < tags.getLength(); k++) {
                    Element tag = (Element) tags.item(k);
                    way.tags.put(tag.getAttribute("k"), tag.getAttribute("v"));
                }

                ways.add(way);
            }
            System.out.println("➡ Wczytano dróg: " + ways.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gettery
    public List<Droga> getWays() { return ways; }
    public Map<Long, Punkt> getNodes() { return allNodes;}
}
