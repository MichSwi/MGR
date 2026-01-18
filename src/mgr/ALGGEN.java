package mgr;

import java.util.List;
import java.util.ArrayList;

public class ALGGEN {

    private List<Droga> drogi;
    private Punkt pktStart;
    private Punkt pktKoniec;
    private Droga drogaStart;
    private Droga drogaKoniec;

    public ALGGEN() {
        this.drogi = DANE.drogi;
        this.pktStart = DANE.punkStartowyAlgorytmu;
        this.pktKoniec = DANE.punkKoncowyAlgorytmu;

        znajdzDrogiPoczatkowe();
    }

    private void znajdzDrogiPoczatkowe() {
        for (Droga dr : drogi) {
            if (pktStart.equals(dr.pkt_start) || pktStart.equals(dr.pkt_koniec)) {
                drogaStart = dr;
            }
            if (pktKoniec.equals(dr.pkt_start) || pktKoniec.equals(dr.pkt_koniec)) {
                drogaKoniec = dr;
            }
        }
    }

    public void inicjalizacja() {
        Droga etap = new Droga();
        List<Droga> sciezka = new ArrayList<>();
        List<Long> uzyteDrogi = new ArrayList<>();
        etap = this.drogaStart;
        sciezka.add(etap);
        int mozliweKierunki = -1;
        int random = -1;
        long wybrane_id = 0;
        int inkrementacja = 0;

        while (!etap.equals(this.drogaKoniec)) {
            System.out.println("nowa iteracja petli glownej" + inkrementacja);
            inkrementacja++;

            // jesli mozliwe drogi juz byly uzyte -> restart,
            // jesli nie -> losowanie
            if (uzyteDrogi.containsAll(etap.polaczenia_ID)) {
                sciezka.clear();
                etap = this.drogaStart;
                sciezka.add(etap);
                inkrementacja++;
                uzyteDrogi.clear();
                System.out.println("wszystkie mozliwe dalsze sceizki uzyte, restart");

                System.out.println("losowanie kolejnej drogi, ale od poczatkowej drogi");
                mozliweKierunki = etap.polaczenia_ID.size();
                random = new java.util.Random().nextInt(mozliweKierunki);
                wybrane_id = etap.polaczenia_ID.get(random);
                System.out.println("wylosowalem" + wybrane_id);;
            } else {
                // losowanie kolejnej drogi

                System.out.println("losowanie kolejnej drogi");
                mozliweKierunki = etap.polaczenia_ID.size();
                random = new java.util.Random().nextInt(mozliweKierunki);
                wybrane_id = etap.polaczenia_ID.get(random);
                System.out.println("wylosowalem" + wybrane_id);
            }

            //szukam wylosowanej drogi i ja dodaje do sciezki, zmieniam etap
            for (Droga dr : drogi) {
                if (dr.ID == wybrane_id) {
                    sciezka.add(dr);
                    etap = dr;
                    uzyteDrogi.add(dr.ID);
                    System.out.println("znalazlem wylosowana droge");
                    break;
                }
            }

            //jesli zagalopowal sie za daleko -> restart
            if (sciezka.size() > 20) {
                sciezka.clear();
                etap = this.drogaStart;
                sciezka.add(etap);
                inkrementacja++;
                System.out.println("restart bo za dluga droga");
                uzyteDrogi.clear();
            }
        }
        System.out.println("ZNALEZIONO");
        System.out.println(sciezka);
    }

    void inicjalizacja2() {
        Droga fragment = new Droga();
        List<Droga> sciezka = new ArrayList<>();

        int random = -1;
        long wybrane_id = 0;

        fragment = this.drogaStart;
        sciezka.add(fragment);

        while (true) {
            // zakonczenie algorytmu
            if (fragment == this.drogaKoniec) {
                System.out.println("ZNALEZIONO");
                System.out.println(sciezka);
                break;
            }
            
            
            
        }
        System.out.println("zakonczono petle glowna");
    }

    public void start() {
        // tu uruchom algorytm genetyczny
        // np. inicjalizacja populacji itd.
    }
}
