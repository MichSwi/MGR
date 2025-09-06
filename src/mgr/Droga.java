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
    
    private long ID;
    private String nazwa;
    private double dlugosc;
    public List<Punkt> punkty;
    private LinkedList<String> polaczenia_ID;
    private boolean jednokierunkowa;
    private Punkt start;
    private Punkt koniec;
    Map<String, String> tags;

    public Droga(long ID, String nazwa, double dlugosc, LinkedList<Punkt> punkty, LinkedList<String> polaczenia_ID, boolean jednokierunkowa, Punkt start, Punkt koniec) {
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
    
    public Droga(long ID){
        this.ID=ID;
        tags = new HashMap<>();
        punkty = new ArrayList<>();
    }

    public Droga() {
    }
    
}
