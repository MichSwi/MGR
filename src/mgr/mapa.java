package mgr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import mgr.ALGORYTMY.WYNIKI;

public class mapa extends javax.swing.JPanel {

    private Boolean zmienna = false;
    public Wezel ZaznaczonyWezel;
    public int clickX;
    public int clickY;

    private int skalaProc = 100;
    private double widok_x = 30;
    private double widok_y = 30;
    private int prevMouseX, prevMouseY;
    private Map<Long, Droga> drogi = DANE.drogi;
    private Map<Long, Wezel> wezly = DANE.wezly;

    Color czerw_przezr = new Color(255, 0, 0, 44); // czerwony, 17% widoczności
    Color kolor_wody = new Color(139, 188, 255);

    public mapa() {
        initComponents();

        this.setBackground(Color.white);
        java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                skala_label.setText("X: " + (int) worldX(e.getX())
                        + " Y: " + (int) worldY(e.getY())
                        + " SKALA: " + skalaProc + "%");
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                prevMouseX = e.getX();
                prevMouseY = e.getY();
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                int dx = e.getX() - prevMouseX;
                int dy = e.getY() - prevMouseY;
                widok_x += dx;
                widok_y += dy;
                prevMouseX = e.getX();
                prevMouseY = e.getY();
                repaint();
            }

            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                int oldProc = skalaProc;
                int step = Math.max(1, (int) Math.round(skalaProc * 0.1));
                skalaProc = Math.max(1, Math.min(500, skalaProc - notches * step));

                if (skalaProc != oldProc) {
                    double sOld = oldProc / 100.0, sNew = scale();
                    double cx = e.getX(), cy = e.getY();
                    // utrzymaj punkt pod kursorem:
                    widok_x = cx - (sNew / sOld) * (cx - widok_x);
                    widok_y = cy - (sNew / sOld) * (cy - widok_y);

                    skala_label.setText("X: " + (int) worldX(e.getX())
                            + " Y: " + (int) worldY(e.getY())
                            + " SKALA: " + skalaProc + "%");
                    repaint();
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        //java.awt.Graphics2D g2d_static = (java.awt.Graphics2D) g;

        Graphics2D g2d_zawartosc = (Graphics2D) g.create();
        g2d_zawartosc.translate(widok_x, widok_y);
        g2d_zawartosc.scale(skalaProc / 100.0, skalaProc / 100.0);

        Graphics2D g2d_podzialka_poz = (Graphics2D) g.create();
        g2d_podzialka_poz.translate(widok_x, 0);
        g2d_podzialka_poz.scale(skalaProc / 100.0, 1);
        g2d_podzialka_poz.setColor(Color.BLACK);
        g2d_podzialka_poz.setStroke(new BasicStroke(1));

        Graphics2D g2d_podzialka_pion = (Graphics2D) g.create();
        g2d_podzialka_pion.translate(0, widok_y);
        g2d_podzialka_pion.scale(1, skalaProc / 100.0);
        g2d_podzialka_pion.setColor(Color.BLACK);
        g2d_podzialka_pion.setStroke(new BasicStroke(1));

        try {
            rysuj_wode_tory(g2d_zawartosc);
            for (Long ID : drogi.keySet()) {
                if (zmienna == false) {
                    if (drogi.get(ID).maxspeed != -1) {
                        rysujDroge(drogi.get(ID), Color.GREEN, g2d_zawartosc);
                    } else {
                        rysujDroge(drogi.get(ID), Color.RED, g2d_zawartosc);
                    }
                } else if (zmienna == true) {
                    if (drogi.get(ID).ruchUliczny != null) {
                        rysujDroge(drogi.get(ID), Color.GREEN, g2d_zawartosc);
                    } else {
                        rysujDroge(drogi.get(ID), Color.ORANGE, g2d_zawartosc);
                    }
                }
               
                for (Punkt p : drogi.get(ID).punkty) {
                    //rysujPunkt(p.X, p.Y, 2, Color.BLACK, g2d_zawartosc);
                    if (p.tags.getOrDefault("highway", "brak").equalsIgnoreCase("crossing")) {
                        //rysujPunkt(p.X, p.Y, 10, czerw_przezr, g2d_zawartosc);
                        rysujPunkt(p.X, p.Y, 2, Color.BLACK, g2d_zawartosc);
                    }
                }

                if (ZaznaczonyWezel != null) {
                    zaznaczWezel(g2d_zawartosc, ZaznaczonyWezel);
                }
            }
            if (!DANE.ALG_SCIEZKA.isEmpty()) {
                //System.out.println("dane maja sciezke");
                for (Droga dr : DANE.ALG_SCIEZKA) {
                    rysujDroge(dr, Color.magenta, g2d_zawartosc);
                    //System.out.println("rysuje jedna droge");
                }
            }
            rysuj_wezly(g2d_zawartosc);
            if (WYNIKI.czyWynikiDijkstra) {
                rysujWartosci(g2d_zawartosc, WYNIKI.wartosc_wezlow_dijkstra);
            } else if (WYNIKI.czyWynikiAStar) {
                rysujWartosci(g2d_zawartosc, WYNIKI.wartosc_wezlow_a_star);
            }

            rysujPodzialke(g2d_podzialka_poz, g2d_podzialka_pion);

        } finally {
            // wyrzucamy kopię, przywracamy oryginalny kontekst dla Swinga
            g2d_zawartosc.dispose();
            g2d_podzialka_pion.dispose();
            g2d_podzialka_poz.dispose();
        }
    }

    private void rysuj_wezly(Graphics2D g2d_zawartosc) {
        for (Wezel w : DANE.wezly.values()) {
            rysujPunkt(w.X, w.Y, 7, new Color(182, 5, 252, 80), g2d_zawartosc);
        }
        //new Color(5, 174, 252, 25)
    }

    private void zaznaczWezel(Graphics2D g2d_zawartosc, Wezel wez) {
        rysujPunkt(wez.X, wez.Y, 20, losowyKolor(), g2d_zawartosc);
    }

    private void rysujPodzialke(Graphics2D g2d_podzialka_poz, Graphics2D g2d_podzialka_pion) {
        // sztywna skala dla kresek
        g2d_podzialka_poz.setStroke(new BasicStroke((float) (1.0 / scale())));
        g2d_podzialka_pion.setStroke(new BasicStroke((float) (1.0 / scale())));

        int ilosc_podzialek_poz = 8;
        int ilosc_podzialek_pion = (int) (ilosc_podzialek_poz * this.getHeight() / this.getWidth()) + 1;

        int[] dopuszczalneSkoki = {10, 20, 50, 100, 200, 250, 500, 750, 1000, 2000, 2500, 5000};
        int wartosc_skok_surowa = (int) Math.abs(worldMaxX() - worldMinX()) / ilosc_podzialek_poz;

        int wartosc_skok = dopuszczalneSkoki[0]; // domyślna
        int roznicaMin = Math.abs(wartosc_skok_surowa - dopuszczalneSkoki[0]);

        for (int i = 1; i < dopuszczalneSkoki.length; i++) {
            int roznica = Math.abs(wartosc_skok_surowa - dopuszczalneSkoki[i]);
            if (roznica < roznicaMin) {
                roznicaMin = roznica;
                wartosc_skok = dopuszczalneSkoki[i];
            }
        }

        int offset_poz = (int) (Math.ceil(worldMinX() / wartosc_skok) * wartosc_skok) - wartosc_skok;
        int offset_pion = (int) (Math.ceil(worldMinY() / wartosc_skok) * wartosc_skok) - wartosc_skok;
        int i = 0;

        while (true) {
            int x_world = wartosc_skok * i + offset_poz; // współrzędna w świecie
            if (x_world > worldMaxX()) {
                break;
            }
            // rysuj kreskę w przeskalowanym układzie
            g2d_podzialka_poz.drawLine(x_world, -10, x_world, 10);

            // --- kontr-skalowanie dla tekstu ---
            AffineTransform old = g2d_podzialka_poz.getTransform();
            g2d_podzialka_poz.translate(x_world, 0);           // ustaw świat na xw
            g2d_podzialka_poz.scale(1.0 / scale(), 1.0);  // odwróć skalowanie w osi X
            g2d_podzialka_poz.drawString(x_world + "", -10, 20);     // tekst w pikselach ekranu
            g2d_podzialka_poz.setTransform(old);          // przywróć transformację
            i++;
        }
        i = 0;
        while (true) {

            int y_world = wartosc_skok * i + offset_pion; // współrzędna w świecie
            if (y_world > worldMaxY()) {
                break;
            }
            // rysuj kreskę w przeskalowanym układzie
            g2d_podzialka_pion.drawLine(10, y_world, 0, y_world);

            // --- kontr-skalowanie dla tekstu ---
            AffineTransform old = g2d_podzialka_pion.getTransform();
            g2d_podzialka_pion.translate(0, y_world);           // ustaw świat na y_w
            g2d_podzialka_pion.scale(1.0, 1.0 / scale());  // odwróć skalowanie w osi X
            g2d_podzialka_pion.drawString(y_world + "", 15, 5);     // tekst w pikselach ekranu
            g2d_podzialka_pion.setTransform(old);          // przywróć transformację
            i++;
        }
    }

    private void rysujDroge(Droga droga, Color kolor, Graphics2D g2d) {
        g2d.setColor(kolor);
        String tag = droga.tags.getOrDefault("highway", "-");
        if (tag.equals("trunk") || tag.equals("motorway") || tag.equals("primary") || tag.equals("secondary") || tag.equals("tertiary")
                || tag.equals("residential") || tag.equals("unclassified")) {
            g2d.setStroke(new BasicStroke(3));
        } else {
            g2d.setStroke(new BasicStroke(1));
        }

        int il_pkt = droga.punkty.size();
        for (int i = 1; i < il_pkt; i++) {
            g2d.drawLine((int) droga.punkty.get(i - 1).X, (int) droga.punkty.get(i - 1).Y, (int) droga.punkty.get(i).X, (int) droga.punkty.get(i).Y);
        }
    }

    private void rysujPunkt(double x, double y, int r, Color kolor, Graphics2D g2d) {
        g2d.setColor(kolor);
        x = x - r / 2;
        y = y - r / 2;
        g2d.fillOval((int) x, (int) y, (int) r, (int) r);
    }

    private double scale() {
        return skalaProc / 100.0;
    }

    private double worldX(int sx) {
        return (sx - widok_x) / scale();
    }

    private double worldY(int sy) {
        return (sy - widok_y) / scale();
    }

    private double worldMinX() {
        return -widok_x / scale();
    }

    private double worldMaxX() {
        return (-widok_x + getWidth()) / scale();
    }

    private double worldMinY() {
        return -widok_y / scale();
    }

    private double worldMaxY() {
        return (-widok_y + getHeight()) / scale();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        reset_widoku_button = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        infoKlik = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        infoX = new javax.swing.JLabel();
        infoY = new javax.swing.JLabel();
        infoUlica = new javax.swing.JLabel();
        CZYHERE = new javax.swing.JLabel();
        infoID = new javax.swing.JLabel();
        infoNetBeansID = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        roundedJPanel1 = new mgr.RoundedJPanel();
        skala_label = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();

        jButton1.setText("jButton1");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        reset_widoku_button.setText("RESET WIDOKU");
        reset_widoku_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_widoku_buttonActionPerformed(evt);
            }
        });

        jButton3.setText("stop");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setText("Info o kliku");

        infoX.setText("X:");

        infoY.setText("Y:");

        infoUlica.setText("Ulica:");

        CZYHERE.setText("czy ma HERE");

        infoID.setText("ID: ");

        infoNetBeansID.setText("Netbeans ID:");

        javax.swing.GroupLayout infoKlikLayout = new javax.swing.GroupLayout(infoKlik);
        infoKlik.setLayout(infoKlikLayout);
        infoKlikLayout.setHorizontalGroup(
            infoKlikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoKlikLayout.createSequentialGroup()
                .addGroup(infoKlikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoKlikLayout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(jLabel1))
                    .addGroup(infoKlikLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(infoKlikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(infoX)
                            .addGroup(infoKlikLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(infoKlikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(infoUlica)
                                    .addComponent(infoY)
                                    .addComponent(CZYHERE)
                                    .addComponent(infoID)
                                    .addComponent(infoNetBeansID))))))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        infoKlikLayout.setVerticalGroup(
            infoKlikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoKlikLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(infoY)
                .addGap(18, 18, 18)
                .addComponent(infoUlica)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(infoNetBeansID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(CZYHERE)
                .addGap(17, 17, 17))
        );

        jButton2.setText("ukryj");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        roundedJPanel1.setBackground(new java.awt.Color(255, 255, 255));

        skala_label.setBackground(new java.awt.Color(0, 0, 0));
        skala_label.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        skala_label.setText("jLabel1");

        javax.swing.GroupLayout roundedJPanel1Layout = new javax.swing.GroupLayout(roundedJPanel1);
        roundedJPanel1.setLayout(roundedJPanel1Layout);
        roundedJPanel1Layout.setHorizontalGroup(
            roundedJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedJPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(skala_label, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        roundedJPanel1Layout.setVerticalGroup(
            roundedJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedJPanel1Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(skala_label, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton4.setText("CHECK ALL");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jToggleButton1.setText("zmien widok");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(557, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(infoKlik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(118, 118, 118))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(roundedJPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reset_widoku_button, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jToggleButton1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3)))
                        .addGap(36, 36, 36))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addGap(12, 12, 12)
                .addComponent(infoKlik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 469, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToggleButton1)
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedJPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reset_widoku_button, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void reset_widoku_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_widoku_buttonActionPerformed
        // TODO add your handling code here:
        skalaProc = 100;
        widok_x = 30;
        widok_y = 30;
        repaint();
    }//GEN-LAST:event_reset_widoku_buttonActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        DANE.stopDebug();
        System.out.println("STOP");

    }//GEN-LAST:event_jButton3ActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        this.clickX = (int) worldX(evt.getX());
        this.clickY = (int) worldY(evt.getY());

        infoX.setText("X: " + clickX);
        infoY.setText("Y: " + clickY);

        int promien_lapania_zaznaczenia = 3;

        for (Wezel wez : wezly.values()) {
            if (abs(wez.X - clickX) < promien_lapania_zaznaczenia && abs(wez.Y - clickY) < promien_lapania_zaznaczenia) {
                // wybrano ten wezel
                this.ZaznaczonyWezel = wez;

                StringBuilder string = new StringBuilder("<html>Przynależne ulice:<br>");
                for (Long id : wez.drogiIDs) {
                    string.append(id).append(" ").append(drogi.get(id).nazwa).append("<br>");
                }
                string.append("</html>");
                infoUlica.setText(string.toString());

                infoID.setText("ID: " + wez.ID);
                infoNetBeansID.setText("NetBeansID: " + wez);
                repaint();
                return;
            }
        }

        this.ZaznaczonyWezel = null;
        infoUlica.setText("Ulica: -");
        infoID.setText("ID: ");
        infoNetBeansID.setText("NetBeansID: ");
        CZYHERE.setText("ma");
        repaint();
    }//GEN-LAST:event_formMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        infoKlik.setVisible(!infoKlik.isVisible());
        if (infoKlik.isVisible()) {
            jButton2.setText("Ukryj");
        } else {
            jButton2.setText("Pokaz");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        System.out.println("======================= CHECK ALL ========================");
        System.out.println("Ilosc relacji w DANE: " + DANE.relacje.size());

        int ilosc_dopasowanego_ruchu = 0;
        for (Droga dr : DANE.drogi.values()) {
            if (dr.ruchUliczny != null) {
                ilosc_dopasowanego_ruchu++;
            }
        }
        System.out.println("Ruch uliczny w pliku: " + DANE.ruchUliczny.size() + " | Ruch uliczny dopisany: " + ilosc_dopasowanego_ruchu);
        System.out.println("Drogi w DANE: " + DANE.drogi.size());
        System.out.println("Drogi jako tory: " + DANE.kolej.size());
        System.out.println("Drogi jako rzeki: " + DANE.rzeki.size());
        System.out.println("Ilosc wezlow: " + DANE.wezly.size());

        Set<Long> listaUniklanychIDosm = new HashSet<>();
        int licznik_brak_IDOSM = 0;
        for (Droga dr : DANE.drogi.values()) {
            if (dr.IDosm == -1) {
                licznik_brak_IDOSM++;
            } else {
                listaUniklanychIDosm.add(dr.IDosm);
            }
        }
        for (Droga dr : DANE.kolej.values()) {
            if (dr.IDosm == -1) {
                licznik_brak_IDOSM++;
            } else {
                listaUniklanychIDosm.add(dr.IDosm);
            }
        }
        for (Droga dr : DANE.rzeki.values()) {
            if (dr.IDosm == -1) {
                licznik_brak_IDOSM++;
            } else {
                listaUniklanychIDosm.add(dr.IDosm);
            }
        }
        for (Relacja rel : DANE.relacje.values()) {
            for (Droga dr : rel.drogi) {
                if (dr.IDosm == -1) {
                    licznik_brak_IDOSM++;
                } else {
                    listaUniklanychIDosm.add(dr.IDosm);
                }
            }
        }
        System.out.println("Liczba drog Z IDOSM w drogi+kolej+rzeki+relacje: " + listaUniklanychIDosm.size());
        System.out.println("Liczba drog BEZ IDOSM w drogi+kolej+rzeki+relacje: " + licznik_brak_IDOSM);

        int ilosc_przejsc = 0;
        int ilosc_sygnalizacji = 0;
        int ilosc_sygnalizacji_na_wezlach = 0;
        int ilosc_przejsc_na_wezlach = 0;
        for (Droga dr : DANE.drogi.values()) {
            for (Punkt pkt : dr.punkty) {
                if (pkt.tags.getOrDefault("highway", "-").equalsIgnoreCase("crossing")) {
                    if (pkt.ID == dr.punkty.getFirst().ID || pkt.ID == dr.punkty.getLast().ID) {
                        ilosc_przejsc_na_wezlach++;
                    } else {
                        ilosc_przejsc++;
                    }
                }
                if (pkt.tags.getOrDefault("highway", "-").equalsIgnoreCase("traffic_signals")) {
                    if (pkt.ID == dr.punkty.getFirst().ID || pkt.ID == dr.punkty.getLast().ID) {
                        ilosc_sygnalizacji_na_wezlach++;
                    } else {
                        ilosc_sygnalizacji++;
                    }
                }
            }
        }
        System.out.println("Ilosc przejsc dla pieszych (ilosc punktow z tagiem higway=crossing): " + ilosc_przejsc);
        System.out.println("Ilosc sygnalizacji swietlnej (ilosc punktow z tagiem higway=traffic_signals): " + ilosc_sygnalizacji);
        System.out.println("Ilosc przejsc dla pieszych NA WEZLACH (ilosc FIRST/LAST punktow z tagiem higway=crossing): " + ilosc_przejsc_na_wezlach);
        System.out.println("Ilosc sygnalizacji swietlnej NA WEZLACH (ilosc FIRST/LAST punktow z tagiem higway=traffic_signals): " + ilosc_sygnalizacji_na_wezlach);

        int ilosc_tagow_maxspeed = 0;
        int ilosc_brakow_maxspeed = 0;
        for (Droga dr : DANE.drogi.values()) {
            String maxspeedString = dr.tags.getOrDefault("maxspeed", "-");
            if (maxspeedString.equals("-")) {
                ilosc_brakow_maxspeed++;
                continue;
            }
            if (maxspeedString.equals("walk")){
                ilosc_tagow_maxspeed++;
                continue;
            }
            int maxSpeedInt = Integer.parseInt(maxspeedString);
            if (maxSpeedInt > 1) {
                ilosc_tagow_maxspeed++;
            }
        }
        System.out.println("Ilosc tagow maxspeed: " + ilosc_tagow_maxspeed + " / " + DANE.drogi.size());
        System.out.println("Ilosc brakow maxspeed: " + ilosc_brakow_maxspeed);

        HashSet<String> set_kategorii_drog = new HashSet();
        for (Droga dr : DANE.drogi.values()) {
            String kategoria_drogi = dr.tags.getOrDefault("highway", "-");
            set_kategorii_drog.add(kategoria_drogi);
        }
        System.out.println("Lista unikalnych kategorii drog: (" + set_kategorii_drog.size() + ")");
        System.out.println(set_kategorii_drog.toString());

        System.out.println("Ilosc ruchu_ulicznego: " + DANE.ruchUliczny.size());
        int ilosc_drog_w_DANE_z_ruchem = 0;
        int ilosc_drog_w_DANE_BEZ_ruchu = 0;
        HashSet<Long> ilosc_osmID_z_ruchem = new HashSet();
        HashSet<Long> ilosc_osmID_bez_ruchu = new HashSet();

        for (Droga dr : DANE.drogi.values()) {
            if (dr.ruchUliczny == null) {
                ilosc_drog_w_DANE_BEZ_ruchu++;
                ilosc_osmID_bez_ruchu.add(dr.IDosm);
            } else {
                ilosc_drog_w_DANE_z_ruchem++;
                ilosc_osmID_z_ruchem.add(dr.IDosm);
            }
        }
        System.out.println("Ilosc drog w DANE z ruchem ulicznym: " + ilosc_drog_w_DANE_z_ruchem);
        System.out.println("Ilosc uniklanych IDOSM z ruchem uliczym: " + ilosc_osmID_z_ruchem.size());
        System.out.println("Ilosc drog w DANE BEZ ruchu ulicznego: " + ilosc_drog_w_DANE_BEZ_ruchu);
        System.out.println("Ilosc uniklanych IDOSM BEZ ruchu ulicznego: " + ilosc_osmID_bez_ruchu.size());

        int liczba_max_speed = 0;
        int liczba_tf = 0;
        int liczba_kategoryzowanych = 0;
        int liczba_brakow = 0;
        for (Droga dr : DANE.drogi.values()) {
            if (dr.ruchUliczny != null) {
                liczba_tf++;
                continue;
            }
            String maxspeedString = dr.tags.getOrDefault("maxspeed", "-");
            if (!maxspeedString.equals("-")) {
                liczba_max_speed++;
                continue;
            }
            String kategoria_drogi = dr.tags.getOrDefault("highway", "-");
            if (kategoria_drogi == null || "".equals(kategoria_drogi) || kategoria_drogi.equalsIgnoreCase("unclassified")) {
                liczba_brakow++;
            } else {
                liczba_kategoryzowanych++;
            }
        }
        System.out.println("Maja tf: " + liczba_tf);
        System.out.println("Maja maxspeed: " + liczba_max_speed);
        System.out.println("Maja kategorie: " + liczba_kategoryzowanych);
        System.out.println("Braki: " + liczba_brakow);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        this.zmienna = !zmienna;
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    static Color losowyKolor() {
        var r = ThreadLocalRandom.current();
        return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)); // nowy za każdym wywołaniem
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CZYHERE;
    private javax.swing.JLabel infoID;
    private javax.swing.JPanel infoKlik;
    private javax.swing.JLabel infoNetBeansID;
    private javax.swing.JLabel infoUlica;
    private javax.swing.JLabel infoX;
    private javax.swing.JLabel infoY;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JButton reset_widoku_button;
    private mgr.RoundedJPanel roundedJPanel1;
    private javax.swing.JLabel skala_label;
    // End of variables declaration//GEN-END:variables

    private void rysujWartosci(Graphics2D g2d, Map<Long, Double> wartoscWezlow) {

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 5));

        for (Long wez_id : wartoscWezlow.keySet()) {
            float x = (float) DANE.wezly.get(wez_id).X;
            float y = (float) DANE.wezly.get(wez_id).Y;
            String wartosc = String.format("%.1f", wartoscWezlow.get(wez_id));
            if (!wartosc.equalsIgnoreCase("infinity")) {
                g2d.drawString(wartosc, x + 5, y);
            }
        }
    }

    private void rysuj_wode_tory(Graphics2D g2d) {

        // rzeki
        for (Long rzeka_id : DANE.rzeki.keySet()) {
            Droga rzeka = DANE.rzeki.get(rzeka_id);
            rysujDroge(rzeka, this.kolor_wody, g2d);
        }

        // zbiorniki wodne
        for (Long zbiornik_id : DANE.woda.keySet()) {
            Droga zbiornik = DANE.woda.get(zbiornik_id);

            if (zbiornik == null || zbiornik.punkty == null || zbiornik.punkty.size() < 3) {
                continue;
            }

            Punkt pierwszy = zbiornik.punkty.get(0);
            Punkt ostatni = zbiornik.punkty.get(zbiornik.punkty.size() - 1);

            // rysuj tylko zamkniete obrysy
            if (pierwszy.ID != ostatni.ID) {
                continue;
            }

            int nPoints = zbiornik.punkty.size();
            int[] xPoints = new int[nPoints];
            int[] yPoints = new int[nPoints];

            for (int i = 0; i < nPoints; i++) {
                Punkt p = zbiornik.punkty.get(i);
                xPoints[i] = (int) p.X;
                yPoints[i] = (int) p.Y;
            }
            g2d.setColor(kolor_wody);
            g2d.drawPolygon(xPoints, yPoints, nPoints);
            g2d.fillPolygon(xPoints, yPoints, nPoints);

        }

        // relacje (zbiorniki też)
        for (Long rel_id : DANE.relacje.keySet()) {
            Relacja rel = DANE.relacje.get(rel_id);

            if (rel == null || rel.drogi == null || rel.drogi.isEmpty()) {
                continue;
            }

            ArrayList<ArrayList<Punkt>> ringi = zbudujRingiZRelacji(rel.drogi);

            g2d.setColor(kolor_wody);

            for (ArrayList<Punkt> ring : ringi) {
                if (ring.size() < 3) {
                    continue;
                }

                Punkt pierwszy = ring.get(0);
                Punkt ostatni = ring.get(ring.size() - 1);

                if (pierwszy.ID != ostatni.ID) {
                    continue;
                }

                int nPoints = ring.size();
                int[] xPoints = new int[nPoints];
                int[] yPoints = new int[nPoints];

                for (int i = 0; i < nPoints; i++) {
                    Punkt p = ring.get(i);
                    xPoints[i] = (int) p.X;
                    yPoints[i] = (int) p.Y;
                }

                g2d.fillPolygon(xPoints, yPoints, nPoints);
                g2d.drawPolygon(xPoints, yPoints, nPoints);
            }
        }

        // tory
        for (Long kolej_id : DANE.kolej.keySet()) {
            Droga kolej = DANE.kolej.get(kolej_id);
            rysujDroge(kolej, Color.DARK_GRAY, g2d);
        }
    }

    private ArrayList<ArrayList<Punkt>> zbudujRingiZRelacji(ArrayList<Droga> drogiRelacji) {
        ArrayList<ArrayList<Punkt>> ringi = new ArrayList<>();

        ArrayList<Droga> pozostale = new ArrayList<>();

        for (Droga d : drogiRelacji) {
            if (d != null && d.punkty != null && d.punkty.size() >= 2) {
                pozostale.add(d);
            }
        }

        while (!pozostale.isEmpty()) {
            Droga pierwszaDroga = pozostale.remove(0);

            ArrayList<Punkt> ring = new ArrayList<>(pierwszaDroga.punkty);

            boolean cosDolaczono = true;

            while (cosDolaczono) {
                cosDolaczono = false;

                Punkt pierwszy = ring.get(0);
                Punkt ostatni = ring.get(ring.size() - 1);

                for (int i = 0; i < pozostale.size(); i++) {
                    Droga d = pozostale.get(i);

                    Punkt dPierwszy = d.punkty.get(0);
                    Punkt dOstatni = d.punkty.get(d.punkty.size() - 1);

                    // koniec aktualnego ringu łączy się z początkiem drogi
                    if (ostatni.ID == dPierwszy.ID) {
                        for (int j = 1; j < d.punkty.size(); j++) {
                            ring.add(d.punkty.get(j));
                        }

                        pozostale.remove(i);
                        cosDolaczono = true;
                        break;
                    }

                    // koniec aktualnego ringu łączy się z końcem drogi, więc trzeba odwrócić
                    if (ostatni.ID == dOstatni.ID) {
                        for (int j = d.punkty.size() - 2; j >= 0; j--) {
                            ring.add(d.punkty.get(j));
                        }

                        pozostale.remove(i);
                        cosDolaczono = true;
                        break;
                    }

                    // początek aktualnego ringu łączy się z końcem drogi
                    if (pierwszy.ID == dOstatni.ID) {
                        ArrayList<Punkt> nowyRing = new ArrayList<>();

                        for (int j = 0; j < d.punkty.size() - 1; j++) {
                            nowyRing.add(d.punkty.get(j));
                        }

                        nowyRing.addAll(ring);
                        ring = nowyRing;

                        pozostale.remove(i);
                        cosDolaczono = true;
                        break;
                    }

                    // początek aktualnego ringu łączy się z początkiem drogi, więc trzeba odwrócić
                    if (pierwszy.ID == dPierwszy.ID) {
                        ArrayList<Punkt> nowyRing = new ArrayList<>();

                        for (int j = d.punkty.size() - 1; j >= 1; j--) {
                            nowyRing.add(d.punkty.get(j));
                        }

                        nowyRing.addAll(ring);
                        ring = nowyRing;

                        pozostale.remove(i);
                        cosDolaczono = true;
                        break;
                    }
                }
            }

            Punkt pierwszy = ring.get(0);
            Punkt ostatni = ring.get(ring.size() - 1);

            if (pierwszy.ID == ostatni.ID) {
                ringi.add(ring);
            }
        }

        return ringi;
    }
}
