/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michal
 */
public class DANE {

    public static double _2_S_minLAT, _1_W_minLON, _4_N_maxLAT, _3_E_maxLON;
    public static List<Boolean> coZaznaczone = new ArrayList<>();
    public static String nazwaPliku;
    public static Map<Long, Droga> drogi = new HashMap<>();
    public static List<TrafficSegment> ruchUliczny = new ArrayList<>();
    public static Wezel wezelStartowyAlgorytmu = new Wezel();
    public static Wezel wezelKoncowyAlgorytmu = new Wezel();
    public static Map<Long, Wezel> wezly = new HashMap<>();

    public static List<Droga> ALG_SCIEZKA = new ArrayList<>();

    public static Map<Long, Droga> rzeki = new HashMap<>();
    public static Map<Long, Droga> woda = new HashMap<>();
    public static Map<Long, Droga> kolej = new HashMap<>();
    public static Map<Long, Relacja> relacje = new HashMap<>();

    public static void ustawPolaczenia() {

//        System.out.println("WSZYSTKIE: " + drogi.size());
//        List<Long> ListaKluczy = new ArrayList<>(drogi.keySet());
//        Long pierwsza_pocz, pierwsza_kon, druga_pocz, druga_kon;
//
//        // iteruje po kluczach od poczatku
//        for (int i = 0; i < ListaKluczy.size(); i++) {
//            pierwsza_pocz = drogi.get(ListaKluczy.get(i)).pkt_start.ID;
//            pierwsza_kon = drogi.get(ListaKluczy.get(i)).pkt_koniec.ID;
//            for (int pozostale = i + 1; pozostale <= drogi.size() - 1; pozostale++) {
//                druga_pocz = drogi.get(ListaKluczy.get(pozostale)).pkt_start.ID;
//                druga_kon = drogi.get(ListaKluczy.get(pozostale)).pkt_koniec.ID;
//                if (pierwsza_pocz.equals(druga_pocz)) {
//                    drogi.get(ListaKluczy.get(i)).polaczenia_poczatek_ID.add(ListaKluczy.get(pozostale));
//                    drogi.get(ListaKluczy.get(pozostale)).polaczenia_poczatek_ID.add(ListaKluczy.get(i));
//                } else if (pierwsza_pocz.equals(druga_kon)) {
//                    drogi.get(ListaKluczy.get(i)).polaczenia_poczatek_ID.add(ListaKluczy.get(pozostale));
//                    drogi.get(ListaKluczy.get(pozostale)).polaczenia_koniec_ID.add(ListaKluczy.get(i));
//
//                } else if (pierwsza_kon.equals(druga_pocz)) {
//                    drogi.get(ListaKluczy.get(i)).polaczenia_koniec_ID.add(ListaKluczy.get(pozostale));
//                    drogi.get(ListaKluczy.get(pozostale)).polaczenia_poczatek_ID.add(ListaKluczy.get(i));
//
//                } else if (pierwsza_kon.equals(druga_kon)) {
//                    drogi.get(ListaKluczy.get(i)).polaczenia_koniec_ID.add(ListaKluczy.get(pozostale));
//                    drogi.get(ListaKluczy.get(pozostale)).polaczenia_koniec_ID.add(ListaKluczy.get(i));
//                }
//            }
//        }
    }

    public static void ustawOdleglosci() {
        for (Long klucz : drogi.keySet()) {
            drogi.get(klucz).ustawOdleglosc();
        }
    }

    public static void ustawStartKoniec() {
        for (Long klucz : drogi.keySet()) {
            Droga dr = drogi.get(klucz);
            dr.pkt_start = dr.punkty.getFirst();
            dr.pkt_koniec = dr.punkty.getLast();
        }
    }

    public static final void budujWezlyZDrog() {
        wezly.clear();

        for (Droga d : drogi.values()) {
            if (d == null || d.pkt_start == null || d.pkt_koniec == null) {
                continue;
            }

            Punkt s = d.pkt_start;
            Punkt k = d.pkt_koniec;

            s.ustawXY();
            k.ustawXY();

            // START
            Wezel ws = wezly.get(s.ID);
            if (ws == null) {
                ws = new Wezel(s.ID, s.X, s.Y);
                wezly.put(s.ID, ws);
            }

            // KONIEC
            Wezel wk = wezly.get(k.ID);
            if (wk == null) {
                wk = new Wezel(k.ID, k.X, k.Y);
                wezly.put(k.ID, wk);
            }

            if (d.jednokierunkowa.equals("true")) {
                ws.dodajPolaczenie(d.ID, true);
                wk.dodajPolaczenie(d.ID, false);  // false = nie ma przejazdu
            } else if (d.jednokierunkowa.equals("false")) {
                ws.dodajPolaczenie(d.ID, true);
                wk.dodajPolaczenie(d.ID, true);
            } else if (d.jednokierunkowa.equals("-1")) {
                // jednokierunkowa ale w druga strone (od pkt_koniec do pkt_pocz)
                wk.dodajPolaczenie(d.ID, true);
                ws.dodajPolaczenie(d.ID, false); // false = nie ma przejazdu
            } else if (d.jednokierunkowa.equals("alternating")) {
                // zmieniajaca sie
                ws.dodajPolaczenie(d.ID, true);
                wk.dodajPolaczenie(d.ID, true);
            }
        }
        System.out.print("Dodano wezly");
    }

    public static final void ustawRuchUliczny() {
        int ilosc_dodanego_ruchu = 0;

        for (TrafficSegment TF : ruchUliczny) {
            for (Long klucz : drogi.keySet()) {
                Droga dr = drogi.get(klucz);
                if (TF.street.equals(dr.nazwa)) {
                    dr.ruchUliczny = TF;
                    ilosc_dodanego_ruchu++;
                }
            }
        }

        System.out.println("dodano info u ruchu ulicznym:" + ilosc_dodanego_ruchu);
    }

    public static void stopDebug() {
        System.out.println("stop debug");
    }

    public static final void ustawToryWode() {
        kolej.clear();
        rzeki.clear();
        woda.clear();

        List<Long> doUsuniecia = new ArrayList<>();

        for (Map.Entry<Long, Droga> entry : drogi.entrySet()) {
            Long id = entry.getKey();
            Droga d = entry.getValue();

            if (d == null || d.tags == null) {
                continue;
            }

            String railway = d.tags.get("railway");
            String waterway = d.tags.get("waterway");
            String natural = d.tags.get("natural");
            String water = d.tags.get("water");

            // KOLEJ
            if (railway != null) {
                kolej.put(id, d);
                doUsuniecia.add(id);
                continue;
            }

            // WODY OBSZAROWE: jeziora, stawy, zbiorniki itd.
            if ("water".equals(natural) || water != null) {
                woda.put(id, d);
                doUsuniecia.add(id);
                continue;
            }

            // WODY LINIOWE: rzeki, strumienie, kanały itd.
            if (waterway != null) {
                rzeki.put(id, d);
                doUsuniecia.add(id);
            }
        }

        for (Relacja rel : DANE.relacje.values()) {
            for (Droga dr_w_rel : rel.drogi) {
                doUsuniecia.add(dr_w_rel.ID * 100);
                rzeki.put(dr_w_rel.ID * 100, dr_w_rel);
            }

        }

        for (Long id : doUsuniecia) {
            drogi.remove(id);
        }

        System.out.println("Przeniesiono kolej: " + kolej.size());
        System.out.println("Przeniesiono rzeki: " + rzeki.size());
        System.out.println("Przeniesiono zbiorniki wodne: " + woda.size());
        System.out.println("Pozostalo drog: " + drogi.size());
    }

    static public void ustawMaxSpeed() {
        int licznik_brak = 0;
        int licznik_tag = 0;
        int licznik_ruch_uliczny = 0;
        int licznik_klasa = 0;
        int licznik_unclassified = 0;

        for (Droga dr : drogi.values()) {
            String zrodlo = dr.ustawMaxSpeed();
            switch (zrodlo) {
                case "brak":
                    licznik_brak++;
                    break;
                case "tag":
                    licznik_tag++;
                    break;
                case "klasa":
                    licznik_klasa++;
                    break;
                case "ruch_uliczny":
                    licznik_ruch_uliczny++;
                    break;
                case "unclassified":
                    licznik_unclassified++;
                    break;
            }
        }
        System.out.println("========== DODANO MAXSPEED, ZLODLA: ==========");
        System.out.println("zrodlo_brak: " + licznik_brak);
        System.out.println("zrodlo_tag: " + licznik_tag);
        System.out.println("zrodlo_ruch_uliczny: " + licznik_ruch_uliczny);
        System.out.println("zrodlo_klasa: " + licznik_klasa);
        System.out.println("zrodlo_unclassified: " + licznik_unclassified);
        System.out.println("=========================================");
    }

    static void ustawCzasPrzejazdu() {
        for (Droga dr : DANE.drogi.values()) {
            dr.obliczCzasPrzejazdu();
        }
    }
}
