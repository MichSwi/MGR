package mgr;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class DownloadOSM {

    // BBOX: minLat, minLon, maxLat, maxLon – mały kawałek Elbląga (zmień wg potrzeb)

//    public static DownloadOSM(double S, double W, double N, double E) {
//        this.S = S;
//        this.W = W;
//        this.N = N;
//        this.E = E;
//        download(); // uruchom od razu; możesz też wywołać metodę ręcznie
//    }

    public static void download(double S, double W, double N, double E) {
        
        // Zapytanie Overpass: tylko drogi (highway) w bbox – mało danych i szybko
        String query = String.format(Locale.US,
                "[out:xml][timeout:60];"
                + "way[\"highway\"](%.6f,%.6f,%.6f,%.6f);"
                + "(._;>;);"
                + // dołącz node należące do way
                "out body;",
                S, W, N, E
        );

        String[] endpoints = new String[]{
            "https://overpass-api.de/api/interpreter",
            "https://overpass.kumi.systems/api/interpreter",
            "https://overpass.openstreetmap.fr/api/interpreter"
        };

        File out = new File("elblag_small.osm");
        System.out.println("➡ Zapisuję do: " + out.getAbsolutePath());

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

                try (in; FileOutputStream fos = new FileOutputStream(out)) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = in.read(buf)) != -1) {
                        fos.write(buf, 0, r);
                    }
                }

                if (code >= 200 && code < 300) {
                    System.out.println("✅ Gotowe: " + out.length() + " bajtów");
                    return; // sukces – kończymy
                } else {
                    System.err.println("⚠ Overpass HTTP " + code + " – spróbuję kolejny mirror.");
                }
            } catch (Exception e) {
                System.err.println("⚠ Błąd połączenia z " + endpoint + ": " + e.getMessage());
            }
        }

        System.err.println("❌ Nie udało się pobrać – spróbuj mniejszego bboxa lub później.");
    }
}
