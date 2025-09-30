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
public class IloscPobranHere {

    private String data;
    private String godzina;
    private int iloscPobran; 

    public IloscPobranHere() {
        data = "";
        godzina = "";
    }

    public int getIloscPobran() {
        return iloscPobran;
    }

    private int odczytajWartosc(String nazwaPliku) {
        return -1;
    }

    public void zapiszPlik() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("licznikPobran"))) {

            String godzina = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            pw.println(data);
            pw.println(godzina);
            pw.println(iloscPobran+1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
