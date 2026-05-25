/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Micha
 */
public class Punkt {

    public long ID;
    public double X;
    public double Y;
    public double LAT;
    public double LON;
    public Map<String, String> tags = new HashMap<>();
    public int ilosc_uzyc;

    Punkt(double LAT, double LON, long ID) {
        this.LAT = LAT;
        this.LON = LON;
        this.ID = ID;
        this.ilosc_uzyc = 0;
    }

    public Punkt(double lat, double lon) {
        this.LAT = lat;
        this.LON = lon;
    }

    public Punkt() {
    }

    public void ustawXY() {

        double latCenter = (DANE._2_S_minLAT + DANE._4_N_maxLAT) / 2.0;
        double lonCenter = (DANE._1_W_minLON + DANE._3_E_maxLON) / 2.0;
        // przeliczenie stopni na metry
        double metersPerDegreeLat = 111320.0;
        double metersPerDegreeLon = 111320.0 * Math.cos(Math.toRadians(latCenter));

        this.X = (this.LON - lonCenter) * metersPerDegreeLon;
        this.Y = -(this.LAT - latCenter) * metersPerDegreeLat;
    }

    public String getNazwa() {

        for (Long id : DANE.drogi.keySet()) {
            Droga dr = DANE.drogi.get(id);
            if (this.equals(dr.pkt_start) || this.equals(dr.pkt_koniec)) {
                return dr.nazwa;
            }
        }
        return "blad";
    }

    public Droga getDroga() {
        for (Long id : DANE.drogi.keySet()) {
            Droga dr = DANE.drogi.get(id);
            if (dr.punkty.contains(this)) {
                return dr;
            }
        }
        return null;
    }

    public Long getDrogaID() {
        for (Long id : DANE.drogi.keySet()) {
            Droga dr = DANE.drogi.get(id);
            if (dr.punkty.contains(this)) {
                return dr.ID;
            }
        }
        return null;
    }

}
