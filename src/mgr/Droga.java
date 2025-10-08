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
    private double dlugosc;
    public List<Punkt> punkty;
    public LinkedList<Long> polaczenia_ID = new LinkedList<>();
    public boolean jednokierunkowa=false;
    private Punkt start;
    private Punkt koniec;
    Map<String, String> tags;
    public Punkt pkt_start, pkt_koniec;
    public TrafficSegment ruchUliczny;

    public Droga(long ID, String nazwa, double dlugosc, LinkedList<Punkt> punkty, LinkedList<Long> polaczenia_ID, boolean jednokierunkowa, Punkt start, Punkt koniec) {
        this.ID = ID;
        this.nazwa = nazwa;
        this.dlugosc = dlugosc;
        this.punkty = punkty;
        this.polaczenia_ID = polaczenia_ID;
        this.jednokierunkowa = jednokierunkowa;
        this.start = start;
        this.koniec = koniec;
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
