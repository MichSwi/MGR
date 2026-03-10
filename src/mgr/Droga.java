/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import static java.lang.Math.sqrt;
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
    public String nazwa;
    public double dlugosc;
    public List<Punkt> punkty;
    public LinkedList<Long> polaczenia_poczatek_ID = new LinkedList<>();
    public LinkedList<Long> polaczenia_koniec_ID = new LinkedList<>();
    public boolean jednokierunkowa=false;
    Map<String, String> tags;
    public Punkt pkt_start, pkt_koniec;
    public TrafficSegment ruchUliczny;
    public int maxspeed=-1;

    public Droga(long ID, String nazwa, double dlugosc, LinkedList<Punkt> punkty, LinkedList<Long> polaczenia_poczatek_ID, LinkedList<Long> polaczenia_koniec_ID, boolean jednokierunkowa, Punkt start, Punkt koniec) {
        this.ID = ID;
        this.nazwa = nazwa;
        this.dlugosc = dlugosc;
        this.punkty = punkty;
        this.polaczenia_poczatek_ID = polaczenia_poczatek_ID;
        this.polaczenia_koniec_ID = polaczenia_koniec_ID;
        this.jednokierunkowa = jednokierunkowa;
        tags = new HashMap<>();

    }

    public Droga(long ID) {
        this.ID = ID;
        tags = new HashMap<>();
        punkty = new ArrayList<>();
    }

    public Droga() {
    }
    
    public void ustawOdleglosc(){
        for (int i=0;i<=punkty.size()-2; i++){
            double a = punkty.get(i).LAT-punkty.get(i+1).LAT;
            double b = punkty.get(i).LON-punkty.get(i+1).LON;
            this.dlugosc = sqrt(a*a+b*b);
        }
    }
}
