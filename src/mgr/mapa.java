package mgr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class mapa extends javax.swing.JPanel {

    private int skalaProc = 100;           // 100%
    private double widok_x = 0;
    private double widok_y = 0;
    private int prevMouseX, prevMouseY;

    public mapa() {
        initComponents();

        this.setBackground(Color.LIGHT_GRAY);

        java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                skala_label.setText("X: " + (int) (e.getX() - widok_x) + " Y: " + (int) (e.getY() - widok_y) + " SKALA: " + skalaProc + "%");
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
                int notches = e.getWheelRotation(); // >0 w dół (oddal), <0 w górę (przybliż)
                int oldProc = skalaProc;

                // krok = 10% bieżącej skali
                int step = (int) Math.round(skalaProc * 0.1);
                if (step < 1) {
                    step = 1; // zabezpieczenie, żeby zawsze coś się zmieniało
                }
                skalaProc += -notches * step;   // kółko w górę -> +skala, w dół -> -skala
                skalaProc = Math.max(10, Math.min(500, skalaProc)); // zakres 10%–500%

                if (skalaProc != oldProc) {
                    double sOld = oldProc / 100.0;
                    double sNew = skalaProc / 100.0;
                    double factor = sNew / sOld;

                    // utrzymaj punkt pod kursorem
                    double cx = e.getX();
                    double cy = e.getY();
                    widok_x = cx - factor * (cx - widok_x);
                    widok_y = cy - factor * (cy - widok_y);
                    skala_label.setText("X: " + (int) (e.getX() - widok_x) + " Y: " + (int) (e.getY() - widok_y) + " SKALA: " + skalaProc + "%");
                    repaint();
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

//    @Override
//protected void paintComponent(java.awt.Graphics g) {
//    super.paintComponent(g);
//    java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
//
//    // zastosuj przesunięcie i skalę BEZ kopii kontekstu
//    g2d.translate(widok_x, widok_y);
//    g2d.scale(skalaProc / 100.0, skalaProc / 100.0);
//
//    rysujPunkt(0, 0, Color.RED, g2d);
//}
//    
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // najpierw normalne czyszczenie tła i narysowanie komponentów-dzieci
        super.paintComponent(g);

        // robimy KOPIĘ kontekstu graficznego
        Graphics2D g2d = (Graphics2D) g.create();
        rysujLinijke(g2d);
        try {
            // zastosuj przesunięcie i skalę tylko na tej kopii
            g2d.translate(widok_x, widok_y);
            g2d.scale(skalaProc / 100.0, skalaProc / 100.0);

            // tu rysujesz wszystko co „mapa”
            rysujPunkt(0, 0, Color.RED, g2d);
            // możesz dodać rysujLinijke(g2d), rysujSkale(g2d) itp.
        } finally {
            // wyrzucamy kopię, przywracamy oryginalny kontekst dla Swinga
            g2d.dispose();
        }
    }

    private void rysujPunkt(double x, double y, Color kolor, Graphics2D g2d) {
        int r = 10;
        g2d.setColor(kolor);
        x = x - r / 2;
        y = y - r / 2;
        g2d.fillOval((int) x, (int) y, (int) r, (int) r);
    }

    private void rysujLinijke(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        int odstep = 15;
        g2d.drawLine(odstep, odstep, this.getWidth() - 2*odstep, odstep);

        
        int ilosc_przedzialek =5;
        int szerokosc = (int)(this.getWidth()-2*odstep) / ilosc_przedzialek;
        int dlugosc_przedzialki = 30;
        for (int i=1; i<=5; i++){
            g2d.drawLine(odstep+szerokosc*i, odstep, dlugosc_przedzialki+odstep, odstep);
        }
        
        
        g2d.drawLine(odstep, odstep, odstep, this.getHeight() - 2*odstep);
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
        jButton2 = new javax.swing.JButton();
        skala_label = new javax.swing.JLabel();

        jButton1.setText("jButton1");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jButton2.setText("jButton2");

        skala_label.setBackground(new java.awt.Color(102, 0, 102));
        skala_label.setForeground(new java.awt.Color(102, 0, 102));
        skala_label.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(114, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(skala_label)
                        .addGap(378, 378, 378))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(291, 291, 291)
                .addComponent(skala_label)
                .addContainerGap(416, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel skala_label;
    // End of variables declaration//GEN-END:variables
}
