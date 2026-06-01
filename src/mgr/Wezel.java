/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Micha
 */
public class Wezel {

    public final long ID;
    public final double X, Y;
    public final List<Polaczenie> polaczenia = new ArrayList<>();

    public Wezel() {
        this.ID = 0L;
        this.X = 0L;
        this.Y = 0L;
    }

    public Wezel(long ID, double x, double y) {
        this.ID = ID;
        this.X = x;
        this.Y = y;
    }

    public final void dodajPolaczenie(long drogaId, Boolean przejazd) {
        
        Long kolejnyWezel = DANE.drogi.get(drogaId).getPrzeciwnyWezelId(this.ID);
        
        Polaczenie pol = new Polaczenie(drogaId, kolejnyWezel, przejazd);
        polaczenia.add(pol);
    }
    
    public class Polaczenie{
        public Long IDdrogi;
        public Long kolejnyWezel;
        public boolean przejazd;

        public Polaczenie(Long IDdrogi, Long kolejnyWezel, boolean przejazd) {
            this.IDdrogi = IDdrogi;
            this.kolejnyWezel = kolejnyWezel;
            this.przejazd = przejazd;
        }
    }
}
