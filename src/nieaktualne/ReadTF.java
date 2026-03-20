// src/mgr/ReadTF.java
package nieaktualne;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import mgr.Punkt;
import mgr.TrafficSegment;
import org.json.*;

public class ReadTF {

    public static List<TrafficSegment> read(String filePath) throws IOException, JSONException {
        String content = Files.readString(Path.of(filePath));
        JSONObject root = new JSONObject(content);
        JSONArray results = root.getJSONArray("results");

        List<TrafficSegment> out = new ArrayList<>();
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

            out.add(new TrafficSegment(street, id, length, funcClass,
                    speed, freeFlow, jam, conf, points));
        }
        return out;
    }

    // Obsługa dwóch formatów: string "lat lon lat lon ..." oraz tablica [{lat,lng}, ...]
    // ReadTF.java
// ReadTF.java
    private static List<Punkt> parseShape(JSONObject loc) {
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
}
