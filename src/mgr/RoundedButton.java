package mgr;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {

    public RoundedButton() {
        super("Rounded");
        init();
    }

    public RoundedButton(String text) {
        super(text);
        init();
    }

    private void init() {
        setFocusPainted(true);
        setContentAreaFilled(true);
        setOpaque(false);
        setBorder(new RoundedBorder());
    }

    @Override
    public Dimension getPreferredSize() {
        // standardowy rozmiar jak zwykły JButton "OK"
        return new Dimension(75, 23);
    }

    // --- ramka zaokrąglona ---
    private static class RoundedBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // radius = wysokość przycisku
            int radius = height;

            // kolor ramki z Look&Feel (jak w zwykłym JButton)
            Color borderColor = UIManager.getColor("Button.borderColor");
            if (borderColor == null) {
                borderColor = c.getForeground(); // fallback
            }

            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    @Override
    public boolean contains(int x, int y) {
        int radius = getHeight();
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        return shape.contains(x, y);
    }
}
