/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;
import mgr.Wezel.Polaczenie;

/**
 *
 * @author Micha
 */
public class LOSOWA_TRASA {

    private static boolean STOP_ALGORYTMU = false;
    private static final Random random = new Random();

// Maksymalne oddalenie aktualnego punktu od celu.
// 1.4 oznacza, że trasa może oddalić się od celu maksymalnie o 140% odległości start-koniec.
    private double MNOZNIK_MAX_ODDALENIA_OD_CELU = 1.2;

// Minimalna wartość maksymalnego oddalenia od celu.
// 300 ozaczna, że trasa ZAWSZE może oddalić się o 300m od celu.
    private double MINIMALNA_WARTOSC_MAX_ODDALENIA = 200;

// Określa, jak długi dystans trasa może przejść bez wyraźnego zbliżania się do celu.
// 0.2 oznacza 20% odległości start-koniec.
    private double MNOZNIK_BRAKU_POSTEPU = 0.3;

// Minimalny dopuszczalny dystans bez postępu.
// Chroni przed zbyt małym limitem przy krótkich trasach.
    private double MINIMALNA_ODLEGLOSC_BRAK_POSTEPU = 200;

// Określa, jaki spadek odległości do celu uznajemy za znaczący postęp.
// 0.1 oznacza 10% odległości start-koniec.
    private double MNOZNIK_ZNACZNEGO_POSTEPU = 0.1;

// Minimalna wartość znaczącego postępu w metrach.
// Nawet jeśli 10% trasy daje małą wartość, postęp musi mieć co najmniej 10 m.
    private double MIN_ZNACZACY_POSTEP = 10.0;

// Opóźnienie animacji między kolejnymi krokami algorytmu w milisekundach.
    private int OPOZNIENIE_MS = 1;

// Maksymalna liczba kroków głównej pętli algorytmu.
    private int MAX_KROKOW = 100000;

// Maksymalna liczba zapamiętanych poprzednich tras.
// Historia służy do sprawdzania, czy nowa trasa nie połączyła się ze starszą próbą.
    private int MAX_HISTORIA_TRAS = 50;

// Maksymalna liczba restartów dla jednej strony wyszukiwania.
    private int MAX_RESTARTOW_NA_STRONE = 500;

// Współczynnik luzowania limitu oddalenia od celu.
// 1.10 oznacza, że po restarcie z powodu zbyt dużego oddalenia
// algorytm zwiększy dopuszczalne oddalenie od celu o 10%.
private double LUZOWANIE_ODDALENIA = 1.05;

// Współczynnik luzowania limitu dystansu bez postępu.
// 1.10 oznacza, że po restarcie z powodu braku postępu
// algorytm pozwoli przejść o 10% dłuższy dystans bez wyraźnego zbliżania się do celu.
private double LUZOWANIE_BRAKU_POSTEPU = 1.1;

// Współczynnik zmniejszania progu znaczącego postępu.
// 0.95 oznacza, że po restarcie z powodu braku postępu
// wymagany postęp zostanie zmniejszony o 5%, więc algorytm łatwiej uzna ruch za postęp.
private double LUZOWANIE_ZNACZNEGO_POSTEPU = 0.96;

    public void startAnimowany(Wezel w_start, Wezel w_koniec, JPanel panelMapy) {
        STOP_ALGORYTMU = false;

        new Thread(() -> {
            TRASA t = szukajTrasyDwukierunkowo(
                    w_start.ID,
                    w_koniec.ID,
                    panelMapy
            );

            if (t != null) {
                DANE.ALG_SCIEZKA = t.trasa_drogi;
                //System.out.println("ZNALEZIONO TRASE ANIMOWANA.");
            } else {
                DANE.ALG_SCIEZKA = new ArrayList<>();
                //System.out.println("NIE ZNALEZIONO TRASY ANIMOWANEJ.");
            }

            odswiezMape(panelMapy);
        }).start();
    }

    public TRASA start(Wezel w_start, Wezel w_koniec) {
        STOP_ALGORYTMU = false;

        TRASA t = szukajTrasyDwukierunkowo(
                w_start.ID,
                w_koniec.ID,
                null
        );

        if (t != null) {
            DANE.ALG_SCIEZKA = t.trasa_drogi;
            //System.out.println("ZNALEZIONO TRASE.");
        } else {
            DANE.ALG_SCIEZKA = new ArrayList<>();
            //System.out.println("NIE ZNALEZIONO TRASY.");
        }
        return t;
    }

    public static void przerwijAlgorytm() {
        STOP_ALGORYTMU = true;
    }

    private static void odswiezMape(JPanel panelMapy) {
        if (panelMapy != null) {
            SwingUtilities.invokeLater(() -> {
                panelMapy.repaint();
            });
        }
    }

    private TRASA szukajTrasyDwukierunkowo(long w_start_id, long w_koniec_id, JPanel panelMapy) {
        // ZABEZPIECZENIA BRAKU ARGUMENTOW

        if (!DANE.wezly.containsKey(w_start_id)) {
            //System.out.println("BLAD: brak wezla startowego.");
            return null;
        }

        if (!DANE.wezly.containsKey(w_koniec_id)) {
            //System.out.println("BLAD: brak wezla koncowego.");
            return null;
        }

        // ZAPISYWANE TRASY
        ArrayList<ArrayList<Long>> historiaTrasOdStartu = new ArrayList<>();
        ArrayList<ArrayList<Long>> historiaTrasOdKonca = new ArrayList<>();

        double odlegloscStartKoniec = zmierz_odl_2pkt(w_start_id, w_koniec_id);

        // WARTOSCI DO OGRANICZEN/KONTROLI PROGRSU TRAS
        double maxOddalenieOdCelu = odlegloscStartKoniec * MNOZNIK_MAX_ODDALENIA_OD_CELU;

        double maxDystansBezPostepu = Math.max(MINIMALNA_ODLEGLOSC_BRAK_POSTEPU, odlegloscStartKoniec * MNOZNIK_BRAKU_POSTEPU);

        double znacznyPostepMetry = Math.max(MIN_ZNACZACY_POSTEP, odlegloscStartKoniec * MNOZNIK_ZNACZNEGO_POSTEPU);

        int restartyOdStartu = 0;
        int restartyOdKonca = 0;

        StanPoszukiwania trasaOdStartu = new StanPoszukiwania("OD STARTU", w_start_id, w_koniec_id);
        StanPoszukiwania trasaOdKonca = new StanPoszukiwania("OD KONCA", w_koniec_id, w_start_id);

        //System.out.println("=======================================");
        //System.out.println("START ALGORYTMU DWUKIERUNKOWEGO Z HISTORIA");
        //System.out.println("Odleglosc start-koniec: " + odlegloscStartKoniec + " m");
        //System.out.println("Max oddalenie od celu: " + maxOddalenieOdCelu + " m");
        //System.out.println("Max dystans bez postepu: " + maxDystansBezPostepu + " m");
        //System.out.println("Znaczny postep: " + znacznyPostepMetry + " m");
        //System.out.println("Historia tras: " + MAX_HISTORIA_TRAS);
        //System.out.println("=======================================");

        rysujTrasy(trasaOdStartu, trasaOdKonca, panelMapy);

        int krok = 0;

        while (krok < MAX_KROKOW) {
            krok++;

            // TRASA OD STARTU
            // zastopowanie algorytmu
            if (STOP_ALGORYTMU) {
                //System.out.println("STOP: algorytm przerwany.");
                return null;
            }

            // sprawdzenie czy istnieje juz trasa
            TRASA znalezniona = sprawdzCzyIsteniejePolaczenieTras(trasaOdStartu, trasaOdKonca, historiaTrasOdStartu, historiaTrasOdKonca);
            if (znalezniona != null) {
                //System.out.println("SUKCES: znaleziono polaczenie tras.");
                return znalezniona;
            }

            String coZrobilaTrasaOdStartu = wykonajKrok(trasaOdStartu, maxOddalenieOdCelu, maxDystansBezPostepu, znacznyPostepMetry);

            rysujTrasy(trasaOdStartu, trasaOdKonca, panelMapy);

            // sprawdzenie czy pod kroku istnieje juz trasa
            znalezniona = sprawdzCzyIsteniejePolaczenieTras(trasaOdStartu, trasaOdKonca, historiaTrasOdStartu, historiaTrasOdKonca);
            if (znalezniona != null) {
                //System.out.println("SUKCES: znaleziono polaczenie tras po wykonaniu kroku trasyOdStartu");
                return znalezniona;
            }

            if (coZrobilaTrasaOdStartu.equals("stop")) {
                //System.out.println("STOP, przerwano po wykonaniu kroku trasyOdStartu");
                return null;
            }

            if (coZrobilaTrasaOdStartu.startsWith("restart")) {
                restartyOdStartu++;

                if (coZrobilaTrasaOdStartu.equals("restart_za_daleko")) {
                    maxOddalenieOdCelu *= LUZOWANIE_ODDALENIA;
                    //System.out.println("Zwiekszono maxOddalenieOdCelu do: " + maxOddalenieOdCelu);
                }

                if (coZrobilaTrasaOdStartu.equals("restart_brak_postepu")
                        || coZrobilaTrasaOdStartu.equals("restart_backtracking_brak_postepu")) {

                    maxDystansBezPostepu *= LUZOWANIE_BRAKU_POSTEPU;
                    znacznyPostepMetry *= LUZOWANIE_ZNACZNEGO_POSTEPU;

                    //System.out.println("Zwiekszono maxDystansBezPostepu do: " + maxDystansBezPostepu);
                    //System.out.println("Zmniejszono znacznyPostepMetry do: " + znacznyPostepMetry);
                }

                //System.out.println("RESTART trasy OD STARTU");
                trasaOdStartu = restartujTrase(trasaOdStartu, historiaTrasOdStartu);

                if (restartyOdStartu > MAX_RESTARTOW_NA_STRONE) {
                    //System.out.println("przekroczono MAX_RESTARTOW_NA_STRONE dla OD STARTU");
                    return null;
                }

                rysujTrasy(trasaOdStartu, trasaOdKonca, panelMapy);
            }

            // TRASA OD KONCA
            // sprawdzenie czy istnieje juz trasa
            znalezniona = sprawdzCzyIsteniejePolaczenieTras(trasaOdStartu, trasaOdKonca, historiaTrasOdStartu, historiaTrasOdKonca);
            if (znalezniona != null) {
                //System.out.println("SUKCES: znaleziono polaczenie tras.");
                return znalezniona;
            }

            String coZrobilaTrasaOdKonca = wykonajKrok(trasaOdKonca, maxOddalenieOdCelu, maxDystansBezPostepu, znacznyPostepMetry);

            rysujTrasy(trasaOdStartu, trasaOdKonca, panelMapy);

            // sprawdzenie czy po kroku istnieje juz trasa
            znalezniona = sprawdzCzyIsteniejePolaczenieTras(trasaOdStartu, trasaOdKonca, historiaTrasOdStartu, historiaTrasOdKonca);
            if (znalezniona != null) {
                //System.out.println("SUKCES: znaleziono polaczenie tras po wykonaniu kroku trasyOdKonca");
                return znalezniona;
            }

            if (coZrobilaTrasaOdKonca.equals("stop")) {
                //System.out.println("STOP, przerwano po wykonaniu kroku trasyOdKonca");
                return null;
            }

            if (coZrobilaTrasaOdKonca.startsWith("restart")) {
                restartyOdKonca++;

                if (coZrobilaTrasaOdKonca.equals("restart_za_daleko")) {
                    maxOddalenieOdCelu *= LUZOWANIE_ODDALENIA;
                    //System.out.println("Zwiekszono maxOddalenieOdCelu do: " + maxOddalenieOdCelu);
                }

                if (coZrobilaTrasaOdKonca.equals("restart_brak_postepu")
                        || coZrobilaTrasaOdKonca.equals("restart_backtracking_brak_postepu")) {

                    maxDystansBezPostepu *= LUZOWANIE_BRAKU_POSTEPU;
                    znacznyPostepMetry *= LUZOWANIE_ZNACZNEGO_POSTEPU;

                    //System.out.println("Zwiekszono maxDystansBezPostepu do: " + maxDystansBezPostepu);
                    //System.out.println("Zmniejszono znacznyPostepMetry do: " + znacznyPostepMetry);
                }

                //System.out.println("RESTART trasy OD KONCA");
                trasaOdKonca = restartujTrase(trasaOdKonca, historiaTrasOdKonca);

                if (restartyOdKonca > MAX_RESTARTOW_NA_STRONE) {
                    //System.out.println("przekroczono MAX_RESTARTOW_NA_STRONE dla OD KONCA");
                    return null;
                }

                rysujTrasy(trasaOdStartu, trasaOdKonca, panelMapy);
            }

        }
        //System.out.println("KONIEC: przekroczono MAX KROKOW PETLI WHILE.");
        return null;
    }

    private static double zmierz_odl_2pkt(Long pkt1_id, Long pkt2_id) {

        double dx = DANE.wezly.get(pkt2_id).X - DANE.wezly.get(pkt1_id).X;
        double dy = DANE.wezly.get(pkt2_id).Y - DANE.wezly.get(pkt1_id).Y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    private void rysujTrasy(StanPoszukiwania trasaOdStartu, StanPoszukiwania trasaOdKonca, JPanel panelMapy) {
        ArrayList<Droga> drogiDoRysowania = new ArrayList<>();

        ArrayList<Long> droga1_jako_wezly_id = new ArrayList<>();
        ArrayList<Long> droga2_jako_wezly_id = new ArrayList<>();

        for (PunktTrasy pkt : trasaOdStartu.przebyta_trasa) {
            droga1_jako_wezly_id.add(pkt.wezel_id);
        }
        for (PunktTrasy pkt : trasaOdKonca.przebyta_trasa) {
            droga2_jako_wezly_id.add(pkt.wezel_id);
        }
        TRASA t1 = TRASA.stworz_z_wezlow_id(droga1_jako_wezly_id);
        TRASA t2 = TRASA.stworz_z_wezlow_id(droga2_jako_wezly_id);

        drogiDoRysowania.addAll(t1.trasa_drogi);
        drogiDoRysowania.addAll(t2.trasa_drogi);

        DANE.ALG_SCIEZKA = drogiDoRysowania;

        odswiezMape(panelMapy);

        if (OPOZNIENIE_MS > 0) {
            try {
                Thread.sleep(OPOZNIENIE_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private TRASA sprawdzCzyIsteniejePolaczenieTras(StanPoszukiwania trasaOdStartu, StanPoszukiwania trasaOdKonca, ArrayList<ArrayList<Long>> historiaTrasOdStartu, ArrayList<ArrayList<Long>> historiaTrasOdKonca) {

        ArrayList<Long> aktualnaStart = trasaOdStartu.pobierzWezly();
        ArrayList<Long> aktualnaKoniec = trasaOdKonca.pobierzWezly();

        // sprawdzenie czy trasy sie lacza
        Long wspolny_wezel_id = znajdzWspolnyWezel(aktualnaStart, aktualnaKoniec);
        if (wspolny_wezel_id != null) {
            //System.out.println("POLACZENIE: aktualna OD STARTU + aktualna OD KONCA, wezel: " + wspolny_wezel_id);
            return polaczListyWezlow(aktualnaStart, aktualnaKoniec, wspolny_wezel_id);
        }

        for (ArrayList<Long> staraKoniec : historiaTrasOdKonca) {
            wspolny_wezel_id = znajdzWspolnyWezel(aktualnaStart, staraKoniec);
            if (wspolny_wezel_id != null) {
                //System.out.println("POLACZENIE: aktualna OD STARTU + historyczna OD KONCA, wezel: " + wspolny_wezel_id);
                return polaczListyWezlow(aktualnaStart, staraKoniec, wspolny_wezel_id);
            }
        }

        for (ArrayList<Long> staraStart : historiaTrasOdStartu) {
            wspolny_wezel_id = znajdzWspolnyWezel(staraStart, aktualnaKoniec);
            if (wspolny_wezel_id != null) {
                //System.out.println("POLACZENIE: historyczna OD STARTU + aktualna OD KONCA, wezel: " + wspolny_wezel_id);
                return polaczListyWezlow(staraStart, aktualnaKoniec, wspolny_wezel_id);
            }
        }

        return null;
    }

    private Long znajdzWspolnyWezel(ArrayList<Long> wezlyA, ArrayList<Long> wezlyB) {
        // szuka wspolnego wezla (polaczenia dwoch tras)

        Set<Long> setB = new HashSet<>(wezlyB);

        for (int i = wezlyA.size() - 1; i >= 0; i--) {
            Long id = wezlyA.get(i);

            if (setB.contains(id)) {
                return id;
            }
        }

        return null;
    }

    private TRASA polaczListyWezlow(ArrayList<Long> listaWezlowOdStartu, ArrayList<Long> listaWezlowOdKonca, Long wspolny_wezel_id) {
        int indexStart = listaWezlowOdStartu.indexOf(wspolny_wezel_id);
        int indexKoniec = listaWezlowOdKonca.indexOf(wspolny_wezel_id);
        ArrayList<Long> pelnaTrasa = new ArrayList<>();
        for (int i = 0; i <= indexStart; i++) {
            pelnaTrasa.add(listaWezlowOdStartu.get(i));
        }
        for (int i = indexKoniec - 1; i >= 0; i--) {
            pelnaTrasa.add(listaWezlowOdKonca.get(i));
        }

        return TRASA.stworz_z_wezlow_id(pelnaTrasa);
    }

    private String wykonajKrok(StanPoszukiwania trasa, double maxOddalenieOdCelu, double maxDystansBezPostepu, double znacznyPostepMetry) {
        if (STOP_ALGORYTMU) {
            return "stop";
        }
        PunktTrasy ostatniPktTrasy = trasa.przebyta_trasa.getLast();
        Long aktualny_wezel = ostatniPktTrasy.wezel_id;

        // jesli ostatni wezel trasy jest za daleko od celu-> restrat
        double odlegloscAktualnegoDoCelu = zmierz_odl_2pkt(aktualny_wezel, trasa.wezel_docelowy);
        if (odlegloscAktualnegoDoCelu > maxOddalenieOdCelu && odlegloscAktualnegoDoCelu > this.MINIMALNA_WARTOSC_MAX_ODDALENIA) {
            //System.out.println("RESTART: " + trasa.typ + " punkt za daleko od celu.");
            return "restart_za_daleko";
        }

        // jesli dystans bez postepu za duzy -> restart
        if (ostatniPktTrasy.dystansBezPostepu > maxDystansBezPostepu) {
            //System.out.println("RESTART: " + trasa.typ + " zbyt dlugi brak postepu.");
            return "restart_brak_postepu";
        }

        ArrayList<Long> kolejne_mozliwe_wezly = pobierzMozliweWezly(trasa);

        //jesli nie ma kolejnych mozliwych = backtracking
        if (kolejne_mozliwe_wezly.isEmpty()) {
            if (wykonajBacktracking(trasa) == true) {
                if (trasa.przebyta_trasa.getLast().dystansBezPostepu > maxDystansBezPostepu) {
                    //System.out.println("RESTART: " + trasa.typ + " backtracking przekroczyl maxDystansBezPostepu");
                    return "restart_backtracking_brak_postepu"; // jesli backtracking przekroczyl maxDystansBezPostepu
                } else {
                    return "backtracking"; // jesli backtracking ok
                }
            } else {
                // nie da sie cofnac bo trasa za krotka
                return "restart_slepa_uliczka";
            }
        }

        Long wybrany_wezel = kolejne_mozliwe_wezly.get(random.nextInt(kolejne_mozliwe_wezly.size()));
        // dodanie wybranego do testowanych z tego wezla
        trasa.przebyta_trasa.getLast().testowaniSasiedzi.add(wybrany_wezel);

        Long wybrana_droga_id = -1L;
        for (Polaczenie pol : DANE.wezly.get(aktualny_wezel).polaczenia) {
            Long dr_id = pol.IDdrogi;
            if (DANE.drogi.get(dr_id).getPrzeciwnyWezelId(aktualny_wezel).equals(wybrany_wezel)) {
                wybrana_droga_id = dr_id;
                break;
            }
        }
        if (wybrana_droga_id == -1L) {
            //System.out.println("Nie znaleziono drogi miedzy aktualnym wezlem a wybranym wezlem");
            return "stop";
        }

        double dlugoscWybranejDrogi = DANE.drogi.get(wybrana_droga_id).dlugosc;
        double nowaDlugoscTrasy = ostatniPktTrasy.dlugoscTrasy + dlugoscWybranejDrogi;
        double nowaNajlepszaOdlegloscDoCelu = ostatniPktTrasy.najlepszaOdlegloscDoCelu;
        double nowyDystansBezPostepu = ostatniPktTrasy.dystansBezPostepu;
        // sprawdzenie czy zresetowac odleglosc bez postepu czy zwiekszyc
        double odlegloscWybranegoDoCelu = zmierz_odl_2pkt(wybrany_wezel, trasa.wezel_docelowy);
        if (odlegloscWybranegoDoCelu < ostatniPktTrasy.najlepszaOdlegloscDoCelu - znacznyPostepMetry) {
            nowaNajlepszaOdlegloscDoCelu = odlegloscWybranegoDoCelu;
            nowyDystansBezPostepu = 0.0;
            //System.out.println("POSTEP: " + trasa.typ + "Nowa najlepsza odleglosc: " + nowaNajlepszaOdlegloscDoCelu);
        } else {
            nowyDystansBezPostepu += dlugoscWybranejDrogi;
        }

        trasa.przebyta_trasa.add(new PunktTrasy(wybrany_wezel, nowaDlugoscTrasy, nowaNajlepszaOdlegloscDoCelu, nowyDystansBezPostepu, dlugoscWybranejDrogi));
        trasa.odwiedzone_wezly.add(wybrany_wezel);

        return "dodano wezel";
    }

    private ArrayList<Long> pobierzMozliweWezly(StanPoszukiwania trasa) {
        ArrayList<Long> mozliwe_wezly = new ArrayList<>();
        Long aktualny_wezel_id = trasa.przebyta_trasa.getLast().wezel_id;

        // jesli brak dalszych wezlow
        if (DANE.wezly.get(aktualny_wezel_id).polaczenia.isEmpty()) {
            return mozliwe_wezly;
        }

        for (Polaczenie pol : DANE.wezly.get(aktualny_wezel_id).polaczenia) {

            // warunki
            if (trasa.typ.equals("OD STARTU") && pol.przejazd == false) {
                continue; // jesli od startu, to wyklucza braki przejazdu
            }

            if (trasa.typ.equals("OD KONCA")) {
                // trasa idaca od konca moze isc dwukierunkowymi i pod prad
                // czyli trasa idaca od konca nie moze isc jednokierunkowymi z prądem
                // czyli wywalic drogi, ktore sa jednokierunkowe, ale z przejazdem
                if (pol.przejazd == true && DANE.drogi.get(pol.IDdrogi).jednokierunkowa.equals("true")) {
                    continue;
                }
            }

            Long droga_id = pol.IDdrogi;
            Long sasiadujacy_wezel_id = pol.kolejnyWezel;

            // pomieniecie jesli wezel juz byl na trasie 
            if (trasa.odwiedzone_wezly.contains(sasiadujacy_wezel_id)) {
                continue;
            }

            //pominiecie wezlow jesli droga "ten wezel" -> "sasiadujacy wezel" juz wczesniej byly losowane
            if (trasa.przebyta_trasa.getLast().testowaniSasiedzi.contains(sasiadujacy_wezel_id)) {
                continue;
            }

            mozliwe_wezly.add(sasiadujacy_wezel_id);

        }
        return mozliwe_wezly;
    }

    private Boolean wykonajBacktracking(StanPoszukiwania trasa) {
        if (trasa.przebyta_trasa.size() <= 1) {
            return false;
        }

        PunktTrasy usuwany = trasa.przebyta_trasa.getLast();
        trasa.przebyta_trasa.remove(usuwany);

        // dodawanie odleglosc backtrackingu do odleglosci z brakiem postepu
        trasa.przebyta_trasa.getLast().dystansBezPostepu += usuwany.dlugoscOdPoprzedniegoWezla;
        return true;
    }

    private StanPoszukiwania restartujTrase(StanPoszukiwania trasa, ArrayList<ArrayList<Long>> historia) {
        ArrayList<Long> trasa_jako_wezly = trasa.pobierzWezly();
        zapiszTrase(historia, trasa_jako_wezly);
        //System.out.println("Zapisano trase" + trasa.typ + " do historii");

        return new StanPoszukiwania(trasa.typ, trasa.wezel_poczatkowy, trasa.wezel_docelowy);
    }

    private void zapiszTrase(ArrayList<ArrayList<Long>> historia, ArrayList<Long> trasa) {
        if (trasa == null || trasa.size() < 2) {
            return;
        }

        historia.add(new ArrayList<>(trasa));

        while (historia.size() > MAX_HISTORIA_TRAS) {
            historia.remove(0);
        }

    }

    private static class StanPoszukiwania {
        //obluga szukania trasy od poczatku/konca

        String typ; // czy leci od startu czy konca
        Long wezel_poczatkowy;
        Long wezel_docelowy;

        Set<Long> odwiedzone_wezly = new HashSet<>();
        ArrayList<PunktTrasy> przebyta_trasa = new ArrayList<>();

        public StanPoszukiwania(String typ, Long wezel_start_id, Long wezel_koniec_id) {
            this.typ = typ;
            this.wezel_poczatkowy = wezel_start_id;
            this.wezel_docelowy = wezel_koniec_id;

            double odlegloscStartCel = zmierz_odl_2pkt(wezel_start_id, wezel_koniec_id);

            // dodanie poczatkowego wezla
            przebyta_trasa.add(new PunktTrasy(wezel_poczatkowy, 0.0, odlegloscStartCel, 0.0, 0.0));
            odwiedzone_wezly.add(wezel_poczatkowy);
        }

        ArrayList<Long> pobierzWezly() {
            // zwraca trase jako liste id wezlow
            ArrayList<Long> wezly = new ArrayList<>();

            for (PunktTrasy pkttr : przebyta_trasa) {
                wezly.add(pkttr.wezel_id);
            }
            return wezly;
        }

    }

    public static class PunktTrasy {

        // zawiera informacje o punkcie trasy
        Long wezel_id;

        double dlugoscTrasy;
        double najlepszaOdlegloscDoCelu;
        double dystansBezPostepu;
        double dlugoscOdPoprzedniegoWezla;

        // bedac w tym wezle, wezly "testowanisasiedzi" byly slepymi sciezkami
        Set<Long> testowaniSasiedzi = new HashSet<>();

        public PunktTrasy(Long wezel_id, double dlugoscTrasy, double najlepszaOdlegloscDoCelu, double dystansBezPostepu, double dlugoscOdPoprzedniegoWezla) {
            this.wezel_id = wezel_id;
            this.dlugoscTrasy = dlugoscTrasy;
            this.najlepszaOdlegloscDoCelu = najlepszaOdlegloscDoCelu;
            this.dystansBezPostepu = dystansBezPostepu;
            this.dlugoscOdPoprzedniegoWezla = dlugoscOdPoprzedniegoWezla;
        }

    }
}