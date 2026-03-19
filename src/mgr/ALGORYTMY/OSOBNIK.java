/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.List;
import mgr.Droga;

/**
 *
 * @author Micha
 */
public class OSOBNIK {

    public List<Droga> trasa = new ArrayList<>();
    public Double ocena = 0.0;

    public OSOBNIK(List<Droga> trasa) {
        this.trasa = trasa;
        for (Droga dr : trasa) {
            this.ocena += dr.dlugosc;
        }
    }

    public OSOBNIK() {
    }

    public void ocenOsobnika() {
        for (Droga dr : this.trasa) {
            this.ocena += dr.dlugosc;
        }
    }
}
