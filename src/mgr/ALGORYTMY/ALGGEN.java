package mgr.ALGORYTMY;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;

public class ALGGEN {

    private int max_dlugosc_sciezki = 3000;
    private int max_iter_alg = 100;
    private int max_iteracji_los_sciezki = 40000;
    private int ilosc_osobnikow = 10;
    private List<OSOBNIK> populacja = new ArrayList<>();

    public Map<Long, Droga> drogi = new HashMap<>();

    private Wezel pktStart;
    private Wezel pktKoniec;
    private Map<Long, Wezel> wezly = new HashMap<>();
    private List<Droga> SCIEZKA_DROG = new ArrayList<>();
    private List<Wezel> SCIEZKA_WEZLOW = new ArrayList<>();

    private final double szansaKrzyzowanie = 0.8;
    private final double szansaMutacja = 0.1;

    public ALGGEN() {
        this.drogi = DANE.drogi;
        this.pktStart = DANE.wezelStartowyAlgorytmu;
        this.pktKoniec = DANE.wezelKoncowyAlgorytmu;
        this.wezly = DANE.wezly;
    }

    private int randomInt(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("max < min");
        }
        int zakres = max - min + 1;
        return (int) (Math.random() * zakres) + min;
    }

    public void startAlg() {

        init_pop();

        int iter = 0;
        OSOBNIK najlepszy = new OSOBNIK();
        while (iter < this.max_iter_alg) {
            ocen_osobnikow();
            najlepszy = znajdz_najlepszego();

            populacja = selekcja_PolowaNajlepszych();
            List<OSOBNIK> dzieci = operacja_krzyzowanie(populacja);
            populacja.addAll(dzieci);
            operacja_mutacja(populacja);

            iter++;
        }

    }

    private OSOBNIK znajdz_losowa_sciezke(Wezel pkt_startowy, Wezel pkt_koncowy) {
        if (pkt_startowy == null || pkt_koncowy == null) {
            throw new IllegalArgumentException("brak pkt_start lub pkt_koniec dla znajdz_losowa_sciezke");
        }

        // ta funkcja szuka losowej drogi
        List<Wezel> sciezka_wezly = new ArrayList<>();
        List<Droga> sciezka = new ArrayList<>();
        Set<Long> uzyte_wezly = new HashSet<>();
        List<Long> mozliwe_kolejne_wezly;
        Long wylosowany_wezel_id;
        int iter = 0;
        sciezka_wezly.add(pkt_startowy);
        Wezel aktualny_wezel = pkt_startowy;
        while (true) {
            // znalezc mozliwe kolejne wezly
            mozliwe_kolejne_wezly = mozliwe_kolejne_wezly(aktualny_wezel, uzyte_wezly);
            if (mozliwe_kolejne_wezly.isEmpty()) {
                // jesli brak mozliwych kierunkow -> restart
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
                sciezka_wezly.clear();
                sciezka_wezly.add(pkt_startowy);
                continue;
            }

            // losowanie drogi z mozliwych
            wylosowany_wezel_id = wylosuj_wezel_id(mozliwe_kolejne_wezly);

            // dodaj droge do sciezki
            dodaj_droge_do_sciezki(aktualny_wezel, wezly.get(wylosowany_wezel_id), sciezka);
            sciezka_wezly.add(wezly.get(wylosowany_wezel_id));

            if (sciezka.size() > max_dlugosc_sciezki) {
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
                sciezka_wezly.clear();
                sciezka_wezly.add(pkt_startowy);
                continue;
            }

            // dodaj wezel do uzytych
            dodaj_wezel_do_uzytych(uzyte_wezly, aktualny_wezel);

            // aktualizuj aktualny wezel
            aktualny_wezel = aktualizuj_wezel(aktualny_wezel, sciezka);

            // sprawdz czy koniec algorytmu
            if (aktualny_wezel.ID == pkt_koncowy.ID) {
                System.out.println("Znalezniono sciezke!");
                System.out.println("sciezka drog: " + sciezka);

                return new OSOBNIK(sciezka, sciezka_wezly);
            }

            iter++;
            if (iter > max_iteracji_los_sciezki) {
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
                sciezka_wezly.clear();
                sciezka_wezly.add(pkt_startowy);
                System.out.println("petla WHILE przekroczyl " + this.max_iteracji_los_sciezki + " iteracji");
                //return null;
            }
        }
    }

    private void dodaj_droge_do_sciezki(Wezel akt_wez, Wezel new_wez, List<Droga> sciezka) {
        for (Long idDrogi : akt_wez.drogiIDs) {
            if (drogi.get(idDrogi).pkt_start.ID == akt_wez.ID && drogi.get(idDrogi).pkt_koniec.ID == new_wez.ID
                    || drogi.get(idDrogi).pkt_koniec.ID == akt_wez.ID && drogi.get(idDrogi).pkt_start.ID == new_wez.ID) {
                sciezka.add(drogi.get(idDrogi));
                return;
            }
        }
        throw new IllegalArgumentException("nie znaleziono drogi w funkcji dodaj_droge_do_sciezki, nie znalezniono zadnej drogi pomiedzy dwoma wezlami");
    }

//    private void restart() {
//        // restart, szukam nowej
//        this.AKTUALNY_WEZEL = this.pktStart;
//        this.UZYTE_WEZLY.clear();
//        this.SCIEZKA_DROG.clear();
//    }
    private List<Long> mozliwe_kolejne_wezly(Wezel wezel, Set<Long> uzyte_wez) {
        List<Long> mozliwe_kierunki = new ArrayList<>();

        // algorytm skacze od wezla do welza
        // mam pierwszy, losuje polaczenie, do kolekcji dodaje wezel z wylosowanej drogi, ale inny niz aktualny
        for (Long idDrogi : wezel.drogiIDs) {
            Droga droga = drogi.get(idDrogi);
            if (droga.pkt_start.ID == wezel.ID) {
                mozliwe_kierunki.add(droga.pkt_koniec.ID);
            } else if (droga.pkt_koniec.ID == wezel.ID) {
                mozliwe_kierunki.add(droga.pkt_start.ID);
            }
        }

        // usuwanie uzytych wezlow aby nie zawracac
        mozliwe_kierunki.removeAll(uzyte_wez);

        return mozliwe_kierunki;
    }

    private Long wylosuj_wezel_id(List<Long> mozliwe_kolejne_wezly) {
        if (mozliwe_kolejne_wezly.isEmpty()) {
            throw new IllegalArgumentException("mozliwe_kolejne_wezly puste a funkcja 'wylosuj_wezel' chce wylosowac cos");
        }
        return mozliwe_kolejne_wezly.get(randomInt(0, mozliwe_kolejne_wezly.size() - 1));
    }

    private void dodaj_wezel_do_uzytych(Set<Long> uzyte, Wezel akt_wez) {
        uzyte.add(akt_wez.ID);
    }

    private Wezel aktualizuj_wezel(Wezel akt_wez, List<Droga> sciezka) {
        Droga ostatnia_droga = sciezka.getLast();
        Wezel new_wez = new Wezel();
        if (akt_wez.ID == ostatnia_droga.pkt_start.ID) {
            new_wez = wezly.get(ostatnia_droga.pkt_koniec.ID);
        } else if (akt_wez.ID == ostatnia_droga.pkt_koniec.ID) {
            new_wez = wezly.get(ostatnia_droga.pkt_start.ID);
        } else {
            throw new IllegalArgumentException("Funkcja 'aktualizuj_wezel()' nie znalazla przeciwnego konca ostatnio dodanej sciezki");
        }
        return new_wez;
    }

    private void init_pop() {
        for (int i = 1; i <= this.ilosc_osobnikow; i++) {
            this.populacja.add(this.znajdz_losowa_sciezke(pktStart, pktKoniec));
        }
    }

    private void ocen_osobnikow() {
        for (OSOBNIK osob : this.populacja) {
            osob.ocenOsobnika();
        }
    }

    private OSOBNIK znajdz_najlepszego() {
        if (populacja == null || populacja.isEmpty()) {
            return null;
        }

        OSOBNIK najlepszy = populacja.get(0);

        for (OSOBNIK os : populacja) {
            if (os != null && os.ocena < najlepszy.ocena) {
                najlepszy = os;
            }
        }

        return najlepszy;
    }

    private List<OSOBNIK> selekcja_PolowaNajlepszych() {
        populacja.sort(Comparator.comparingDouble(osobnik -> osobnik.ocena));
        return new ArrayList<>(populacja.subList(0, populacja.size() / 2));
    }

    private List<OSOBNIK> operacja_krzyzowanie(List<OSOBNIK> pop) {
        List<OSOBNIK> skrzyzowane = new ArrayList<>();

        for (int i = 0; i < pop.size(); i++) {
            if (i == pop.size() - 1) {
                if (Math.random() < this.szansaKrzyzowanie) {
                    skrzyzowane.add(krzyzuj(pop.getFirst(), pop.getLast()));
                }
                break;
            }
            if (Math.random() < this.szansaKrzyzowanie) {
                skrzyzowane.add(krzyzuj(pop.get(i), pop.get(i + 1)));
            }
        }

        return skrzyzowane;
    }

    private OSOBNIK krzyzuj(OSOBNIK os1, OSOBNIK os2) {
        // dwoch osobnikow ma trasy       

        int size1 = os1.trasa_wezly.size();
        int size2 = os2.trasa_wezly.size();

        List<Wezel> czesc11w, czesc22w;
        List<Droga> czesc11d, czesc22d;

        int random = randomInt(1, size1 - 2);
        czesc11w = new ArrayList<>(os1.trasa_wezly.subList(0, random));
        czesc11d = new ArrayList<>(os1.trasa_drogi.subList(0, random));

        random = randomInt(1, size2 - 2);
        czesc22w = new ArrayList<>(os2.trasa_wezly.subList(random, size2 - 1));
        czesc22d = new ArrayList<>(os2.trasa_drogi.subList(random, size2 - 1));

        OSOBNIK osobnik_skrzyzowany = znajdz_losowa_sciezke(czesc11w.getLast(), czesc22w.getFirst());
        osobnik_skrzyzowany.trasa_drogi.addAll(0, czesc11d);
        osobnik_skrzyzowany.trasa_drogi.addAll(czesc22d);
        osobnik_skrzyzowany.trasa_wezly.addAll(0, czesc11w);
        osobnik_skrzyzowany.trasa_wezly.addAll(czesc22w);
        return osobnik_skrzyzowany;
    }

    private void operacja_mutacja(List<OSOBNIK> pop) {
        for (int i = 0; i < pop.size(); i++) {
            OSOBNIK osob = pop.get(i);

            if (osob == null) {
                continue;
            }

            if (Math.random() < this.szansaMutacja) {
                if (osob.trasa_drogi == null || osob.trasa_wezly == null) {
                    continue;
                }

                if (osob.trasa_drogi.size() < 2 || osob.trasa_wezly.size() < 3) {
                    continue;
                }

                int punkt_ciecia = randomInt(1, osob.trasa_drogi.size() - 1);

                osob.trasa_drogi.subList(punkt_ciecia, osob.trasa_drogi.size()).clear();
                osob.trasa_wezly.subList(punkt_ciecia + 1, osob.trasa_wezly.size()).clear();

                Wezel ostatni_wezel = osob.trasa_wezly.get(osob.trasa_wezly.size() - 1);

                OSOBNIK osobnik_zmutowany = znajdz_losowa_sciezke(ostatni_wezel, this.pktKoniec);

                if (osobnik_zmutowany == null) {
                    continue;
                }

                osobnik_zmutowany.trasa_drogi.addAll(0, osob.trasa_drogi);

                if (!osobnik_zmutowany.trasa_wezly.isEmpty()) {
                    osobnik_zmutowany.trasa_wezly.remove(0);
                }
                osobnik_zmutowany.trasa_wezly.addAll(0, osob.trasa_wezly);

                pop.set(i, osobnik_zmutowany);
            }
        }
    }
}
