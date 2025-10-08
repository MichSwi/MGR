/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michal
 */
public class DANE {

    public static double _2_S_LON, _1_W_LAT, _4_N_WYS, _3_E_SZER_R;
    public static List<Boolean> coZaznaczone = new ArrayList<>();
    public static String nazwaPliku;
    public static List<Droga> drogi = new ArrayList<>();
    public static List<TrafficSegment> ruchUliczny;

    public static void ustawPolaczenia() {
        // biore droge
        System.out.println("WSZYSTKIE: " + drogi.size());
        for (int i = 0; i <= drogi.size(); i++) {
            // iteruje po reszcie drog
            for (int pozostale = i + 1; pozostale <= drogi.size() - 1; pozostale++) {
                System.out.println("droga bazowa:" + i + " sprawdzam: " + pozostale);
                if (drogi.get(i).pkt_start.ID == drogi.get(pozostale).pkt_start.ID || drogi.get(i).pkt_start.ID == drogi.get(pozostale).pkt_koniec.ID) {
                    drogi.get(i).polaczenia_ID.add(drogi.get(pozostale).ID);
                    drogi.get(pozostale).polaczenia_ID.add(drogi.get(i).ID);
                    System.out.println(" DODAŁEM ");
                } else if (drogi.get(i).pkt_koniec.ID == drogi.get(pozostale).pkt_start.ID || drogi.get(i).pkt_koniec.ID == drogi.get(pozostale).pkt_koniec.ID) {
                    drogi.get(i).polaczenia_ID.add(drogi.get(pozostale).ID);
                    drogi.get(pozostale).polaczenia_ID.add(drogi.get(i).ID);
                    System.out.println(" DODAŁEM ");
                }
            }
        }
    }

    public static void ustawOdleglosci() {
        for (Droga droga : drogi) {
            droga.ustawOdleglosc();
        }
    }

    public static void ustawStartKoniec() {
        for (Droga droga : drogi) {
            droga.pkt_start = droga.punkty.getFirst();
            droga.pkt_koniec = droga.punkty.getLast();
        }
    }

    public static void ustawRuchUliczny() {
        for (Droga dr : drogi) {
            for (TrafficSegment TF : ruchUliczny) {
                if (dr.nazwa.equals(TF.street)) {
                    dr.ruchUliczny = TF;
                }
            }
        }
    }

    public static void stopDebug() {
        System.out.println("stop debug");
    }

}
