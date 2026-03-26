/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.List;
import mgr.Droga;
import mgr.Wezel;

/**
 *
 * @author Micha
 */
public class OSOBNIK {

    public List<Droga> trasa_drogi = new ArrayList<>();
    public List<Wezel> trasa_wezly = new ArrayList<>();
    public Double ocena = 0.0;

    public OSOBNIK(List<Droga> trasa_drog, List<Wezel> trasa_wez) {
        this.trasa_drogi = trasa_drog;
        for (Droga dr : trasa_drog) {
            this.ocena += dr.dlugosc;
        }
        this.trasa_wezly=trasa_wez;
    }

    public OSOBNIK() {
    }

    public void ocenOsobnika() {
        for (Droga dr : this.trasa_drogi) {
            this.ocena += dr.dlugosc;
        }
    }
}
