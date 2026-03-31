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
    public final List<Long> drogiIDs = new ArrayList<>();

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

    public final void dodajDroge(long drogaId) {
        drogiIDs.add(drogaId);
    }
}
