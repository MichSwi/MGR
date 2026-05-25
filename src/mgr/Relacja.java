/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

/**
 *
 * @author Micha
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Relacja {

    public long ID;
    public Map<String, String> tags = new HashMap<>();

    // Członkowie relacji jako gotowe obiekty Droga
    public ArrayList<Droga> drogi = new ArrayList<>();

    public Relacja(long ID) {
        this.ID = ID;
    }
}
