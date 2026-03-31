package mgr.ALGORYTMY;

import java.util.HashMap;
import java.util.Map;

public class WYNIKI {
    
    public static boolean czyWynikiDijkstra = false;
    public static boolean czyWynikiAlgGen = false;
    public static boolean czyWynikiAntColony = false;
    public static boolean czyWynikiAStar = false;

    public static Map<Long, Double> wartosc_wezlow_dijkstra = new HashMap<>();
    public static Map<Long, Double> wartosc_wezlow_a_star = new HashMap<>();


    public static void setWynikiDijkstra() {
        czyWynikiDijkstra = true;
        czyWynikiAlgGen = false;
        czyWynikiAntColony = false;
        czyWynikiAStar = false;
    }


    public static void setWynikiAlgGen() {
        czyWynikiDijkstra = false;
        czyWynikiAlgGen = true;
        czyWynikiAntColony = false;
        czyWynikiAStar = false;
    }


    public static void setWynikiAntColony() {
        czyWynikiDijkstra = false;
        czyWynikiAlgGen = false;
        czyWynikiAntColony = true;
        czyWynikiAStar = false;
    }


    public static void setWynikiAStar() {
        czyWynikiDijkstra = false;
        czyWynikiAlgGen = false;
        czyWynikiAntColony = false;
        czyWynikiAStar = true;
    }
}