package mgr.ALGORYTMY;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;

public class ALGGEN {

    private int max_dlugosc_sciezki = 3000;
    private int max_iteracji = 40000;
    private int ilosc_osobnikow = 10;
    private List<OSOBNIK> populacja = new ArrayList<>(); 

    public Map<Long, Droga> drogi = new HashMap<>();
 
    private Wezel pktStart;
    private Wezel pktKoniec;
    private Map<Long, Wezel> wezly = new HashMap<>();
    private List<Droga> SCIEZKA_DROG = new ArrayList<>();
    
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
        
    }

    private List<Droga> znajdz_losowa_sciezke(Wezel pkt_startowy, Wezel pkt_koncowy) {
        if (pkt_startowy == null || pkt_koncowy == null) {
            throw new IllegalArgumentException("brak pkt_start lub pkt_koniec dla znajdz_losowa_sciezke");
        }

        // ta funkcja szuka losowej drogi
        List<Droga> sciezka = new ArrayList<>();
        Set<Long> uzyte_wezly = new HashSet<>();
        List<Long> mozliwe_kolejne_wezly;
        Long wylosowany_wezel_id;
        int iter = 0;

        Wezel aktualny_wezel = pkt_startowy;
        while (true) {
            // znalezc mozliwe kolejne wezly
            mozliwe_kolejne_wezly = mozliwe_kolejne_wezly(aktualny_wezel, uzyte_wezly);
            if (mozliwe_kolejne_wezly.isEmpty()) {
                // jesli brak mozliwych kierunkow -> restart
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
                continue;
            }

            // losowanie drogi z mozliwych
            wylosowany_wezel_id = wylosuj_wezel_id(mozliwe_kolejne_wezly);

            // dodaj droge do sciezki
            dodaj_droge_do_sciezki(aktualny_wezel, wezly.get(wylosowany_wezel_id), sciezka);
            if (sciezka.size() > max_dlugosc_sciezki) {
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
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
                return sciezka;
            }

            iter++;
            if (iter > max_iteracji) {
                aktualny_wezel = pkt_startowy;
                uzyte_wezly.clear();
                sciezka.clear();
                System.out.println("petla WHILE przekroczyl " + this.max_iteracji + " iteracji");
                return new ArrayList<>();
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
        for(int i=1; i<=this.ilosc_osobnikow; i++){
            this.populacja.add(new OSOBNIK(this.znajdz_losowa_sciezke(pktStart, pktKoniec)));
        }
    }

}
