/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr.ALGORYTMY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mgr.DANE;
import mgr.Droga;
import mgr.Wezel;
import mgr.Wezel.Polaczenie;

/**
 *
 * @author Michal
 */
public class AntColonyAlg {

    public Double alpha = 1.0;
    public Double beta = 1.0;

    private Map<Long, Double> feromony = new HashMap<>();
    private int ilosc_mrowek = 5;

    private Long pkt_start = DANE.wezelStartowyAlgorytmu.ID;
    private Long pkt_koniec = DANE.wezelKoncowyAlgorytmu.ID;
    private Map<Long, Wezel> wezly = DANE.wezly;
    ArrayList<Long> najlepsza_trasa = new ArrayList<>();

    public AntColonyAlg() {

        for (Long dr_id : DANE.drogi.keySet()) {
            feromony.put(dr_id, 1.0);
        }

    }

    public List<Droga> startAlg() {
        List<Droga> trasa_drog = new ArrayList<>();
        List<Long> trasa_wezlow_id = new ArrayList<>();
        Long aktualnyWezel = pkt_start;

        trasa_wezlow_id.add(aktualnyWezel);

        while (!aktualnyWezel.equals(pkt_koniec)) {
            Long kolejnyWezel = wybierzKolejnyWezel(aktualnyWezel, trasa_wezlow_id);

            if (kolejnyWezel == null) {
                System.out.println("Brak dalszej drogi");
                trasa_drog.clear();
                return trasa_drog;
            }

            trasa_wezlow_id.add(kolejnyWezel);
            
            for(Polaczenie pol : DANE.wezly.get(aktualnyWezel).polaczenia){
                if(pol.kolejnyWezel.equals(kolejnyWezel)){
                    trasa_drog.add(DANE.drogi.get(pol.IDdrogi));
                }
            }
            
            aktualnyWezel = kolejnyWezel;
        }

        System.out.println("Trasa: " + trasa_wezlow_id);
        return trasa_drog;

       
    }

    private Long wybierzKolejnyWezel(Long aktualny_wezel, List<Long> odwiedzone) {
        List<Long> drogi = new ArrayList<>();
        for(Polaczenie pol : DANE.wezly.get(aktualny_wezel).polaczenia){
            if(pol.przejazd==true){
                drogi.add(pol.IDdrogi);
            }
        }
        Map<Long, Double> ocenyWezlow = new HashMap<>();
        double sumaOcen = 0.0;

        for (Long drogaId : drogi) {
            Long nowyWezel = DANE.drogi.get(drogaId).getPrzeciwnyWezelId(aktualny_wezel);

            if (odwiedzone.contains(nowyWezel)) {
                continue;
            }

            double feromon = feromony.get(drogaId);
            double dystansDoCelu = odlegloscWLiniProstej(nowyWezel, pkt_koniec);

            double heurystyka = 1.0 / (dystansDoCelu + 0.0001);
            double ocena = Math.pow(feromon, alpha) * Math.pow(heurystyka, beta);

            ocenyWezlow.put(nowyWezel, ocena);
            sumaOcen += ocena;
        }

        if (ocenyWezlow.isEmpty()) {
            return null;
        }

        double los = Math.random() * sumaOcen;
        double akumulator = 0.0;

        for (Long wezelId : ocenyWezlow.keySet()) {
            akumulator += ocenyWezlow.get(wezelId);
            if (los <= akumulator) {
                return wezelId;
            }
        }

        return ocenyWezlow.keySet().iterator().next();
    }

    private double odlegloscWLiniProstej(Long id1, Long id2) {
        Wezel w1 = DANE.wezly.get(id1);
        Wezel w2 = DANE.wezly.get(id2);

        double dx = w1.X - w2.X;
        double dy = w1.Y - w2.Y;

        return Math.sqrt(dx * dx + dy * dy);
    }
}
