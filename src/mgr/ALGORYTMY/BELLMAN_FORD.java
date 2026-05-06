package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;

/**
 *
 * @author Michal
 */
public class BELLMAN_FORD {

    private Long pkt_start = DANE.wezelStartowyAlgorytmu.ID;
    private Long pkt_koniec = DANE.wezelKoncowyAlgorytmu.ID;
    private Map<Long, Wezel> wezly = DANE.wezly;

    private ArrayList<Droga> najlepsza_trasa = new ArrayList<>();

    private static final double INF = Double.MAX_VALUE;

    public BELLMAN_FORD() {
    }

    public List<Droga> startAlg() {

        System.out.println("========== BELLMAN-FORD START ==========");

        Map<Long, Double> dystans = new HashMap<>();
        Map<Long, Long> poprzednik = new HashMap<>();
        Map<Long, Droga> drogaDoWezla = new HashMap<>();

        for (Long idWezla : wezly.keySet()) {
            dystans.put(idWezla, INF);
            poprzednik.put(idWezla, null);
        }

        dystans.put(pkt_start, 0.0);

        int liczbaWezlow = wezly.size();

        for (int i = 0; i < liczbaWezlow - 1; i++) {

            boolean czyBylaZmiana = false;
            int liczbaZmian = 0;

            for (Wezel w : wezly.values()) {

                double dystansDoAktualnego = dystans.getOrDefault(w.ID, INF);

                if (dystansDoAktualnego == INF) {
                    continue;
                }

                for (Long idDrogi : w.drogiIDs) {

                    Droga droga = DANE.drogi.get(idDrogi);

                    Long sasiedniWezel = droga.getPrzeciwnyWezelId(w.ID);

                    if (sasiedniWezel == null) {
                        continue;
                    }

                    if (!wezly.containsKey(sasiedniWezel)) {
                        continue;
                    }

                    double waga = getWagaDrogi(droga);

                    double nowyDystans = dystansDoAktualnego + waga;
                    double staryDystans = dystans.getOrDefault(sasiedniWezel, INF);

                    if (nowyDystans < staryDystans) {
                        dystans.put(sasiedniWezel, nowyDystans);
                        poprzednik.put(sasiedniWezel, w.ID);
                        drogaDoWezla.put(sasiedniWezel, droga);

                        czyBylaZmiana = true;
                        liczbaZmian++;
                    }
                }
            }

            if (!czyBylaZmiana) {
                System.out.println("Brak zmian w iteracji, koniec.");
                break;
            }
        }

        // Kontrola cyklu ujemnego
        for (Wezel w : wezly.values()) {

            double dystansDoAktualnego = dystans.getOrDefault(w.ID, INF);

            if (dystansDoAktualnego == INF) {
                continue;
            }

            for (Long idDrogi : w.drogiIDs) {

                Droga droga = DANE.drogi.get(idDrogi);

                if (droga == null) {
                    continue;
                }

                Long sasiedniWezel = droga.getPrzeciwnyWezelId(w.ID);

                if (sasiedniWezel == null) {
                    continue;
                }

                double waga = getWagaDrogi(droga);

                if (dystansDoAktualnego + waga < dystans.getOrDefault(sasiedniWezel, INF)) {
                    System.out.println("BLAD: Wykryto cykl o ujemnej wadze.");
                    return new ArrayList<>();
                }
            }
        }

        double dystansKonca = dystans.getOrDefault(pkt_koniec, INF);

        System.out.printf("Dystans do konca: %s%n",
                dystansKonca == INF ? "INF" : String.format("%.2f", dystansKonca));

        System.out.printf("Poprzednik konca: %s%n", poprzednik.get(pkt_koniec));

        if (dystansKonca == INF) {
            System.out.println("Nie znaleziono trasy.");

            int liczbaOsiagalnych = 0;

            for (Long id : dystans.keySet()) {
                if (dystans.get(id) != INF) {
                    liczbaOsiagalnych++;
                }
            }

            System.out.printf("Liczba wezlow osiagalnych ze startu: %d / %d%n",
                    liczbaOsiagalnych, wezly.size());

            return new ArrayList<>();
        }

        ArrayList<Long> trasaWezlow = new ArrayList<>();
        ArrayList<Droga> trasaDrog = new ArrayList<>();

        Long aktualny = pkt_koniec;

        int zabezpieczenie = 0;

        while (aktualny != null) {

            trasaWezlow.add(aktualny);

            Droga droga = drogaDoWezla.get(aktualny);

            if (droga != null) {
                trasaDrog.add(droga);
            }

            aktualny = poprzednik.get(aktualny);

            zabezpieczenie++;

            if (zabezpieczenie > wezly.size()) {
                System.out.println("BLAD: Odtwarzanie trasy przekroczylo liczbe wezlow.");
                break;
            }
        }

        Collections.reverse(trasaWezlow);
        Collections.reverse(trasaDrog);

        najlepsza_trasa = trasaDrog;

        System.out.println("Trasa wezlow:");
        for (Long id : trasaWezlow) {
            System.out.printf("%d -> ", id);
        }
        System.out.println("KONIEC");

        System.out.println("Trasa drog:");
        for (Droga d : najlepsza_trasa) {
            System.out.printf("Droga ID=%d, nazwa=%s, dlugosc=%.2f%n",
                    d.ID,
                    d.nazwa,
                    d.dlugosc
            );
        }

        System.out.printf("Dlugosc najkrotszej trasy: %.2f%n", dystans.get(pkt_koniec));
        System.out.printf("Liczba wezlow trasy: %d%n", trasaWezlow.size());
        System.out.printf("Liczba drog trasy: %d%n", najlepsza_trasa.size());

        System.out.println("========== BELLMAN-FORD KONIEC ==========");

        return najlepsza_trasa;
    }

    private Long getSasiedniWezel(Long aktualnyWezelID, Droga droga) {

        if (droga == null || droga.pkt_start == null || droga.pkt_koniec == null) {
            return null;
        }

        if (droga.pkt_start.ID == aktualnyWezelID) {
            return droga.pkt_koniec.ID;
        }

        if (droga.pkt_koniec.ID == aktualnyWezelID) {
            return droga.pkt_start.ID;
        }

        return null;
    }

    private double getWagaDrogi(Droga droga) {

        return droga.dlugosc;
    }

    public ArrayList<Droga> getNajlepszaTrasa() {
        return najlepsza_trasa;
    }
}