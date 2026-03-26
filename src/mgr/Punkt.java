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
    public TypPunkt typ;
    public Map<String, String> tags = new HashMap<>();
    public int ilosc_uzyc;

    Punkt(double LAT, double LON, TypPunkt typ, long ID) {
        this.LAT = LAT;
        this.LON = LON;
        this.typ = typ;
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
        double szer_mapy = 1023;
        double wys_mapy = 886;

//        System.out.println("PUNKT ID=" + ID);
//        System.out.println("LON=" + LON + "  W=" + DANE._1_W_LAT + "  E=" + DANE._3_E_SZER_R);
//        System.out.println("LAT=" + LAT + "  S=" + DANE._2_S_LON + "  N=" + DANE._4_N_WYS);
        this.X = mapuj(LON, DANE._1_W_LAT, DANE._3_E_SZER_R, -1000, szer_mapy);
        this.Y = mapuj(LAT, DANE._4_N_WYS, DANE._2_S_LON, -1000, wys_mapy);

//        System.out.println("X=" + X + " Y=" + Y);
    }

    private double mapuj(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
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
