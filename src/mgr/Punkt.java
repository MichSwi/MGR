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
    
    private long ID;
    private double X;
    private double Y;
    private double LAT;
    private double LON;
    private TypPunkt typ;
    Map<String, String> tags = new HashMap<>();
    
    
    Punkt(double LAT, double LON, TypPunkt typ, long ID){
        this.LAT = LAT;
        this.LON = LON;
        this.typ=typ;
        this.ID=ID;
    }
    
        public Punkt(double lat, double lon) {
        this.LAT = lat;
        this.LON = lon;
    }
    
}
