package nieaktualne;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.FileWriter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Konwertuje plik traffic_elblag_v7.xml (HERE Traffic API v7, format=xml)
 * na poprawny GeoJSON (LineString), który można otworzyć w QGIS.
 */
public class HereXMLtoGeoJSON {

    public static void main(String[] args) {
        konwertuj("traffic_elblag_v7.xml", "traffic_elblag_v7.geojson");
    }

    public static void konwertuj(String inputFile, String outputFile) {
        try {
            // wczytanie XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(inputFile));
            doc.getDocumentElement().normalize();

            // budujemy FeatureCollection
            JSONObject featureCollection = new JSONObject();
            featureCollection.put("type", "FeatureCollection");
            JSONArray features = new JSONArray();

            NodeList results = doc.getElementsByTagName("results");
            for (int i = 0; i < results.getLength(); i++) {
                Element res = (Element) results.item(i);

                // opis drogi
                String description = "";
                NodeList descNodes = res.getElementsByTagName("description");
                if (descNodes.getLength() > 0) {
                    description = descNodes.item(0).getTextContent();
                }

                // shape -> coordinates [lng, lat]
                JSONArray coords = new JSONArray();
                NodeList points = res.getElementsByTagName("point");
                for (int j = 0; j < points.getLength(); j++) {
                    Element pt = (Element) points.item(j);
                    double lat = Double.parseDouble(pt.getAttribute("lat"));
                    double lng = Double.parseDouble(pt.getAttribute("lng"));

                    JSONArray xy = new JSONArray();
                    xy.put(lng);
                    xy.put(lat);
                    coords.put(xy);
                }

                if (coords.length() == 0) continue;

                // geometry
                JSONObject geom = new JSONObject();
                geom.put("type", "LineString");
                geom.put("coordinates", coords);

                // properties
                JSONObject props = new JSONObject();
                props.put("description", description);

                NodeList flowNodes = res.getElementsByTagName("currentFlow");
                if (flowNodes.getLength() > 0) {
                    Element flow = (Element) flowNodes.item(0);
                    props.put("speed", flow.getAttribute("speed"));
                    props.put("jamFactor", flow.getAttribute("jamFactor"));
                }

                // feature
                JSONObject feature = new JSONObject();
                feature.put("type", "Feature");
                feature.put("geometry", geom);
                feature.put("properties", props);

                features.put(feature);
            }

            featureCollection.put("features", features);

            // zapis do pliku
            try (FileWriter fw = new FileWriter(outputFile)) {
                fw.write(featureCollection.toString(2));
            }

            System.out.println("[INFO] Zapisano plik " + outputFile + " (otwórz w QGIS)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
