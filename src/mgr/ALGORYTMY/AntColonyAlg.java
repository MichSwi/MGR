package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;
import mgr.Wezel.Polaczenie;

public class AntColonyAlg {

    private double alpha = 0.8;
    private double beta = 1.6;
    private double EPS = 0.000001;

    private double parowanie = 0.3;
    private double Q = 1000.0;

    private double FEROMON_STARTOWY = 1.0;
    private double FEROMON_MIN = 0.0001;
    private double FEROMON_MAX = 100000.0;

    private int ILOSC_MROWEK = 30;
    private int MAX_ITER = 120;
    private int MAX_KROKOW_MROWKI = 3000;
    private int MAX_BACKTRACKING_MROWKI = 300;

    private Map<Long, Double> feromony = new HashMap<>();

    private Long pkt_start;
    private Long pkt_koniec;

    private Mrowka najlepszaGlobalnie = null;

    private ArrayList<MetrykaIteracji> historiaIteracji = new ArrayList<>();

    public AntColonyAlg() {
        inicjalizujFeromony();
    }

    public ArrayList<Droga> start() {

        System.out.println("==================== START ACO ====================");

        if (DANE.wezelStartowyAlgorytmu == null || DANE.wezelKoncowyAlgorytmu == null) {
            System.out.println("ACO: brak wezla startowego albo koncowego.");
            return new ArrayList<>();
        }

        pkt_start = DANE.wezelStartowyAlgorytmu.ID;
        pkt_koniec = DANE.wezelKoncowyAlgorytmu.ID;

        if (!DANE.wezly.containsKey(pkt_start) || !DANE.wezly.containsKey(pkt_koniec)) {
            System.out.println("ACO: wezel startowy albo koncowy nie istnieje w DANE.wezly.");
            return new ArrayList<>();
        }

        if (pkt_start.equals(pkt_koniec)) {
            System.out.println("ACO: start i koniec sa tym samym wezlem.");
            return new ArrayList<>();
        }

        inicjalizujFeromony();
        historiaIteracji.clear();
        najlepszaGlobalnie = null;

        for (int iter = 0; iter < MAX_ITER; iter++) {

            ArrayList<Mrowka> udaneMrowki = new ArrayList<>();
            MetrykaIteracji metrykaIteracji = new MetrykaIteracji(iter);

            for (int i = 0; i < ILOSC_MROWEK; i++) {

                Mrowka mrowka = zbudujTraseMrowki();

                mrowka.numerIteracji = iter;
                mrowka.numerMrowki = i;

                metrykaIteracji.wszystkieMrowki.add(mrowka);

                if (mrowka.dotarlaDoCelu) {

                    uzupelnijKosztTrasy(mrowka);

                    if (mrowka.koszt <= 0.0 || Double.isNaN(mrowka.koszt) || Double.isInfinite(mrowka.koszt)) {
                        mrowka.dotarlaDoCelu = false;
                        mrowka.powodNiepowodzenia = "bledny_koszt_trasy";
                    } else {

                        udaneMrowki.add(mrowka);
                        metrykaIteracji.udaneMrowki.add(mrowka);

                        if (najlepszaGlobalnie == null || mrowka.koszt < najlepszaGlobalnie.koszt) {
                            najlepszaGlobalnie = mrowka;

                            System.out.println("ACO: nowa najlepsza trasa | iteracja: " + iter
                                    + " | mrowka: " + i
                                    + " | koszt/czas: " + najlepszaGlobalnie.koszt
                                    + " | dlugosc: " + najlepszaGlobalnie.dlugosc
                                    + " | wezly: " + najlepszaGlobalnie.trasaWezlow.size()
                                    + " | backtracking: " + najlepszaGlobalnie.liczbaBacktrackingow);
                        }
                    }
                }
            }

            odparujFeromony();

            if (!udaneMrowki.isEmpty()) {
                dodajFeromonyUdanychMrowek(udaneMrowki, metrykaIteracji);
            }

            if (najlepszaGlobalnie != null) {
                metrykaIteracji.najlepszyGlobalnyPoIteracji = najlepszaGlobalnie.koszt;
            }

            historiaIteracji.add(metrykaIteracji);

            WYNIKI.wartosci_feromonow_na_drogach = new HashMap<>(feromony);

            System.out.println("ACO iteracja " + iter
                    + " | udane mrowki: " + udaneMrowki.size()
                    + " / " + ILOSC_MROWEK
                    + " | najlepsza globalnie: "
                    + (najlepszaGlobalnie == null ? "-" : najlepszaGlobalnie.koszt));
        }

        printMetryki();

        if (najlepszaGlobalnie == null) {
            System.out.println("ACO: nie znaleziono trasy.");
            return new ArrayList<>();
        }

        TRASA wynik = TRASA.stworz_z_wezlow_id(najlepszaGlobalnie.trasaWezlow);
        
        DANE.ALG_SCIEZKA = new ArrayList<>(wynik.trasa_drogi);

        System.out.println("ACO: znaleziono trase.");
        System.out.println("Koszt/czas: " + najlepszaGlobalnie.koszt);
        System.out.println("Dlugosc: " + najlepszaGlobalnie.dlugosc);
        System.out.println("Wezly: " + najlepszaGlobalnie.trasaWezlow);
        System.out.println("Drogi: " + najlepszaGlobalnie.trasaDrog);

        return new ArrayList<>(wynik.trasa_drogi);
    }

    private void inicjalizujFeromony() {

        feromony.clear();

        for (Long dr_id : DANE.drogi.keySet()) {
            feromony.put(dr_id, FEROMON_STARTOWY);
        }
    }

    private Mrowka zbudujTraseMrowki() {

        Mrowka mrowka = new Mrowka();

        Long aktualnyWezel = pkt_start;

        mrowka.trasaWezlow.add(aktualnyWezel);
        mrowka.odwiedzone.add(aktualnyWezel);

        int krok = 0;
        int licznikBacktrackingu = 0;

        while (!aktualnyWezel.equals(pkt_koniec) && krok < MAX_KROKOW_MROWKI) {

            krok++;

            Ruch ruch = wybierzKolejnyRuch(
                    aktualnyWezel,
                    mrowka.odwiedzone,
                    mrowka.zablokowaneRuchy
            );

            if (ruch == null) {

                boolean cofniecieOk = wykonajBacktracking(mrowka);

                if (!cofniecieOk) {
                    mrowka.powodNiepowodzenia = "brak_ruchu_i_brak_backtrackingu";
                    return mrowka;
                }

                licznikBacktrackingu++;
                mrowka.liczbaBacktrackingow++;

                if (licznikBacktrackingu > MAX_BACKTRACKING_MROWKI) {
                    mrowka.powodNiepowodzenia = "przekroczono_MAX_BACKTRACKING_MROWKI";
                    return mrowka;
                }

                aktualnyWezel = mrowka.trasaWezlow.get(mrowka.trasaWezlow.size() - 1);
                continue;
            }

            mrowka.trasaWezlow.add(ruch.wezelId);
            mrowka.trasaDrog.add(ruch.drogaId);
            mrowka.odwiedzone.add(ruch.wezelId);

            mrowka.koszt += kosztOdcinkaHeurystyki(ruch.drogaId);

            aktualnyWezel = ruch.wezelId;
        }

        if (aktualnyWezel.equals(pkt_koniec)) {
            mrowka.dotarlaDoCelu = true;
            mrowka.powodNiepowodzenia = "-";
            return mrowka;
        }

        mrowka.powodNiepowodzenia = "przekroczono_MAX_KROKOW_MROWKI";
        return mrowka;
    }

    private Ruch wybierzKolejnyRuch(Long aktualnyWezel,
            Set<Long> odwiedzone,
            Map<Long, Set<Long>> zablokowaneRuchy) {

        Wezel wezel = DANE.wezly.get(aktualnyWezel);

        if (wezel == null || wezel.polaczenia == null || wezel.polaczenia.isEmpty()) {
            return null;
        }

        Set<Long> zablokowaniSasiedzi = zablokowaneRuchy.getOrDefault(aktualnyWezel, new HashSet<>());

        ArrayList<Ruch> kandydaci = new ArrayList<>();
        double sumaOcen = 0.0;

        for (Polaczenie pol : wezel.polaczenia) {

            if (!pol.przejazd) {
                continue;
            }

            Long drogaId = pol.IDdrogi;
            Long kolejnyWezel = pol.kolejnyWezel;

            if (drogaId == null || kolejnyWezel == null) {
                continue;
            }

            if (!DANE.drogi.containsKey(drogaId) || !DANE.wezly.containsKey(kolejnyWezel)) {
                continue;
            }

            if (odwiedzone.contains(kolejnyWezel)) {
                continue;
            }

            if (zablokowaniSasiedzi.contains(kolejnyWezel)) {
                continue;
            }

            double feromon = feromony.getOrDefault(drogaId, FEROMON_STARTOWY);
            double heurystyka = obliczHeurystyke(aktualnyWezel, kolejnyWezel, drogaId);

            double ocena = Math.pow(feromon, alpha) * Math.pow(heurystyka, beta);

            if (Double.isNaN(ocena) || Double.isInfinite(ocena) || ocena <= 0.0) {
                continue;
            }

            Ruch ruch = new Ruch(drogaId, kolejnyWezel, ocena);
            kandydaci.add(ruch);
            sumaOcen += ocena;
        }

        if (kandydaci.isEmpty()) {
            return null;
        }

        double los = Math.random() * sumaOcen;
        double sumaNarastajaca = 0.0;

        for (Ruch ruch : kandydaci) {
            sumaNarastajaca += ruch.ocena;

            if (sumaNarastajaca >= los) {
                return ruch;
            }
        }

        return kandydaci.get(kandydaci.size() - 1);
    }

    private double obliczHeurystyke(Long aktualnyWezel, Long kolejnyWezel, Long drogaId) {

        double kosztOdcinka = kosztOdcinkaHeurystyki(drogaId);

        double dystansAktualny = odlegloscWLiniProstej(aktualnyWezel, pkt_koniec);
        double dystansNowy = odlegloscWLiniProstej(kolejnyWezel, pkt_koniec);

        double postepWLiniiProstej = dystansAktualny - dystansNowy;

        if (postepWLiniiProstej <= 0.0) {
            postepWLiniiProstej = EPS;
        }

        double stosunekKosztuDoPostepu = kosztOdcinka / (postepWLiniiProstej + EPS);

        double heurystyka = 1.0 / (stosunekKosztuDoPostepu + EPS);

        if (heurystyka <= 0.0 || Double.isNaN(heurystyka) || Double.isInfinite(heurystyka)) {
            return EPS;
        }

        return heurystyka;
    }

    private boolean wykonajBacktracking(Mrowka mrowka) {

        if (mrowka.trasaWezlow.size() <= 1) {
            return false;
        }

        Long usuwanyWezel = mrowka.trasaWezlow.remove(mrowka.trasaWezlow.size() - 1);
        Long poprzedniWezel = mrowka.trasaWezlow.get(mrowka.trasaWezlow.size() - 1);

        if (!mrowka.trasaDrog.isEmpty()) {
            Long usuwanaDroga = mrowka.trasaDrog.remove(mrowka.trasaDrog.size() - 1);
            mrowka.koszt -= kosztOdcinkaHeurystyki(usuwanaDroga);

            if (mrowka.koszt < 0.0) {
                mrowka.koszt = 0.0;
            }
        }

        mrowka.odwiedzone.remove(usuwanyWezel);

        mrowka.zablokowaneRuchy
                .computeIfAbsent(poprzedniWezel, k -> new HashSet<>())
                .add(usuwanyWezel);

        return true;
    }

    private void odparujFeromony() {

        for (Map.Entry<Long, Double> entry : feromony.entrySet()) {

            double nowyFeromon = entry.getValue() * (1.0 - parowanie);

            if (nowyFeromon < FEROMON_MIN) {
                nowyFeromon = FEROMON_MIN;
            }

            entry.setValue(nowyFeromon);
        }
    }

    private void dodajFeromonyUdanychMrowek(ArrayList<Mrowka> udaneMrowki, MetrykaIteracji metrykaIteracji) {

        if (udaneMrowki == null || udaneMrowki.isEmpty()) {
            return;
        }

        Mrowka najlepszaIteracji = getNajlepszaMrowka(udaneMrowki);

        metrykaIteracji.najlepszaMrowkaIteracji = najlepszaIteracji.numerMrowki;
        metrykaIteracji.najlepszyKosztIteracji = najlepszaIteracji.koszt;

        double lacznyDodanyFeromon = 0.0;

        for (Mrowka mrowka : udaneMrowki) {

            double dodanyFeromonNaDroge = dodajFeromonDlaMrowki(mrowka);

            mrowka.czyDodalaFeromon = true;
            mrowka.feromonNaDroge = dodanyFeromonNaDroge;
            mrowka.feromonLacznie = dodanyFeromonNaDroge * mrowka.trasaDrog.size();

            lacznyDodanyFeromon += mrowka.feromonLacznie;
        }

        metrykaIteracji.feromonDodanyLacznie = lacznyDodanyFeromon;
        metrykaIteracji.feromonSrednioNaUdanaMrowke = lacznyDodanyFeromon / udaneMrowki.size();
    }

    private double dodajFeromonDlaMrowki(Mrowka mrowka) {

        if (mrowka == null || mrowka.koszt <= 0.0) {
            return 0.0;
        }

        double iloscFeromonu = Q / mrowka.koszt;

        for (Long drogaId : mrowka.trasaDrog) {

            double aktualny = feromony.getOrDefault(drogaId, FEROMON_STARTOWY);
            double nowy = aktualny + iloscFeromonu;

            if (nowy > FEROMON_MAX) {
                nowy = FEROMON_MAX;
            }

            feromony.put(drogaId, nowy);
        }

        return iloscFeromonu;
    }

    private Mrowka getNajlepszaMrowka(ArrayList<Mrowka> mrowki) {

        Mrowka najlepsza = mrowki.get(0);

        for (Mrowka m : mrowki) {
            if (m.koszt < najlepsza.koszt) {
                najlepsza = m;
            }
        }

        return najlepsza;
    }

    private void uzupelnijKosztTrasy(Mrowka mrowka) {

        TRASA trasa = TRASA.stworz_z_wezlow_id(mrowka.trasaWezlow);

        if (trasa == null) {
            return;
        }

        mrowka.koszt = trasa.czas_przejazdu;
        mrowka.dlugosc = trasa.dlugosc;
    }

    private double kosztOdcinkaHeurystyki(Long drogaId) {

        Droga droga = DANE.drogi.get(drogaId);

        if (droga == null) {
            return 999999999.0;
        }

        if (droga.czas_przejazdu <= 0.0) {
            return EPS;
        }

        return droga.czas_przejazdu;
    }

    private double odlegloscWLiniProstej(Long id1, Long id2) {

        Wezel w1 = DANE.wezly.get(id1);
        Wezel w2 = DANE.wezly.get(id2);

        if (w1 == null || w2 == null) {
            return 999999999.0;
        }

        double dx = w1.X - w2.X;
        double dy = w1.Y - w2.Y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    private void printMetryki() {

        System.out.println("");
        System.out.println("==================== METRYKI ACO ====================");

        if (historiaIteracji == null || historiaIteracji.isEmpty()) {
            System.out.println("Brak zapisanych iteracji.");
            return;
        }

        int wszystkieUdane = 0;
        int wszystkieNieudane = 0;

        double najlepszyKosztGlobalnie = Double.MAX_VALUE;
        double najgorszyKosztGlobalnie = -Double.MAX_VALUE;
        double sumaKosztowGlobalnie = 0.0;

        for (MetrykaIteracji iteracja : historiaIteracji) {

            System.out.println("");
            System.out.println("========== ITERACJA " + iteracja.numerIteracji + " ==========");

            int udane = iteracja.udaneMrowki.size();
            int wszystkie = iteracja.wszystkieMrowki.size();
            int nieudane = wszystkie - udane;

            wszystkieUdane += udane;
            wszystkieNieudane += nieudane;

            System.out.println("Mrowki lacznie: " + wszystkie);
            System.out.println("Udane mrowki: " + udane + " / " + ILOSC_MROWEK);
            System.out.println("Nieudane mrowki: " + nieudane);

            if (wszystkie > 0) {
                double skutecznoscIteracji = 100.0 * udane / wszystkie;
                System.out.printf("Skutecznosc iteracji: %.2f%%%n", skutecznoscIteracji);
            }

            System.out.println("Najlepsza mrowka iteracji: " + iteracja.najlepszaMrowkaIteracji);
            System.out.printf("Najlepszy koszt iteracji: %.3f%n", iteracja.najlepszyKosztIteracji);
            System.out.printf("Feromon dodany lacznie w iteracji: %.8f%n", iteracja.feromonDodanyLacznie);
            System.out.printf("Feromon srednio na udana mrowke: %.8f%n", iteracja.feromonSrednioNaUdanaMrowke);
            System.out.printf("Najlepszy globalny po iteracji: %.3f%n", iteracja.najlepszyGlobalnyPoIteracji);

            if (udane == 0) {
                System.out.println("Brak poprawnych tras w tej iteracji.");
                continue;
            }

            double sumaKosztow = 0.0;
            double najlepszyKoszt = Double.MAX_VALUE;
            double najgorszyKoszt = -Double.MAX_VALUE;

            double sumaFeromonu = 0.0;
            double sumaFeromonuNaDroge = 0.0;

            int sumaWezlow = 0;
            int sumaDrog = 0;
            int sumaBacktrackingow = 0;

            for (Mrowka mrowka : iteracja.wszystkieMrowki) {

                if (!mrowka.dotarlaDoCelu) {
                    continue;
                }

                double koszt = mrowka.koszt;

                sumaKosztow += koszt;
                sumaKosztowGlobalnie += koszt;

                if (koszt < najlepszyKoszt) {
                    najlepszyKoszt = koszt;
                }

                if (koszt > najgorszyKoszt) {
                    najgorszyKoszt = koszt;
                }

                if (koszt < najlepszyKosztGlobalnie) {
                    najlepszyKosztGlobalnie = koszt;
                }

                if (koszt > najgorszyKosztGlobalnie) {
                    najgorszyKosztGlobalnie = koszt;
                }

                sumaFeromonu += mrowka.feromonLacznie;
                sumaFeromonuNaDroge += mrowka.feromonNaDroge;

                sumaWezlow += mrowka.trasaWezlow.size();
                sumaDrog += mrowka.trasaDrog.size();
                sumaBacktrackingow += mrowka.liczbaBacktrackingow;
            }

            double sredniKoszt = sumaKosztow / udane;
            double sredniFeromon = sumaFeromonu / udane;
            double sredniFeromonNaDroge = sumaFeromonuNaDroge / udane;
            double sredniaLiczbaWezlow = (double) sumaWezlow / udane;
            double sredniaLiczbaDrog = (double) sumaDrog / udane;
            double sredniBacktracking = (double) sumaBacktrackingow / udane;

            System.out.println("");
            System.out.printf("Sredni koszt/czas tras w iteracji: %.3f%n", sredniKoszt);
            System.out.printf("Najlepszy koszt/czas w iteracji: %.3f%n", najlepszyKoszt);
            System.out.printf("Najgorszy koszt/czas w iteracji: %.3f%n", najgorszyKoszt);
            System.out.printf("Srednia liczba wezlow: %.2f%n", sredniaLiczbaWezlow);
            System.out.printf("Srednia liczba drog: %.2f%n", sredniaLiczbaDrog);
            System.out.printf("Sredni feromon dodany przez mrowki na droge: %.8f%n", sredniFeromonNaDroge);
            System.out.printf("Sredni feromon dodany przez mrowki lacznie: %.8f%n", sredniFeromon);
            System.out.printf("Srednia liczba backtrackingow: %.2f%n", sredniBacktracking);
        }

        System.out.println("");
        System.out.println("==================== PODSUMOWANIE ACO ====================");

        int wszystkieMrowki = wszystkieUdane + wszystkieNieudane;

        System.out.println("Liczba iteracji: " + historiaIteracji.size());
        System.out.println("Liczba wszystkich mrowek: " + wszystkieMrowki);
        System.out.println("Liczba udanych mrowek: " + wszystkieUdane);
        System.out.println("Liczba nieudanych mrowek: " + wszystkieNieudane);

        if (wszystkieMrowki > 0) {
            double skutecznosc = 100.0 * wszystkieUdane / wszystkieMrowki;
            System.out.printf("Skutecznosc mrowek: %.2f%%%n", skutecznosc);
        }

        if (wszystkieUdane > 0) {
            double sredniKosztGlobalnie = sumaKosztowGlobalnie / wszystkieUdane;

            System.out.printf("Sredni koszt/czas wszystkich udanych tras: %.3f%n", sredniKosztGlobalnie);
            System.out.printf("Najlepszy koszt/czas globalnie: %.3f%n", najlepszyKosztGlobalnie);
            System.out.printf("Najgorszy koszt/czas globalnie: %.3f%n", najgorszyKosztGlobalnie);
        }

        if (najlepszaGlobalnie != null) {
            System.out.println("");
            System.out.println("Najlepsza trasa globalnie:");
            System.out.println("Koszt/czas: " + najlepszaGlobalnie.koszt);
            System.out.println("Dlugosc: " + najlepszaGlobalnie.dlugosc);
            System.out.println("Liczba wezlow: " + najlepszaGlobalnie.trasaWezlow.size());
            System.out.println("Liczba drog: " + najlepszaGlobalnie.trasaDrog.size());
            System.out.println("Backtracking: " + najlepszaGlobalnie.liczbaBacktrackingow);
            System.out.println("Wezly: " + najlepszaGlobalnie.trasaWezlow);
            System.out.println("Drogi: " + najlepszaGlobalnie.trasaDrog);
        }

        System.out.println("===========================================================");
    }

    private static class Ruch {

        Long drogaId;
        Long wezelId;
        double ocena;

        Ruch(Long drogaId, Long wezelId, double ocena) {
            this.drogaId = drogaId;
            this.wezelId = wezelId;
            this.ocena = ocena;
        }
    }

    private static class Mrowka {

        int numerMrowki = -1;
        int numerIteracji = -1;

        ArrayList<Long> trasaWezlow = new ArrayList<>();
        ArrayList<Long> trasaDrog = new ArrayList<>();

        Set<Long> odwiedzone = new HashSet<>();

        Map<Long, Set<Long>> zablokowaneRuchy = new HashMap<>();

        double koszt = 0.0;
        double dlugosc = 0.0;

        boolean czyDodalaFeromon = false;
        double feromonNaDroge = 0.0;
        double feromonLacznie = 0.0;

        int liczbaBacktrackingow = 0;

        boolean dotarlaDoCelu = false;
        String powodNiepowodzenia = "-";
    }

    private static class MetrykaIteracji {

        int numerIteracji;

        double najlepszyGlobalnyPoIteracji = -1.0;

        int najlepszaMrowkaIteracji = -1;
        double najlepszyKosztIteracji = -1.0;

        double feromonDodanyLacznie = 0.0;
        double feromonSrednioNaUdanaMrowke = 0.0;

        ArrayList<Mrowka> wszystkieMrowki = new ArrayList<>();
        ArrayList<Mrowka> udaneMrowki = new ArrayList<>();

        MetrykaIteracji(int numerIteracji) {
            this.numerIteracji = numerIteracji;
        }
    }
}
