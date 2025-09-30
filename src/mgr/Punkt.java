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
        
        public void ustawXY(){

            double szer_mapy = 100000;
            double wys_mapy = 100000;
            
            // X
            this.X = mapuj(LON, DANE._1_W_LAT, DANE._3_E_SZER_R, 0, szer_mapy);
            
            // Y
            this.Y = mapuj(LAT, DANE._4_N_WYS, DANE._2_S_LON, 0, wys_mapy);
        }
    
                private double mapuj(double x, double in_min, double in_max, double out_min, double out_max) {
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}
}
