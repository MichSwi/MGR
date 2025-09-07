/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Michal
 */
public class infoTXT {

    private String nazwaPliku;
    private String data;
    private String godzina;
    private double _4_N_WYS,
            _3_E_SZER_R,
            _1_W_LAT,
            _2_S_LON;
    private int typPobrania; //1 - granice  2 - obszar  3 - kolo
    private double pole;

    public infoTXT() {
        nazwaPliku = "";
        data = "";
        godzina = "";
        _4_N_WYS = 0;
        _3_E_SZER_R = 0;
        _1_W_LAT = 0;
        _2_S_LON = 0;
        typPobrania = 0;
        pole = 0;
    }

    public infoTXT(String nazwaPliku, String data, String godzina, double _4_N_WYS, double _3_E_SZER_R, double _1_W_LAT, double _2_S_LON, int typPobrania) {
        this.nazwaPliku = nazwaPliku;
        this.data = data;
        this.godzina = godzina;
        this._4_N_WYS = _4_N_WYS;
        this._3_E_SZER_R = _3_E_SZER_R;
        this._1_W_LAT = _1_W_LAT;
        this._2_S_LON = _2_S_LON;
        this.typPobrania = typPobrania;
        
        pole = obliczPole();
    }

    public boolean wczytajPlik(String nazwaPliku) {
        return false;
    }

    public void zapiszPlik() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("POBRANE_PLIKI\\" + nazwaPliku))) {
            pw.println(nazwaPliku);
            String godzina = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            pw.println(data);
            pw.println(godzina);
            pw.println(_1_W_LAT);
            pw.println(_2_S_LON);
            pw.println(_3_E_SZER_R);
            pw.println(_4_N_WYS);
            pw.println(typPobrania);
            pw.println(obliczPole());
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double obliczPole(){
        if (typPobrania==1){ //granice
            return abs((_4_N_WYS-_2_S_LON)*(_3_E_SZER_R-_1_W_LAT));
        }
        else if (typPobrania==2){ //obszar
            return abs(_3_E_SZER_R*_4_N_WYS);
            
        }
        else if (typPobrania==3){
            return abs(Math.PI*_3_E_SZER_R*_3_E_SZER_R);
        }
        return -1;
    }
    
}
