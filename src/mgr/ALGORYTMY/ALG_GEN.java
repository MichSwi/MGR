/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr.ALGORYTMY;

import java.util.ArrayList;
import mgr.DANE;
import mgr.Wezel;

/**
 *
 * @author Micha
 */
public class ALG_GEN {

    double szansa_na_mutacje = 0.1;
    double szansa_na_krzyzowanie = 0.9;
    int LICZEBNOSC_POPULACJI = 20;
    int max_iter = 20;
    boolean elitarnosc= true;

    int KRZYZOWANIE_GDY_BRAK_WSPOLNEGO = 0;
    // 0 - brak krzyzowania
    // 1 - losowa trasa w losowych punktach dwoch tras
    // 2 - dijkstra w losowych punktach dwoch tras

    Wezel w_start;
    Wezel w_koniec;
    //Random random = new Random();

    public ALG_GEN(Wezel w_start, Wezel w_koniec) {
        this.w_start = w_start;
        this.w_koniec = w_koniec;

    }

    public TRASA start() {

        ArrayList<TRASA> pop = new ArrayList<>();
        ArrayList<TRASA> dzieci = new ArrayList<>();
        ArrayList<Double> srednie_pop = new ArrayList<>();
        TRASA najlepszy = new TRASA();
        
        pop = init_pop();
        najlepszy = getElitarny(pop, pop.get(0));
        System.out.println("");

        int iter = 0;
        while (iter < max_iter) {
            iter++;
            dzieci.clear();

            // ocena - liczona na bierzaco
            //krzyzowanie + selekcja
            for (int i = 0; i < pop.size(); i += 2) {
                TRASA rodzic2 = new TRASA();
                TRASA rodzic1 = sel_ruletka(pop);
                do {
                    rodzic2 = sel_ruletka(pop);
                } while (rodzic1 == rodzic2);

                if (Math.random() < szansa_na_krzyzowanie) {
                    TRASA dziecko1 = krzyzowanie_wspolny_punkt(rodzic1, rodzic2);
                    TRASA dziecko2 = krzyzowanie_wspolny_punkt(rodzic2, rodzic1);

                    if (KRZYZOWANIE_GDY_BRAK_WSPOLNEGO == 0) {
                        if (dziecko1 != null && dziecko2 != null) {
                            dzieci.add(dziecko1);
                            dzieci.add(dziecko2);
                        } else {
                            dzieci.add(rodzic1);
                            dzieci.add(rodzic2);
                        }
                    }
                } else {
                    dzieci.add(rodzic1);
                    dzieci.add(rodzic2);
                }
            }

            //mutacja
            dzieci = wykonajMutacje(dzieci);

            // nowa pop
            //warunki stopu
            //najlepszy save
            najlepszy = getElitarny(pop, najlepszy);
            
            pop = new ArrayList<>(dzieci);
            double srednia_populacji = 0;
            for (TRASA tr : dzieci) {
                srednia_populacji += tr.czas_przejazdu;
            }
            srednia_populacji = srednia_populacji / pop.size();
            srednie_pop.add(srednia_populacji);

        }
        System.out.println("kolejne srednie po iteracjach: " + srednie_pop.toString());
        return new TRASA();
    }

    private ArrayList<TRASA> init_pop() {
        ArrayList<TRASA> new_pop = new ArrayList<>();
        LOSOWA_TRASA los = new LOSOWA_TRASA();

        for (int i = 1; i <= LICZEBNOSC_POPULACJI; i++) {
            new_pop.add(los.start(w_start, w_koniec));
        }
        return new_pop;
    }

    private TRASA sel_ruletka(ArrayList<TRASA> pop) {
        Double suma_fitness = 0.0;
        for (TRASA trasa : pop) {
            double fitness = 1.0 / (trasa.czas_przejazdu);
            suma_fitness += fitness;
        }

        // losowanie punktu na ruletce
        double los = Math.random() * suma_fitness;

        // szukanie wyniku
        double sumaNarastajaca = 0.0;
        for (TRASA trasa : pop) {
            double fitness = 1.0 / (trasa.czas_przejazdu + 0.000001);
            sumaNarastajaca += fitness;

            if (sumaNarastajaca >= los) {
                return trasa;
            }
        }
        return null;
    }

    private TRASA krzyzowanie_wspolny_punkt(TRASA trasa1, TRASA trasa2) {

        Long wsp_wezel = wylosujWspolnyWezel(trasa1, trasa2);
        if (wsp_wezel == -1L) {
            return null;
        }

        ArrayList<Long> t1_w_id = new ArrayList<>(trasa1.trasa_wezly_id);
        ArrayList<Long> t2_w_id = new ArrayList<>(trasa2.trasa_wezly_id);

        int index1 = t1_w_id.indexOf(wsp_wezel);
        int index2 = t2_w_id.indexOf(wsp_wezel);

        ArrayList<Long> dzieckoWezly = new ArrayList<>();

        // początek z rodzica 1
        for (int i = 0; i <= index1; i++) {
            dzieckoWezly.add(t1_w_id.get(i));
        }

        // końcówka z rodzica 2
        for (int i = index2 + 1; i < t2_w_id.size(); i++) {
            dzieckoWezly.add(t2_w_id.get(i));
        }

        return TRASA.stworz_z_wezlow_id(dzieckoWezly);

    }

    private Long wylosujWspolnyWezel(TRASA trasa1, TRASA trasa2) {
        ArrayList<Long> wspolne = new ArrayList<>();

        for (Long id : trasa1.trasa_wezly_id) {
            if (trasa2.trasa_wezly_id.contains(id)) {
                wspolne.add(id);
            }
        }

        // usuniecei start i koniec
        if (!wspolne.isEmpty()) {
            wspolne.remove(trasa1.trasa_wezly_id.get(0));
            wspolne.remove(trasa1.trasa_wezly_id.get(trasa1.trasa_wezly_id.size() - 1));
        }

        // jeśli nie ma wspólnego punktu, zwracamy kopię jednego rodzica
        if (wspolne.isEmpty()) {
            return -1L;
        }

        Long punktKrzyzowania = wspolne.get((int) (Math.random() * wspolne.size()));
        return punktKrzyzowania;
    }

    private ArrayList<TRASA> wykonajMutacje(ArrayList<TRASA> dzieci) {
        ArrayList<TRASA> noweDzieci = new ArrayList<>();
        for (TRASA dziecko : dzieci) {
            if (Math.random() < szansa_na_mutacje) {
                int il_wezlow = dziecko.trasa_wezly_id.size() - 2; //odjety poczatkowy i koncowy

                int losowyIndex1 = (int) (Math.random() * il_wezlow) + 1;
                int losowyIndex2;
                do {
                    losowyIndex2 = (int) (Math.random() * il_wezlow) + 1;
                } while (losowyIndex1 == losowyIndex2);

                if (losowyIndex1 > losowyIndex2) {
                    int temp = losowyIndex1;
                    losowyIndex1 = losowyIndex2;
                    losowyIndex2 = temp;
                }

                Wezel w1 = DANE.wezly.get(dziecko.trasa_wezly_id.get(losowyIndex1));
                Wezel w2 = DANE.wezly.get(dziecko.trasa_wezly_id.get(losowyIndex2));

                LOSOWA_TRASA losowa = new LOSOWA_TRASA();
                TRASA wypelnienie = losowa.start(w1, w2);

                ArrayList<Long> nowa_trasa_wezly_id = new ArrayList<>();
                for (int i = 0; i < losowyIndex1; i++) {
                    nowa_trasa_wezly_id.add(dziecko.trasa_wezly_id.get(i));
                }
                nowa_trasa_wezly_id.addAll(wypelnienie.trasa_wezly_id);
                for (int i = losowyIndex2 + 1; i < dziecko.trasa_wezly_id.size(); i++) {
                    nowa_trasa_wezly_id.add(dziecko.trasa_wezly_id.get(i));
                }
                noweDzieci.add(TRASA.stworz_z_wezlow_id(nowa_trasa_wezly_id));
            } else {
                noweDzieci.add(dziecko);
            }
        }
        return noweDzieci;
    }

    private TRASA getElitarny(ArrayList<TRASA> pop, TRASA best) {
        for (TRASA tr : pop){
            if(tr.czas_przejazdu<best.czas_przejazdu){
                best=tr;
            }
        }
        return best;
    }
}
