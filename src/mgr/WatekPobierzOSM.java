package mgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import java.util.List;
import java.util.Locale;

public class WatekPobierzOSM extends SwingWorker<Void, Integer> {

    long pobraneBajty;
    JProgressBar pasekPostepuOSM;
    private final double S, W, N, E;

    public WatekPobierzOSM(JProgressBar pasekPostepuOSM, double S, double W, double N, double E) {
        this.pasekPostepuOSM = pasekPostepuOSM;
        this.S = S;
        this.W = W;
        this.N = N;
        this.E = E;
        pobraneBajty = 0;
        this.pasekPostepuOSM.setIndeterminate(true);
    }

    @Override
    protected Void doInBackground() throws Exception {
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
                        pobraneBajty = pobraneBajty + r;

                        //if (pobrane % 1000 == 0) { // jeśli znamy długość pliku
                        int procent = (int) (pobraneBajty);
                        publish(procent); // <<< TUTAJ wysyłasz postęp do process()
                        //}
                    }
                }

                if (code >= 200 && code < 300) {
                    System.out.println("✅ Gotowe: " + out.length() + " bajtów");
                } else {
                    System.err.println("⚠ Overpass HTTP " + code + " – spróbuję kolejny mirror.");
                }
            } catch (Exception e) {
                System.err.println("⚠ Błąd połączenia z " + endpoint + ": " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        int ostatnia = chunks.get(chunks.size() - 1);
        String tekst;

        if (ostatnia < 1024) {
            tekst = "Pobrano: " + ostatnia + " B";
        } else if (ostatnia < 1024 * 1024) {
            tekst = "Pobrano: " + (ostatnia / 1024) + " KB";
        } else {
            tekst = "Pobrano: " + (ostatnia / (1024 * 1024)) + " MB, " + ostatnia % (1024 * 1024) / 1024 + " KB";
        }
        pasekPostepuOSM.setString(tekst);
    }

    @Override
    protected void done() {
        // działa w GUI po zakończeniu pracy

        JOptionPane.showMessageDialog(null, "Pobieranie zakończone!");
    }

}
