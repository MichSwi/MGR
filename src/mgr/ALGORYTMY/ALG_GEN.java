/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.Comparator;
import mgr.DANE;
import static mgr.DANE.print;
import mgr.Wezel;

/**
 *
 * @author Micha
 */
public class ALG_GEN {

    double SZANSA_NA_MUTACJE = 0.15;
    double SZANSA_NA_KRZYZOWANIE = 0.9;
    int LICZEBNOSC_POPULACJI = 30;
    int MAX_GENERACJI = 50;
    boolean ZACHOWANIE_ELITARNEGO = true;

    int KRZYZOWANIE_GDY_BRAK_WSPOLNEGO = 0;
    // 0 - brak krzyzowania
    // 1 - losowa trasa w losowych punktach dwoch tras
    // 2 - dijkstra w losowych punktach dwoch tras

    Wezel w_start;
    Wezel w_koniec;
    //Random random = new Random();

    ArrayList<ArrayList<TRASA>> zapisaneGeneracje = new ArrayList<>();
    ArrayList<TRASA> zapisaniElitarni = new ArrayList<>();
    private final int MAX_BRAK_PROGRESU = 10;
    private final int ROZMIAR_TURNIEJU = 3;

    public ALG_GEN(Wezel w_start, Wezel w_koniec) {
        this.w_start = w_start;
        this.w_koniec = w_koniec;
    }

    public TRASA start() {
        print("============== START ALG GEN =================");
        ArrayList<TRASA> dzieci = new ArrayList<>();
        ArrayList<TRASA> pop = init_pop();

        TRASA elitarny = getNajlepszy(pop);
        System.out.println("");

        int brak_progresu = 0;
        int iter = 0;
        while (iter < MAX_GENERACJI) {
            iter++;
            print("iteracja nr: " + iter);
            dzieci.clear();

            // ocena - liczona na bierzaco
            //krzyzowanie + selekcja
            for (int i = 0; i < pop.size(); i += 2) {
                TRASA rodzic2 = new TRASA();
                //TRASA rodzic1 = sel_ruletka(pop);
                //TRASA rodzic1 = sel_turniejowa(pop);
                TRASA rodzic1 = sel_rankingowa(pop);
                do {
                    //rodzic2 = sel_ruletka(pop);
                    //rodzic2 = sel_turniejowa(pop);
                    rodzic2 = sel_rankingowa(pop);
                } while (rodzic1 == rodzic2);

                print("krzyzowanie nr " + i);
                print("Rodzic1 : " + rodzic1.czas_przejazdu);
                print("Rodzic2 : " + rodzic2.czas_przejazdu);
                if (Math.random() < SZANSA_NA_KRZYZOWANIE) {
                    TRASA dziecko1 = krzyzowanie_wspolny_punkt(rodzic1, rodzic2);
                    TRASA dziecko2 = krzyzowanie_wspolny_punkt(rodzic2, rodzic1);

                    if (KRZYZOWANIE_GDY_BRAK_WSPOLNEGO == 0) {
                        if (dziecko1 != null && dziecko2 != null) {
                            print("UDANE krzyzowanie");
                            print("dziecko1: " + dziecko1.czas_przejazdu);
                            print("dziecko2: " + dziecko2.czas_przejazdu);
                            dzieci.add(dziecko1);
                            dzieci.add(dziecko2);

                        } else {
                            print("NIE UDANE krzyzowanie, oddaje rodzicow");
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

            // elitarnosc - dodaj jesli usunieto elitarnego
            if (ZACHOWANIE_ELITARNEGO) {
                if (!dzieci.contains(elitarny)) {
                    dzieci.remove(getNajgorszy(dzieci));
                    dzieci.add(elitarny);
                }
            }

            //elitarnosc - sprawdz czy nowy elitarny / sprawdz czy postep
            TRASA najlepszaAktualnie = getNajlepszy(dzieci);
            if (najlepszaAktualnie.czas_przejazdu < elitarny.czas_przejazdu) {
                elitarny = najlepszaAktualnie;
                this.zapisaniElitarni.add(elitarny);
                brak_progresu = 0;
            } else {
                brak_progresu++;
            }

            pop = new ArrayList<>(dzieci);
            zapiszPop(pop);

            // warunki stopu
            if (brak_progresu > MAX_BRAK_PROGRESU) {
                print("ZADZIALAL WARUNEK STOP BRAK PROGERSU");
                printMetryki();
                return elitarny;
            }

        }
        print("ZADZIALAL MAX ITER" + iter);
        printMetryki();
        return elitarny;
    }

    private ArrayList<TRASA> init_pop() {
        print("start init pop");
        ArrayList<TRASA> new_pop = new ArrayList<>();
        LOSOWA_TRASA los = new LOSOWA_TRASA();

        for (int i = 1; i <= LICZEBNOSC_POPULACJI; i++) {
            new_pop.add(los.start(w_start, w_koniec));
            print("stworzono " + i + "/" + LICZEBNOSC_POPULACJI);
        }
        return new_pop;
    }

    private TRASA sel_ruletka(ArrayList<TRASA> pop) {
        print("START RULETKA");
        Double suma_fitness = 0.0;
        for (TRASA trasa : pop) {
            double fitness = 1.0 / (trasa.czas_przejazdu);
            suma_fitness += fitness;
        }
        print("suma_fitness: " + suma_fitness);
        // losowanie punktu na ruletce
        double los = Math.random() * suma_fitness;
        print("losowy punkt na ruletce: " + los);
        // szukanie wyniku
        double sumaNarastajaca = 0.0;
        for (TRASA trasa : pop) {
            double fitness = 1.0 / (trasa.czas_przejazdu + 0.000001);
            sumaNarastajaca += fitness;

            if (sumaNarastajaca >= los) {
                print("wylosowano drroge o czasie: " + trasa.czas_przejazdu);
                return trasa;
            }
        }
        return null;
    }

    private TRASA sel_turniejowa(ArrayList<TRASA> pop) {

        TRASA najlepszy = null;

        for (int i = 0; i < ROZMIAR_TURNIEJU; i++) {

            int losowyIndex = (int) (Math.random() * pop.size());
            TRASA kandydat = pop.get(losowyIndex);

            if (najlepszy == null || kandydat.czas_przejazdu < najlepszy.czas_przejazdu) {
                najlepszy = kandydat;
            }
        }

        return najlepszy;
    }

    private TRASA sel_rankingowa(ArrayList<TRASA> pop) {

        ArrayList<TRASA> posortowane = new ArrayList<>(pop);

        // najmniejszy czas przejazdu idzie na poczatek
        posortowane.sort(Comparator.comparingDouble(t -> t.czas_przejazdu));

        int n = posortowane.size();
        int sumaRang = n * (n + 1) / 2;
        double los = Math.random() * sumaRang;

        double sumaNarastajaca = 0.0;

        for (int i = 0; i < n; i++) {

            // najlepszy dostaje najwyzsza range
            int ranga = n - i;

            sumaNarastajaca += ranga;

            if (sumaNarastajaca >= los) {
                return posortowane.get(i);
            }
        }

        return posortowane.get(n - 1);
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
        print("Ilosc wspolnych wezlow: " + wspolne.size());

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
        print("Wylosowany wezle/punktkrzyzowania: " + (int) (Math.random() * wspolne.size()));
        return punktKrzyzowania;
    }

    private ArrayList<TRASA> wykonajMutacje(ArrayList<TRASA> dzieci) {

        print("START funkcji MUTACJI");
        ArrayList<TRASA> noweDzieci = new ArrayList<>();
        for (TRASA dziecko : dzieci) {
            if (Math.random() < SZANSA_NA_MUTACJE) {
                print("ROZPOCZETA MUTACJA");
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
                print("Mutowana trasa: " + dziecko.trasa_drogi_id.toString() + " czas przejazdu= " + dziecko.czas_przejazdu + " , ilosc wezlow: " + dziecko.trasa_wezly_id.size());
                print("losowyIndex1 = " + losowyIndex1);
                print("losowyIndex2 = " + losowyIndex2);
                Wezel w1 = DANE.wezly.get(dziecko.trasa_wezly_id.get(losowyIndex1));
                Wezel w2 = DANE.wezly.get(dziecko.trasa_wezly_id.get(losowyIndex2));

                LOSOWA_TRASA losowa = new LOSOWA_TRASA();
                TRASA wypelnienie = losowa.start(w1, w2);
                print("Czas wypelniajacej losowej trasy: " + wypelnienie.czas_przejazdu);
                print("Trasa wypelniajaca: " + wypelnienie.trasa_wezly_id.toString());
                ArrayList<Long> nowa_trasa_wezly_id = new ArrayList<>();
                for (int i = 0; i < losowyIndex1; i++) {
                    nowa_trasa_wezly_id.add(dziecko.trasa_wezly_id.get(i));
                }
                nowa_trasa_wezly_id.addAll(wypelnienie.trasa_wezly_id);
                for (int i = losowyIndex2 + 1; i < dziecko.trasa_wezly_id.size(); i++) {
                    nowa_trasa_wezly_id.add(dziecko.trasa_wezly_id.get(i));
                }
                TRASA nowa_trasa = TRASA.stworz_z_wezlow_id(nowa_trasa_wezly_id);
                noweDzieci.add(nowa_trasa);
                print("Nowa zmutowana trasa czas: " + nowa_trasa.czas_przejazdu + " , ilosc wezlow: " + nowa_trasa.trasa_wezly_id.size());

            } else {
                noweDzieci.add(dziecko);
            }
        }
        return noweDzieci;
    }

    private TRASA getNajlepszy(ArrayList<TRASA> pop) {
//        TRASA best = pop.get(0);
//        for (TRASA tr : pop) {
//            if (tr.czas_przejazdu < best.czas_przejazdu) {
//                best = tr;
//            }
//        }
//        return best;

        ArrayList<TRASA> posortowane = new ArrayList<>(pop);
        posortowane.sort(Comparator.comparingDouble(t -> t.czas_przejazdu));
        return posortowane.getFirst();
    }

    private void printMetryki() {

        ArrayList<Double> srednie_generacji = new ArrayList<>();
        double srednia_populacji = 0;
        for (ArrayList<TRASA> pop : zapisaneGeneracje) {
            srednia_populacji = 0;
            for (TRASA tr : pop) {
                srednia_populacji += tr.czas_przejazdu;
            }
            srednia_populacji = srednia_populacji / pop.size();
            srednie_generacji.add(srednia_populacji);
        }

        for (ArrayList<TRASA> pop : zapisaneGeneracje) {
            print("====== GEN " + zapisaneGeneracje.indexOf(pop) + " ======");
            for (TRASA tr : pop) {
                print("Ilosc wezlow: " + tr.trasa_wezly_id.size() + " czas: " + tr.czas_przejazdu);
            }
            print("Srednia czasu generacji: " + srednie_generacji.get(zapisaneGeneracje.indexOf(pop)));
            print("Najlepszy z generacji [s]: " + getNajlepszy(pop).czas_przejazdu + " | [m]: " + getNajlepszy(pop).dlugosc);
        }

        int i = 0;
        for (TRASA elitarny : zapisaniElitarni) {
            i++;
            print("Elita nr " + i + " ilosc wezlow : " + elitarny.trasa_wezly_id.size() + " czas przejazdu: " + elitarny.czas_przejazdu + " odleglosc [m]: " + elitarny.dlugosc);
        }
    }

    private void zapiszPop(ArrayList<TRASA> pop) {
        this.zapisaneGeneracje.add(pop);
    }

    private TRASA getNajgorszy(ArrayList<TRASA> pop) {
//        TRASA worst = pop.get(0);
//        for (TRASA tr : pop) {
//            if (tr.czas_przejazdu > worst.czas_przejazdu) {
//                worst = tr;
//            }
//        }
//        return worst;
        ArrayList<TRASA> posortowane = new ArrayList<>(pop);
        posortowane.sort(Comparator.comparingDouble(t -> t.czas_przejazdu));
        return posortowane.getLast();
    }

}
