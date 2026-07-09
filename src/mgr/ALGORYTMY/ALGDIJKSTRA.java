package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;
import mgr.Wezel.Polaczenie;

public class ALGDIJKSTRA {

    private Long pktStart, pktKoniec;

    private List<Long> nieodwiedzone = new ArrayList<>();
    private Map<Long, Double> wartosc_wezlow = new HashMap<>();
    private Map<Long, Long> poprzedni_wezel = new HashMap<>();

    private PriorityQueue<Map.Entry<Long, Double>> kolejka
            = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

    private Map<Long, Wezel> wezly = new HashMap<>();
    private Map<Long, Droga> drogi = new HashMap<>();

    public ALGDIJKSTRA() {
        pktStart = DANE.wezelStartowyAlgorytmu.ID;
        pktKoniec = DANE.wezelKoncowyAlgorytmu.ID;
        wezly = DANE.wezly;
        drogi = DANE.drogi;

        for (Long wezel_id : wezly.keySet()) {
            nieodwiedzone.add(wezel_id);
            wartosc_wezlow.put(wezel_id, Double.POSITIVE_INFINITY);
            poprzedni_wezel.put(wezel_id, null);
        }

        wartosc_wezlow.put(pktStart, 0.0);
        kolejka.add(Map.entry(pktStart, 0.0));

        while (!kolejka.isEmpty()) {
            Map.Entry<Long, Double> wpis = kolejka.poll();
            Long akt_wez = wpis.getKey();
            Double koszt_z_kolejki = wpis.getValue();

            // pomijamy nieaktualny wpis z kolejki
            if (koszt_z_kolejki > wartosc_wezlow.get(akt_wez)) {
                continue;
            }

            // jesli już odwiedzony - pomijamy
            if (!nieodwiedzone.contains(akt_wez)) {
                continue;
            }

            // oznacz jako odwiedzony
            nieodwiedzone.remove(akt_wez);

            // jeśli doszlismy do celu - return
            if (akt_wez.equals(pktKoniec)) {
                WYNIKI.czyWynikiDijkstra = true;
                break;
            }

            // sprawdzanie sasiadow
            for (Polaczenie pol : wezly.get(akt_wez).polaczenia) {

                if (pol.przejazd == false) {
                    continue;
                }

                Long przeciwny_wezel = pol.kolejnyWezel;
                // sąsiad już odwiedzony - kolejny sasiad
                if (!nieodwiedzone.contains(przeciwny_wezel)) {
                    continue;
                }

                Double new_koszt = wartosc_wezlow.get(akt_wez) + drogi.get(pol.IDdrogi).czas_przejazdu;

                if (new_koszt < wartosc_wezlow.get(przeciwny_wezel)) {
                    wartosc_wezlow.put(przeciwny_wezel, new_koszt);
                    poprzedni_wezel.put(przeciwny_wezel, akt_wez);
                    kolejka.add(Map.entry(przeciwny_wezel, new_koszt));
                }
            }
        }
    }

    public Double getKosztDoKonca() {
        return wartosc_wezlow.get(pktKoniec);
    }

    public TRASA getSciezka() {
        ArrayList<Long> sciezka = new ArrayList<>();

        if (wartosc_wezlow.get(pktKoniec).equals(Double.POSITIVE_INFINITY)) {
            return TRASA.stworz_z_wezlow_id(sciezka);
        }

        Long akt = pktKoniec;
        while (akt != null) {
            sciezka.add(0, akt);
            akt = poprzedni_wezel.get(akt);
        }
        return TRASA.stworz_z_wezlow_id(sciezka);
    }

    public Map<Long, Double> getWartosc_wezlow() {
        return wartosc_wezlow;
    }

}
