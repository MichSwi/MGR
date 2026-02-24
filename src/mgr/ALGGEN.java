package mgr;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ALGGEN {

    private Map<Long, Droga> drogi = DANE.drogi;

    private Punkt pktStart;
    private Punkt pktKoniec;
    private Droga drogaStart;
    private Droga drogaKoniec;

    private List<Droga> SCIEZKA = new ArrayList<>();
    private Droga AKTUALNY_ETAP;
    private List<Long> UZYTE_DROGI = new ArrayList<>();

    public ALGGEN() {
        this.drogi = DANE.drogi;
        this.pktStart = DANE.punkStartowyAlgorytmu;
        this.pktKoniec = DANE.punkKoncowyAlgorytmu;

        znajdzDrogiPoczatkowe();
    }

    private void znajdzDrogiPoczatkowe() {
        for (Long ID : drogi.keySet()) {
            Droga dr = drogi.get(ID);
//        for (Droga dr : drogi) {
            if (pktStart.equals(dr.pkt_start) || pktStart.equals(dr.pkt_koniec)) {
                drogaStart = dr;
            }
            if (pktKoniec.equals(dr.pkt_start) || pktKoniec.equals(dr.pkt_koniec)) {
                drogaKoniec = dr;
            }
        }
    }

    private void restart() {
        AKTUALNY_ETAP = this.drogaStart;
        SCIEZKA.clear();
        UZYTE_DROGI.clear();
    }

    private void dodaj_kolejna_sciezke() {
        List<Long> mozliwe_kierunki = AKTUALNY_ETAP.polaczenia_ID;
        while (true) {
            int random = new java.util.Random().nextInt(mozliwe_kierunki.size());
            Long wylosowane_id = AKTUALNY_ETAP.polaczenia_ID.get(random);
            if (!UZYTE_DROGI.contains(wylosowane_id)) {
                // wylosowany nowy
                System.out.println("========WYLOSOWANY NOWY=====");

                for (Long id : drogi.keySet()) {
                    Droga dr = drogi.get(id);
                    if (dr.ID == wylosowane_id) {
                        SCIEZKA.add(dr);
                        AKTUALNY_ETAP = dr;
                        break;
                    }
                }
                break;
            } else if (UZYTE_DROGI.containsAll(mozliwe_kierunki)) {
                // wszystkie byly juz wylosowane
                System.out.println("wszystkie byly juz wylosowane, restart");
                restart();
                break;
            }

            // jesli zawiera wylosowana -> losuj dalej
        }
    }

    public void start() {
        UZYTE_DROGI.clear();
        SCIEZKA.clear();
        AKTUALNY_ETAP = this.drogaStart;

        int iter = 0;
        while (true) {

            dodaj_kolejna_sciezke();

            System.out.println("Dlugosc sciezki: " + SCIEZKA.size() + " Aktualny etap: " + AKTUALNY_ETAP.nazwa + " Szukany: " + this.drogaKoniec.nazwa);
            if (AKTUALNY_ETAP.equals(this.drogaKoniec)) {
                System.out.println("WYZNACZONA SCIEZKA:");
                for (int i = 0; i < SCIEZKA.size(); i++) {
                    System.out.println(SCIEZKA.get(i).nazwa+" "+SCIEZKA.get(i).ID);
                }
                break;
            }

            if (SCIEZKA.size() > 10) {
                System.out.println("restart bo za dluga");
                restart();
            }

            iter++;
            System.out.println("iteracja glownej petli: " + iter);
        }
        DANE.ALG_GEN_SCIEZKA = this.SCIEZKA;
        System.out.println("Zakonczono");
        
    }
}
