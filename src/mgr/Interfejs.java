/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mgr;

import java.awt.*;
import javax.swing.JPanel;

/**
 *
 * @author Micha
 */
public class Interfejs extends JPanel {

    public Interfejs() {
        setPreferredSize(new Dimension(200, 150));
        setBackground(Color.WHITE); // tło panelu
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;  // rzutowanie na Graphics2D

        g2.setColor(Color.BLACK);
        int wysokoscPanelu = getHeight();
        int szerokoscPanelu = getWidth();
        int wysokoscProstokata = (int) (wysokoscPanelu * 0.8);
        int szerokoscProstokata = (int) (szerokoscPanelu * 0.8);
        int x = szerokoscPanelu / 2 - szerokoscProstokata / 2;
        int y = wysokoscPanelu / 2 - wysokoscProstokata / 2;

        g2.fillRect(x, y, szerokoscProstokata, wysokoscProstokata);

        GradientPaint gp = new GradientPaint(
                20, 100, Color.YELLOW, // punkt startowy + kolor
                190, 100, Color.RED // punkt końcowy + kolor
        );

        g2.setPaint(gp);
        g2.setStroke(new BasicStroke(10f));
        g2.drawLine(20, 100, 190, 100);
    }

}
