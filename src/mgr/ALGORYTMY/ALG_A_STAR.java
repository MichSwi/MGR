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

public class ALG_A_STAR {

    private static Map<Long, Double> gScore = new HashMap<>();
    
    public static void start() {
        List<Long> closedSet = new ArrayList<>();
        PriorityQueue<Map.Entry<Long, Double>> openSet
                = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        gScore.clear();
        Map<Long, Double> fScore = new HashMap<>();
        Map<Long, Long> cameFromDro = new HashMap<>();
        Map<Long, Long> cameFromWez = new HashMap<>();

        for (Wezel wez : DANE.wezly.values()) {
            gScore.put(wez.ID, Double.POSITIVE_INFINITY);
            fScore.put(wez.ID, Double.POSITIVE_INFINITY);
        }

        gScore.put(DANE.wezelStartowyAlgorytmu.ID, 0.0);
        fScore.put(
                DANE.wezelStartowyAlgorytmu.ID,
                odlegloscWLiniProstej(DANE.wezelStartowyAlgorytmu.ID, DANE.wezelKoncowyAlgorytmu.ID)
        );

        openSet.add(Map.entry(
                DANE.wezelStartowyAlgorytmu.ID,
                fScore.get(DANE.wezelStartowyAlgorytmu.ID))
        );

        while (!openSet.isEmpty()) {
            Long aktualnyWezelId = openSet.poll().getKey();

            if (closedSet.contains(aktualnyWezelId)) {
                continue;
            }

            Wezel aktualny_wezel = DANE.wezly.get(aktualnyWezelId);

            if (aktualny_wezel.ID == DANE.wezelKoncowyAlgorytmu.ID) {
                odtworzSciezke(cameFromWez, cameFromDro);
                return;
            }

            closedSet.add(aktualny_wezel.ID);

            for (Long dr_sasiad : aktualny_wezel.drogiIDs) {
                Long sasiad_wezel_id = DANE.drogi.get(dr_sasiad).getPrzeciwnyWezelId(aktualny_wezel.ID);

                if (closedSet.contains(sasiad_wezel_id)) {
                    continue;
                }

                Double kosztCalkowity = gScore.get(aktualny_wezel.ID)
                        + DANE.drogi.get(dr_sasiad).dlugosc;

                if (kosztCalkowity < gScore.get(sasiad_wezel_id)) {
                    cameFromDro.put(sasiad_wezel_id, dr_sasiad);
                    cameFromWez.put(sasiad_wezel_id, aktualny_wezel.ID);

                    gScore.put(sasiad_wezel_id, kosztCalkowity);
                    fScore.put(
                            sasiad_wezel_id,
                            kosztCalkowity + odlegloscWLiniProstej(
                                    sasiad_wezel_id,
                                    DANE.wezelKoncowyAlgorytmu.ID
                            )
                    );

                    openSet.add(Map.entry(
                            sasiad_wezel_id,
                            fScore.get(sasiad_wezel_id)
                    ));
                }
            }
        }
    }

    private static double odlegloscWLiniProstej(Long id1, Long id2) {
        Wezel w1 = DANE.wezly.get(id1);
        Wezel w2 = DANE.wezly.get(id2);

        double dx = w1.X - w2.X;
        double dy = w1.Y - w2.Y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    private static void odtworzSciezke(Map<Long, Long> cameFromWez, Map<Long, Long> cameFromDro) {
        DANE.ALG_SCIEZKA.clear();

        Long aktualnyWezelId = DANE.wezelKoncowyAlgorytmu.ID;
        List<Long> sciezkaDrogTmp = new ArrayList<>();

        while (!aktualnyWezelId.equals(DANE.wezelStartowyAlgorytmu.ID)) {
            Long drogaId = cameFromDro.get(aktualnyWezelId);
            Long poprzedniWezelId = cameFromWez.get(aktualnyWezelId);

            if (drogaId == null || poprzedniWezelId == null) {
                System.out.println("Nie udało się odtworzyć ścieżki.");
                DANE.ALG_SCIEZKA.clear();
                return;
            }

            sciezkaDrogTmp.add(0, drogaId);
            aktualnyWezelId = poprzedniWezelId;
        }
        List<Droga> trasa = new ArrayList<>();
        for (Long droga_id : sciezkaDrogTmp) {
            trasa.add(DANE.drogi.get(droga_id));
        }
        DANE.ALG_SCIEZKA = trasa;
        
        WYNIKI.wartosc_wezlow_a_star = getgScore();
        WYNIKI.setWynikiAStar();
    }

    public static Map<Long, Double> getgScore() {
        return gScore;
    }
    
    
}
