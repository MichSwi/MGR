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
    private int X;
    private int Y;
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
        
        public void ustawXY(double _1_W_LAT, double _2_S_LON, double _3_E_SZER_R, double _4_N_WYS){
            int szer_mapy = 1000;
            int wys_mapy = 1000;
            
            this.X = 0;
        }
    
}
