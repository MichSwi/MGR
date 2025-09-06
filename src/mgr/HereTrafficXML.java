package mgr;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Klasa do pobierania danych Traffic Flow z HERE API v7 Dla prostokąta (bbox)
 * wokół Elbląga
 */
public class HereTrafficXML {

    // Wstaw swój klucz API
    private static final String API_KEY = "opXIV-5dVqM7Ia_DQmr9L2giM4hKSk07FwlE7zi2_h0";

    public static void pobierz() {
        System.out.println("[INFO] Start pobierania danych z HERE Traffic API v7...");

        // Współrzędne Elbląga (środek)
        double lat = 54.152;
        double lon = 19.408;
        double offset = 0.005; // ~0.5 km w każdą stronę -> bbox ~1 km x 1 km

        // Wyliczenie granic prostokąta
        double minLat = lat - offset;
        double maxLat = lat + offset;
        double minLon = lon - offset;
        double maxLon = lon + offset;

        try {
            // Tworzymy bbox w formacie: minLon,minLat,maxLon,maxLat
            String bbox = minLon + "," + minLat + "," + maxLon + "," + maxLat;

            // Endpoint API v7 (JSON domyślnie)
//            String endpoint = "https://data.traffic.hereapi.com/v7/flow"
//                    + "?in=bbox:" + bbox
//                    + "&locationReferencing=olr"
//                    + "&apiKey=" + API_KEY;
//endpoint = "https://data.traffic.hereapi.com/v7/flow?in=bbox:13.37,52.50,13.40,52.52&apiKey=" + API_KEY;
            String endpoint = "https://data.traffic.hereapi.com/v7/flow?in=bbox:13.386969,52.527129,13.424134,52.549420&locationReferencing=shape&apiKey=" + API_KEY;
            System.out.println("[INFO] Przygotowany URL:");
            System.out.println(endpoint);

            // Otwieramy połączenie HTTP
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            System.out.println("[INFO] Połączenie otwarte...");

            // Sprawdzamy kod odpowiedzi
            int code = conn.getResponseCode();
            System.out.println("[INFO] HTTP response = " + code);

            if (code != 200) {
                System.out.println("[ERROR] Pobieranie nie powiodło się, kod odpowiedzi: " + code);
                return;
            }

            // Pobieramy dane do pliku
            InputStream in = new BufferedInputStream(conn.getInputStream());
            FileOutputStream out = new FileOutputStream("nowy28.08.xml");

            System.out.println("[INFO] Zapisuję dane do pliku traffic_elblag_v7.json...");

            byte[] buffer = new byte[4096];
            int bytesRead;
            int total = 0;

            int totalSize = conn.getContentLength();

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                total += bytesRead;
                System.out.println("[DEBUG] Pobrano " + total + " bajtów...");
            }

            // Zamykamy strumienie
            in.close();
            out.close();
            conn.disconnect();

            System.out.println("[INFO] Zakończono pobieranie.");
            System.out.println("[INFO] Łącznie zapisano: " + total + " bajtów.");

        } catch (Exception e) {
            System.out.println("[ERROR] Wystąpił wyjątek!");
            e.printStackTrace();
        }
    }
}
