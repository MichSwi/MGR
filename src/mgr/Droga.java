/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Micha
 */
public class Droga {

    public long ID;
    public long IDosm;
    public String nazwa;
    public double dlugosc;
    public List<Punkt> punkty;
    public LinkedList<Long> polaczenia_poczatek_ID = new LinkedList<>();
    public LinkedList<Long> polaczenia_koniec_ID = new LinkedList<>();
    public String jednokierunkowa = "";
    public Map<String, String> tags;
    public Punkt pkt_start, pkt_koniec;
    public TrafficSegment ruchUliczny;
    public double maxspeed = -1;
    public double czas_przejazdu = Double.MIN_VALUE;
    public String temp_source;

    public Droga(long ID, String nazwa, double dlugosc, LinkedList<Punkt> punkty, LinkedList<Long> polaczenia_poczatek_ID, LinkedList<Long> polaczenia_koniec_ID, String jednokierunkowa) {
        this.ID = ID;
        this.nazwa = nazwa;
        this.dlugosc = dlugosc;
        this.punkty = punkty;
        this.polaczenia_poczatek_ID = polaczenia_poczatek_ID;
        this.polaczenia_koniec_ID = polaczenia_koniec_ID;
        this.jednokierunkowa = jednokierunkowa;
        tags = new HashMap<>();
        IDosm = -1;
    }

    public Droga(long ID) {
        this.ID = ID;
        tags = new HashMap<>();
        punkty = new ArrayList<>();
    }

    public Droga() {
    }

    public void ustawOdleglosc() {
        // WZOREM HAVERSINE
        this.dlugosc = 0.0;

        final double R = 6371000.0; // promień ziemi w metrach

        for (int i = 0; i < punkty.size() - 1; i++) {
            Punkt p1 = punkty.get(i);
            Punkt p2 = punkty.get(i + 1);

            double lat1 = Math.toRadians(p1.LAT);
            double lat2 = Math.toRadians(p2.LAT);

            double deltaLat = Math.toRadians(p2.LAT - p1.LAT);
            double deltaLon = Math.toRadians(p2.LON - p1.LON);

            double a = Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.sin(deltaLon / 2.0) * Math.sin(deltaLon / 2.0);

            double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

            double odleglosc = R * c;
            //System.out.println(odleglosc);
            this.dlugosc += odleglosc;
        }
//        this.dlugosc = 0.0;
//
//        for (int i = 0; i < punkty.size() - 1; i++) {
//            Punkt p1 = punkty.get(i);
//            Punkt p2 = punkty.get(i + 1);
//
//            double sredniaLat = Math.toRadians((p1.LAT + p2.LAT) / 2.0);
//
//            double dx = (p2.LON - p1.LON) * 111320.0 * Math.cos(sredniaLat);
//            double dy = (p2.LAT - p1.LAT) * 111320.0;
//
//            this.dlugosc += Math.sqrt(dx * dx + dy * dy);
//        }
    }

    public Long getPrzeciwnyWezelId(Long wez) {

        if (this.pkt_start.ID == wez) {
            return this.pkt_koniec.ID;
        } else if (this.pkt_koniec.ID == wez) {
            return this.pkt_start.ID;
        } else {
            throw new IllegalArgumentException("brak przeciwnego wezla w polaczeniu");
        }
    }

    public void obliczCzasPrzejazdu() {
        double czas_przejscie = 10.5;
        double czas_sygnalizacja = 30.5;

        double czas_kary = 0; //[s]

        // przejscia dla pieszych
        for (Punkt pkt : this.punkty) {
            if (pkt.tags.getOrDefault("highway", "-").equalsIgnoreCase("crossing")) {
                if (pkt.equals(this.pkt_koniec) || pkt.equals(this.pkt_start)) {
                    czas_kary += czas_przejscie / 2;
                } else {
                    czas_kary += czas_przejscie;
                }
            }
        }

        // sygnalizacja
        for (Punkt pkt : this.punkty) {
            if (pkt.tags.getOrDefault("highway", "-").equalsIgnoreCase("traffic_signals")) {
                if (pkt.equals(this.pkt_koniec) || pkt.equals(this.pkt_start)) {
                    czas_kary += czas_sygnalizacja / 2;
                } else {
                    czas_kary += czas_sygnalizacja;
                }
            }
        }
        // odleglosc
        Double czas_jazdy = 0.0;
        czas_jazdy = this.dlugosc / this.maxspeed * 3.6; //   m/1000 / km/h * 3600 = [s]

        this.czas_przejazdu = czas_kary + czas_jazdy;
    }

    public String ustawMaxSpeed() {

        if (this.ruchUliczny != null) {
            this.maxspeed = this.ruchUliczny.speed * 3.6; // z m/s na km/h
            return "HERE";
        }

        String maxspeedString = tags.getOrDefault("maxspeed", "-");
        if (maxspeedString.equalsIgnoreCase("walk")) {
            maxspeed = 7 * DANE.WSPOLCZYNNIK_PREDKOSCI;
            return "MaxSpeed";
        } else if (!maxspeedString.equals("-")) {
            maxspeed = Integer.parseInt(maxspeedString) * DANE.WSPOLCZYNNIK_PREDKOSCI;
            return "MaxSpeed";
        }

        String typ_drogi = tags.getOrDefault("highway", "brak tagu");
        switch (typ_drogi) {
            case "brak tagu":
                // brak informacji o typie drogi
                maxspeed = 50;
                return "brak";

            case "motorway":
                // autostrada
                maxspeed = 120;
                break;

            case "motorway_link":
                // łącznica autostrady
                maxspeed = 60;
                break;

            case "trunk":
                // droga ekspresowa lub główna
                maxspeed = 100;
                break;

            case "trunk_link":
                // łącznica drogi głównej
                maxspeed = 50;
                break;

            case "primary":
                // droga pierwszorzędna
                maxspeed = 50;
                break;

            case "primary_link":
                // łącznica drogi pierwszorzędnej
                maxspeed = 40;
                break;

            case "secondary":
                // droga drugorzędna
                maxspeed = 50;
                break;

            case "secondary_link":
                // łącznica drogi drugorzędnej
                maxspeed = 40;
                break;

            case "tertiary":
                // droga trzeciorzędna
                maxspeed = 40;
                break;

            case "tertiary_link":
                // łącznica drogi trzeciorzędnej
                maxspeed = 30;
                break;

            case "unclassified":
                // droga lokalna
                maxspeed = 30;
                return "unclassified";

            case "residential":
                // droga osiedlowa
                maxspeed = 30;
                break;

            case "living_street":
                // strefa zamieszkania
                maxspeed = 10;
                break;

            case "service":
                // droga serwisowa
                maxspeed = 20;
                break;

            case "rest_area":
                // miejsce obsługi podróżnych lub parking przy trasie
                maxspeed = 10;
                break;

            case "walk":
                // prędkość pieszych
                maxspeed = 7;
                break;

            default:
                // nieznany typ drogi
                maxspeed = 50;
                return "brak";
        }
        return "klasa";
    }
}
