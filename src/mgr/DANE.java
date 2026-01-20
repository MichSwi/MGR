/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michal
 */
public class DANE {

    public static double _2_S_LON, _1_W_LAT, _4_N_WYS, _3_E_SZER_R;
    public static List<Boolean> coZaznaczone = new ArrayList<>();
    public static String nazwaPliku;
    public static Map<Long, Droga> drogi = new HashMap<>();
    public static List<TrafficSegment> ruchUliczny = new ArrayList<>();
    public static Punkt punkStartowyAlgorytmu = new Punkt();
    public static Punkt punkKoncowyAlgorytmu = new Punkt();

    public static void ustawPolaczenia() {
//
//        System.out.println("WSZYSTKIE: " + drogi.size());
//        // biore droge
//        for (int i = 0; i <= drogi.size(); i++) {
//            // iteruje po reszcie drog
//            for (int pozostale = i + 1; pozostale <= drogi.size() - 1; pozostale++) {
//                //jesli ID punktu startowego bazowej drogi == ID punktu startowego lub kocowego jakiejs kolejnej drogi -> polaczenie
//                if (drogi.get(i).pkt_start.ID == drogi.get(pozostale).pkt_start.ID || drogi.get(i).pkt_start.ID == drogi.get(pozostale).pkt_koniec.ID) {
//                    drogi.get(i).polaczenia_ID.add(drogi.get(pozostale).ID);
//                    drogi.get(pozostale).polaczenia_ID.add(drogi.get(i).ID);
////                    System.out.println(" DODAŁEM droga bazowa:" + i + " sprawdzam: " + pozostale);
//
//                    //jesli ID punktu KONCOWEGO bazowej drogi == ID punktu startowego lub kocowego jakiejs kolejnej drogi -> polaczenie
//                } else if (drogi.get(i).pkt_koniec.ID == drogi.get(pozostale).pkt_start.ID || drogi.get(i).pkt_koniec.ID == drogi.get(pozostale).pkt_koniec.ID) {
//                    drogi.get(i).polaczenia_ID.add(drogi.get(pozostale).ID);
//                    drogi.get(pozostale).polaczenia_ID.add(drogi.get(i).ID);
////                    System.out.println(" DODAŁEM droga bazowa:" + i + " sprawdzam: " + pozostale);
//                }
//            }
//        }
        System.out.println("WSZYSTKIE: " + drogi.size());
        List<Long> ListaKluczy = new ArrayList<>(drogi.keySet());
        // iteruje po kluczach od poczatku
        for (int i = 0; i < ListaKluczy.size(); i++) {
            
            // iteruje po reszcie kluczy
            for (int pozostale=i+1; pozostale<=drogi.size() - 1; pozostale++){
                                //jesli ID punktu startowego bazowej drogi == ID punktu startowego lub kocowego jakiejs kolejnej drogi -> polaczenie
                if (drogi.get(ListaKluczy.get(i)).pkt_start.ID == drogi.get(ListaKluczy.get(pozostale)).pkt_start.ID || drogi.get(ListaKluczy.get(i)).pkt_start.ID == drogi.get(ListaKluczy.get(pozostale)).pkt_koniec.ID) {
                    drogi.get(ListaKluczy.get(i)).polaczenia_ID.add(drogi.get(ListaKluczy.get(pozostale)).ID);
                    drogi.get(ListaKluczy.get(pozostale)).polaczenia_ID.add(drogi.get(ListaKluczy.get(i)).ID);
//                    System.out.println(" DODAŁEM droga bazowa:" + i + " sprawdzam: " + pozostale);

                    //jesli ID punktu KONCOWEGO bazowej drogi == ID punktu startowego lub kocowego jakiejs kolejnej drogi -> polaczenie
                } else if (drogi.get(ListaKluczy.get(i)).pkt_koniec.ID == drogi.get(ListaKluczy.get(pozostale)).pkt_start.ID || drogi.get(ListaKluczy.get(i)).pkt_koniec.ID == drogi.get(ListaKluczy.get(pozostale)).pkt_koniec.ID) {
                    drogi.get(ListaKluczy.get(i)).polaczenia_ID.add(drogi.get(ListaKluczy.get(pozostale)).ID);
                    drogi.get(ListaKluczy.get(pozostale)).polaczenia_ID.add(drogi.get(ListaKluczy.get(i)).ID);
//                    System.out.println(" DODAŁEM droga bazowa:" + i + " sprawdzam: " + pozostale);
                }
            }
        } 
    }

    public static void ustawOdleglosci() {
//        for (Droga droga : drogi) {
//            droga.ustawOdleglosc();
//        }

        for (Long klucz : drogi.keySet()) {
            drogi.get(klucz).ustawOdleglosc();
        }
    }

    public static void ustawStartKoniec() {
//        for (Droga droga : drogi) {
//            droga.pkt_start = droga.punkty.getFirst();
//            droga.pkt_koniec = droga.punkty.getLast();
//        }

        for (Long klucz : drogi.keySet()) {
            Droga dr = drogi.get(klucz);
            dr.pkt_start = dr.punkty.getFirst();
            dr.pkt_koniec = dr.punkty.getLast();
        }
    }

    public static void ustawRuchUliczny() {
        int ilosc_dodanego_ruchu = 0;

//        for (TrafficSegment TF : ruchUliczny) {
//            for (Droga dr : drogi){
//                if (TF.street.equals(dr.nazwa)){
//                    dr.ruchUliczny = TF;
//                    ilosc_dodanego_ruchu++;
//                }
//            }
//        }        
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

}
