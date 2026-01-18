/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.io.BufferedReader;
import java.io.FileReader;
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

    public infoTXT(String nazwaPliku, double _4_N_WYS, double _3_E_SZER_R, double _1_W_LAT, double _2_S_LON, int typPobrania) {
        this.nazwaPliku = nazwaPliku;
        this._4_N_WYS = _4_N_WYS;
        this._3_E_SZER_R = _3_E_SZER_R;
        this._1_W_LAT = _1_W_LAT;
        this._2_S_LON = _2_S_LON;
        this.typPobrania = typPobrania;

        pole = obliczPole();
    }

    public boolean wczytajPlik(String nazwaPliku) {
        try (BufferedReader br = new BufferedReader(new FileReader("POBRANE_PLIKI\\" + nazwaPliku))) {
            data = br.readLine();    // pierwsza linia
            godzina = br.readLine();
            _1_W_LAT = Double.parseDouble(br.readLine());
            _2_S_LON = Double.parseDouble(br.readLine());
            _3_E_SZER_R = Double.parseDouble(br.readLine());
            _4_N_WYS = Double.parseDouble(br.readLine());
            typPobrania = Integer.parseInt(br.readLine());
            pole = Double.parseDouble(br.readLine());
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void zapiszPlik() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("POBRANE_PLIKI\\" + nazwaPliku + ".txt"))) {
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

    private double obliczPole() {
        if (typPobrania == 1) { //granice
            return abs((_4_N_WYS - _2_S_LON) * (_3_E_SZER_R - _1_W_LAT));
        } else if (typPobrania == 2) { //obszar
            return abs(_3_E_SZER_R * _4_N_WYS);
        } else if (typPobrania == 3) {
            return abs(Math.PI * _3_E_SZER_R * _3_E_SZER_R);
        }
        return -1;
    }

    public String getNazwaPliku() {
        return nazwaPliku;
    }

    public String getData() {
        return data;
    }

    public String getGodzina() {
        return godzina;
    }

    public double get4_N_WYS() {
        return _4_N_WYS;
    }

    public double get3_E_SZER_R() {
        return _3_E_SZER_R;
    }

    public double get1_W_LAT() {
        return _1_W_LAT;
    }

    public double get2_S_LON() {
        return _2_S_LON;
    }

    public int getTypPobrania() {
        return typPobrania;
    }

    public double getPole() {
        return pole;
    }
    
    
    
}
