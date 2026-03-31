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

    private int max_dlugosc_sciezki = 1000;
    private int max_iter_alg = 1000;
    private int max_iteracji_los_sciezki = 80000;
    private int ilosc_osobnikow = 10;
    private List<OSOBNIK> populacja = new ArrayList<>();

    public Map<Long, Droga> drogi = new HashMap<>();

    private Wezel pktStart;
    private Wezel pktKoniec;
    private Map<Long, Wezel> wezly = new HashMap<>();

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
        OSOBNIK najlepszyGlobalnie = null;

        while (iter < this.max_iter_alg) {
            if (populacja == null || populacja.isEmpty()) {
                break;
            }

            ocen_osobnikow();

            OSOBNIK najlepszyIteracji = znajdz_najlepszego();
            if (najlepszyIteracji != null) {
                if (najlepszyGlobalnie == null || najlepszyIteracji.ocena < najlepszyGlobalnie.ocena) {
                    najlepszyGlobalnie = najlepszyIteracji;
                }
            }

            populacja = selekcja_PolowaNajlepszych();

            List<OSOBNIK> dzieci = operacja_krzyzowanie(populacja);
            populacja.addAll(dzieci);

            operacja_mutacja(populacja);

            uzupelnij_populacje();

            iter++;
        }

        if (najlepszyGlobalnie != null) {
            System.out.println("Najlepszy osobnik ocena: " + najlepszyGlobalnie.ocena);
            DANE.ALG_SCIEZKA = najlepszyGlobalnie.trasa_drogi;
        } else {
            System.out.println("Nie znaleziono najlepszego osobnika.");
        }
    }

    private OSOBNIK znajdz_losowa_sciezke(Wezel pkt_startowy, Wezel pkt_koncowy) {
        if (pkt_startowy == null || pkt_koncowy == null) {
            throw new IllegalArgumentException("brak pkt_start lub pkt_koniec dla znajdz_losowa_sciezke");
        }

        List<Wezel> sciezka_wezly = new ArrayList<>();
        List<Droga> sciezka = new ArrayList<>();
        Set<Long> uzyte_wezly = new HashSet<>();
        int iter = 0;

        sciezka_wezly.add(pkt_startowy);
        Wezel aktualny_wezel = pkt_startowy;

        while (true) {
            List<Long> mozliwe_kolejne_wezly = mozliwe_kolejne_wezly(aktualny_wezel, uzyte_wezly);

            if (mozliwe_kolejne_wezly.isEmpty()) {
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
                sciezka_wezly.clear();
                sciezka_wezly.add(pkt_startowy);
                iter++;
                if (iter > max_iteracji_los_sciezki) {
                    System.out.println("Przekroczono limit iteracji przy szukaniu losowej sciezki.");
                    return null;
                }
                continue;
            }

            Long wylosowany_wezel_id = wylosuj_wezel_id(mozliwe_kolejne_wezly);
            Wezel nowy_wezel = wezly.get(wylosowany_wezel_id);

            if (nowy_wezel == null) {
                iter++;
                if (iter > max_iteracji_los_sciezki) {
                    System.out.println("Przekroczono limit iteracji przy szukaniu losowej sciezki.");
                    return null;
                }
                continue;
            }

            dodaj_droge_do_sciezki(aktualny_wezel, nowy_wezel, sciezka);
            sciezka_wezly.add(nowy_wezel);

            if (sciezka.size() > max_dlugosc_sciezki) {
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
                sciezka_wezly.clear();
                sciezka_wezly.add(pkt_startowy);
                iter++;
                if (iter > max_iteracji_los_sciezki) {
                    System.out.println("Przekroczono limit iteracji przy szukaniu losowej sciezki.");
                    return null;
                }
                continue;
            }

            dodaj_wezel_do_uzytych(uzyte_wezly, aktualny_wezel);
            aktualny_wezel = aktualizuj_wezel(aktualny_wezel, sciezka);

            if (aktualny_wezel.ID == pkt_koncowy.ID) {
                return new OSOBNIK(new ArrayList<>(sciezka), new ArrayList<>(sciezka_wezly));
            }

            iter++;
            if (iter > max_iteracji_los_sciezki) {
                System.out.println("Przekroczono limit iteracji przy szukaniu losowej sciezki.");
                return null;
            }
        }
    }

    private void dodaj_droge_do_sciezki(Wezel akt_wez, Wezel new_wez, List<Droga> sciezka) {
        for (Long idDrogi : akt_wez.drogiIDs) {
            Droga droga = drogi.get(idDrogi);

            if (droga == null) {
                continue;
            }

            if ((droga.pkt_start.ID == akt_wez.ID && droga.pkt_koniec.ID == new_wez.ID)
                    || (droga.pkt_koniec.ID == akt_wez.ID && droga.pkt_start.ID == new_wez.ID)) {
                sciezka.add(droga);
                return;
            }
        }

        throw new IllegalArgumentException("nie znaleziono drogi pomiedzy dwoma wezlami");
    }

    private List<Long> mozliwe_kolejne_wezly(Wezel wezel, Set<Long> uzyte_wez) {
        List<Long> mozliwe_kierunki = new ArrayList<>();

        for (Long idDrogi : wezel.drogiIDs) {
            Droga droga = drogi.get(idDrogi);

            if (droga == null) {
                continue;
            }

            if (droga.pkt_start.ID == wezel.ID) {
                mozliwe_kierunki.add(droga.pkt_koniec.ID);
            } else if (droga.pkt_koniec.ID == wezel.ID) {
                mozliwe_kierunki.add(droga.pkt_start.ID);
            }
        }

        mozliwe_kierunki.removeAll(uzyte_wez);
        return mozliwe_kierunki;
    }

    private Long wylosuj_wezel_id(List<Long> mozliwe_kolejne_wezly) {
        if (mozliwe_kolejne_wezly.isEmpty()) {
            throw new IllegalArgumentException("mozliwe_kolejne_wezly puste");
        }
        return mozliwe_kolejne_wezly.get(randomInt(0, mozliwe_kolejne_wezly.size() - 1));
    }

    private void dodaj_wezel_do_uzytych(Set<Long> uzyte, Wezel akt_wez) {
        uzyte.add(akt_wez.ID);
    }

    private Wezel aktualizuj_wezel(Wezel akt_wez, List<Droga> sciezka) {
        Droga ostatnia_droga = sciezka.get(sciezka.size() - 1);

        if (akt_wez.ID == ostatnia_droga.pkt_start.ID) {
            return wezly.get(ostatnia_droga.pkt_koniec.ID);
        } else if (akt_wez.ID == ostatnia_droga.pkt_koniec.ID) {
            return wezly.get(ostatnia_droga.pkt_start.ID);
        } else {
            throw new IllegalArgumentException("aktualizuj_wezel() nie znalazla przeciwnego konca drogi");
        }
    }

    private void init_pop() {
        this.populacja.clear();

        while (this.populacja.size() < this.ilosc_osobnikow) {
            OSOBNIK nowy = this.znajdz_losowa_sciezke(pktStart, pktKoniec);
            if (nowy != null) {
                this.populacja.add(nowy);
            } else {
                break;
            }
        }
    }

    private void ocen_osobnikow() {
        for (OSOBNIK osob : this.populacja) {
            if (osob != null) {
                osob.ocenOsobnika();
            }
        }
    }

    private OSOBNIK znajdz_najlepszego() {
        if (populacja == null || populacja.isEmpty()) {
            return null;
        }

        OSOBNIK najlepszy = null;

        for (OSOBNIK os : populacja) {
            if (os == null) {
                continue;
            }

            if (najlepszy == null || os.ocena < najlepszy.ocena) {
                najlepszy = os;
            }
        }

        return najlepszy;
    }

    private List<OSOBNIK> selekcja_PolowaNajlepszych() {
        List<OSOBNIK> kopia = new ArrayList<>();

        for (OSOBNIK os : populacja) {
            if (os != null) {
                kopia.add(os);
            }
        }

        kopia.sort(Comparator.comparingDouble(osobnik -> osobnik.ocena));

        int ile = Math.max(1, kopia.size() / 2);
        return new ArrayList<>(kopia.subList(0, ile));
    }

    private List<OSOBNIK> operacja_krzyzowanie(List<OSOBNIK> pop) {
        List<OSOBNIK> skrzyzowane = new ArrayList<>();

        if (pop == null || pop.size() < 2) {
            return skrzyzowane;
        }

        for (int i = 0; i < pop.size() - 1; i++) {
            if (Math.random() < this.szansaKrzyzowanie) {
                OSOBNIK dziecko = krzyzuj(pop.get(i), pop.get(i + 1));
                if (dziecko != null) {
                    skrzyzowane.add(dziecko);
                }
            }
        }

        if (pop.size() > 2 && Math.random() < this.szansaKrzyzowanie) {
            OSOBNIK dziecko = krzyzuj(pop.get(0), pop.get(pop.size() - 1));
            if (dziecko != null) {
                skrzyzowane.add(dziecko);
            }
        }

        return skrzyzowane;
    }

    private OSOBNIK krzyzuj(OSOBNIK os1, OSOBNIK os2) {
        if (os1 == null || os2 == null) {
            return null;
        }

        if (os1.trasa_drogi == null || os1.trasa_wezly == null || os2.trasa_drogi == null || os2.trasa_wezly == null) {
            return null;
        }

        if (os1.trasa_drogi.size() < 2 || os1.trasa_wezly.size() < 3
                || os2.trasa_drogi.size() < 2 || os2.trasa_wezly.size() < 3) {
            return null;
        }

        int punkt1 = randomInt(1, os1.trasa_drogi.size() - 1);
        int punkt2 = randomInt(1, os2.trasa_drogi.size() - 1);

        List<Droga> czesc1d = new ArrayList<>(os1.trasa_drogi.subList(0, punkt1));
        List<Wezel> czesc1w = new ArrayList<>(os1.trasa_wezly.subList(0, punkt1 + 1));

        List<Droga> czesc2d = new ArrayList<>(os2.trasa_drogi.subList(punkt2, os2.trasa_drogi.size()));
        List<Wezel> czesc2w = new ArrayList<>(os2.trasa_wezly.subList(punkt2, os2.trasa_wezly.size()));

        Wezel koniecCzesci1 = czesc1w.get(czesc1w.size() - 1);
        Wezel startCzesci2 = czesc2w.get(0);

        OSOBNIK lacznik = znajdz_losowa_sciezke(koniecCzesci1, startCzesci2);
        if (lacznik == null) {
            return null;
        }

        List<Droga> noweDrogi = new ArrayList<>();
        List<Wezel> noweWezly = new ArrayList<>();

        noweDrogi.addAll(czesc1d);
        noweDrogi.addAll(lacznik.trasa_drogi);
        noweDrogi.addAll(czesc2d);

        noweWezly.addAll(czesc1w);

        if (!lacznik.trasa_wezly.isEmpty()) {
            List<Wezel> wezlyLacznika = new ArrayList<>(lacznik.trasa_wezly);
            wezlyLacznika.remove(0);
            noweWezly.addAll(wezlyLacznika);
        }

        if (!czesc2w.isEmpty()) {
            List<Wezel> wezlyCzesci2 = new ArrayList<>(czesc2w);
            wezlyCzesci2.remove(0);
            noweWezly.addAll(wezlyCzesci2);
        }

        return new OSOBNIK(noweDrogi, noweWezly);
    }

    private void operacja_mutacja(List<OSOBNIK> pop) {
        for (int i = 0; i < pop.size(); i++) {
            OSOBNIK osob = pop.get(i);

            if (osob == null) {
                continue;
            }

            if (Math.random() >= this.szansaMutacja) {
                continue;
            }

            if (osob.trasa_drogi == null || osob.trasa_wezly == null) {
                continue;
            }

            if (osob.trasa_drogi.size() < 2 || osob.trasa_wezly.size() < 3) {
                continue;
            }

            int punkt_ciecia = randomInt(1, osob.trasa_drogi.size() - 1);

            List<Droga> prefixDrogi = new ArrayList<>(osob.trasa_drogi.subList(0, punkt_ciecia));
            List<Wezel> prefixWezly = new ArrayList<>(osob.trasa_wezly.subList(0, punkt_ciecia + 1));

            Wezel ostatni_wezel = prefixWezly.get(prefixWezly.size() - 1);
            OSOBNIK nowyOgon = znajdz_losowa_sciezke(ostatni_wezel, this.pktKoniec);

            if (nowyOgon == null) {
                continue;
            }

            List<Droga> noweDrogi = new ArrayList<>(prefixDrogi);
            noweDrogi.addAll(nowyOgon.trasa_drogi);

            List<Wezel> noweWezly = new ArrayList<>(prefixWezly);
            if (!nowyOgon.trasa_wezly.isEmpty()) {
                List<Wezel> ogonWezly = new ArrayList<>(nowyOgon.trasa_wezly);
                ogonWezly.remove(0);
                noweWezly.addAll(ogonWezly);
            }

            pop.set(i, new OSOBNIK(noweDrogi, noweWezly));
        }
    }

    private void uzupelnij_populacje() {
        while (populacja.size() < ilosc_osobnikow) {
            OSOBNIK nowy = znajdz_losowa_sciezke(pktStart, pktKoniec);
            if (nowy != null) {
                populacja.add(nowy);
            } else {
                break;
            }
        }

        if (populacja.size() > ilosc_osobnikow) {
            populacja = new ArrayList<>(populacja.subList(0, ilosc_osobnikow));
        }
    }
}