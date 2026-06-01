/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr.ALGORYTMY;

import java.util.ArrayList;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;
import mgr.Wezel.Polaczenie;

/**
 *
 * @author Micha
 */
public class TRASA {

    ArrayList<Droga> trasa_drogi;
    ArrayList<Wezel> trasa_wezly;
    ArrayList<Long> trasa_drogi_id;
    ArrayList<Long> trasa_wezly_id;
    Double dlugosc;
    int czas_przejazdu;

    public TRASA() {
        this.trasa_drogi = new ArrayList();
        this.trasa_wezly = new ArrayList();
        this.trasa_drogi_id = new ArrayList();
        this.trasa_wezly_id = new ArrayList();
        this.dlugosc = 0.0;
        this.czas_przejazdu=0;
    }

    public static TRASA stworz_z_wezlow_id(ArrayList<Long> trasa_wezly_id) {
        TRASA t = new TRASA();
        t.trasa_wezly_id = trasa_wezly_id;
        for (Long wez_id : trasa_wezly_id) {
            t.trasa_wezly.add(DANE.wezly.get(wez_id));
        }

        for (int i = 0; i < trasa_wezly_id.size() - 1; i++) {
            Long wez_id_1 = trasa_wezly_id.get(i);
            Long wez_id_2 = trasa_wezly_id.get(i + 1);

            Long droga_id = 0L;
            for (Polaczenie pol : DANE.wezly.get(wez_id_1).polaczenia) {
                Long id_dr = pol.IDdrogi;

                if (DANE.drogi.get(id_dr).getPrzeciwnyWezelId(wez_id_1).equals(wez_id_2)) {
                    droga_id = id_dr;
                    break;
                }
            }
            t.trasa_drogi.add(DANE.drogi.get(droga_id));
            t.trasa_drogi_id.add(droga_id);
        }
        for(Droga dr : t.trasa_drogi){
            t.dlugosc+=dr.dlugosc;
            t.czas_przejazdu+=dr.czas_przejazdu;
        }
        return t;
    }

    public void dodaj_wezel_id(Long w) {
        trasa_wezly_id.add(w);
        trasa_wezly.add(DANE.wezly.get(w));

        if (!this.trasa_drogi_id.isEmpty()) {
            Long ostatni_wezel = trasa_wezly_id.getLast();
            for (Polaczenie pol : DANE.wezly.get(ostatni_wezel).polaczenia) {
                if (pol.kolejnyWezel.equals(w)) {
                    this.trasa_drogi_id.add(pol.IDdrogi);
                    this.trasa_drogi.add(DANE.drogi.get(pol.IDdrogi));
                    this.dlugosc += DANE.drogi.get(pol.IDdrogi).dlugosc;
                    return;
                }
            }
        }
    }

    public void dodaj_wezel(Wezel w) {
        dodaj_wezel_id(w.ID);
    }

}
