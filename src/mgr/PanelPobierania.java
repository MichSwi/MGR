/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mgr;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextField;

/**
 *
 * @author Micha
 */
public class PanelPobierania extends javax.swing.JPanel {

    /**
     * Creates new form PanelPobierania
     */
    private int tryb;          //1 - granice  2 - obszar  3 - kolo

    private List<TrafficSegment> TrafficFlow = new ArrayList<>();
    private List<Droga> drogi = new ArrayList<>();
    private Map<Long, Punkt> punktyLista = new HashMap<>();

    public PanelPobierania() {
        initComponents();
        tryb = 1;
        jRadioButtonGran.setSelected(true);
        jProgressBarOSMDOWNLOAD.setStringPainted(true);
        jProgressBarOSMDOWNLOAD.setString("");
        jProgressBarOSMREAD.setStringPainted(true);
        jProgressBarOSMREAD.setString("");
        jProgressBarTFDOWNLOAD.setStringPainted(true);
        jProgressBarTFDOWNLOAD.setString("");
        jProgressBarTFREAD.setStringPainted(true);
        jProgressBarTFREAD.setString("");

        przelaczTryb();
        wywolajListenery();
        DANE._4_N_WYS = 0;
        DANE._1_W_LAT = 0;
        DANE._2_S_LON = 0;
        DANE._3_E_SZER_R = 0;

        // NEWS: 54.160, 19.380, 54.175, 19.430
        jTextField1.setText("13.3905");
        jTextField2.setText("52.5110");
        jTextField3.setText("13.4195"); //BERLIN
        jTextField4.setText("52.5290");

//        jTextField1.setText("19.3935"); // W
//        jTextField2.setText("54.1430"); // S   ELBLAG
//        jTextField3.setText("19.4241"); // E
//        jTextField4.setText("54.1910"); // N

//        jTextField1.setText("18.5942"); // SW lon = 18.6097 - 0.0155
//        jTextField2.setText("54.3729"); // SW lat = 54.3819 - 0.0090
//        jTextField3.setText("18.6252"); // NE lon = 18.6097 + 0.0155
//        jTextField4.setText("54.3909"); // NE lat = 54.3819 + 0.0090   WRZESZCZ 2km 2km
        
//        jTextField1.setText("18.6083"); // lon - 0.0014
//jTextField2.setText("54.3810"); // lat - 0.0009
//// NE (+100m)
//jTextField3.setText("18.6111"); // lon + 0.0014
//jTextField4.setText("54.3828"); // lat + 0.0009  // WRZESZ 100m X 100m
    }

    private void przelaczTryb() {
        //1 - granice  2 - obszar prost  3 -kolo

        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");

        if (tryb == 1) {
            jLabel1.setText("Zachodnia granica W:");
            jLabel2.setText("Południowa granica S:");
            jLabel3.setText("Wschodnia granica E:");

            jLabel4.setVisible(true);
            jTextField4.setVisible(true);
            jLabel4.setText("Północna granica N:");

        } else if (tryb == 2) {
            jLabel1.setText("Szerokosc geograficzna LAT:");
            jLabel2.setText("Długość geograficzna LON:");
            jLabel3.setText("Szerokość obszaru:");

            jLabel4.setVisible(true);
            jTextField4.setVisible(true);
            jLabel4.setText("Wysokość obszaru:");

        } else if (tryb == 3) {
            jLabel1.setText("Szerokosc geograficzna:");
            jLabel2.setText("Długość geograficzna:");
            jLabel3.setText("Promień:");

            jLabel4.setVisible(false);
            jTextField4.setVisible(false);
        }
    }

    private void FormatujDane(JTextField textField, boolean czyWspolzedna) {

        String wpisane = textField.getText();
        String doWyswietlenia;
        DecimalFormat df;
        String koncowka;

        if (czyWspolzedna) {
            df = new DecimalFormat("0.000000");
        } else {
            df = new DecimalFormat("0.000");
        }

        try {
            doWyswietlenia = wpisane.replace(",", ".");
            double wartosc = Double.parseDouble(doWyswietlenia);

            if (czyWspolzedna) {
                df = new DecimalFormat("0.000000");
                koncowka = "";
            } else {
                df = new DecimalFormat("0.000");
                koncowka = " km";
            }

            doWyswietlenia = df.format(wartosc);
            doWyswietlenia = wpisane.replace(",", ".");
            textField.setText(doWyswietlenia + koncowka);

        } catch (NumberFormatException e) {
            textField.setText("");
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabelPobOSM = new javax.swing.JLabel();
        jProgressBarOSMDOWNLOAD = new javax.swing.JProgressBar();
        jLabelPobTF = new javax.swing.JLabel();
        jProgressBarTFDOWNLOAD = new javax.swing.JProgressBar();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButtonGran = new javax.swing.JRadioButton();
        jRadioButtonpProst = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldNazwaPliku = new javax.swing.JTextField();
        jButtonPobierz = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jRadioButtonOkrag = new javax.swing.JRadioButton();
        jButton4 = new javax.swing.JButton();
        jProgressBarOSMREAD = new javax.swing.JProgressBar();
        jLabelPobOSM1 = new javax.swing.JLabel();
        jProgressBarTFREAD = new javax.swing.JProgressBar();
        jLabelPobTF1 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();

        setAutoscrolls(true);

        jLabelPobOSM.setText("Pobieranie OSM");

        jLabelPobTF.setText("Pobieranie Traffic Flow");

        jTextField1.setText("jTextField1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setText("jTextField2");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField3.setText("jTextField3");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.setText("TextField4");
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel1.setText("jLabel1");

        jLabel2.setText("jLabel2");

        jLabel3.setText("jLabel3");

        jLabel4.setText("jLabel4");

        buttonGroup1.add(jRadioButtonGran);
        jRadioButtonGran.setText("Granice");
        jRadioButtonGran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonGranActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButtonpProst);
        jRadioButtonpProst.setText("Obszar prostokątny");
        jRadioButtonpProst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonpProstActionPerformed(evt);
            }
        });

        jLabel5.setText("Nazwa pliku:");

        jLabel6.setText("Plik pomyślnie zapisano w: ////");

        jTextFieldNazwaPliku.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextFieldNazwaPliku.setText("el");

        jButtonPobierz.setText("Pobierz");
        jButtonPobierz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPobierzActionPerformed(evt);
            }
        });

        jLabel7.setText("Ilość pobranych TF w miesiącu: ");

        buttonGroup1.add(jRadioButtonOkrag);
        jRadioButtonOkrag.setText("Obszar okrągły");
        jRadioButtonOkrag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonOkragActionPerformed(evt);
            }
        });

        jButton4.setText("STOP DEBUG");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabelPobOSM1.setText("Czytanie OSM");

        jLabelPobTF1.setText("Czytanie TF");

        jCheckBox2.setSelected(true);

        jCheckBox5.setText("text stworz,pobierz czy cos");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addComponent(jRadioButtonGran)
                .addGap(38, 38, 38)
                .addComponent(jRadioButtonpProst)
                .addGap(28, 28, 28)
                .addComponent(jRadioButtonOkrag)
                .addGap(0, 352, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(264, 264, 264)
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(308, 308, 308)
                        .addComponent(jButtonPobierz)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(76, 76, 76))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldNazwaPliku, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3)
                            .addComponent(jTextField4))))
                .addGap(87, 87, 87)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox5)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelPobOSM)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jProgressBarTFDOWNLOAD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                        .addComponent(jProgressBarOSMDOWNLOAD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jProgressBarOSMREAD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jProgressBarTFREAD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabelPobOSM1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox2))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabelPobTF1)
                            .addGap(18, 18, 18)
                            .addComponent(jCheckBox4)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelPobTF)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox3)))
                .addGap(135, 135, 135))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonGran)
                    .addComponent(jRadioButtonpProst)
                    .addComponent(jRadioButtonOkrag))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPobOSM)
                            .addComponent(jCheckBox1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jProgressBarOSMDOWNLOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPobOSM1)
                    .addComponent(jCheckBox2))
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jProgressBarOSMREAD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldNazwaPliku, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPobTF)
                            .addComponent(jCheckBox3))
                        .addGap(25, 25, 25)
                        .addComponent(jProgressBarTFDOWNLOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPobTF1)
                    .addComponent(jCheckBox4))
                .addGap(6, 6, 6)
                .addComponent(jProgressBarTFREAD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jCheckBox5)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addComponent(jButtonPobierz)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(69, 69, 69)
                .addComponent(jButton4)
                .addGap(61, 61, 61))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        FormatujDane(jTextField1, true);
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
        FormatujDane(jTextField2, true);
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
        if (tryb == 1)
            FormatujDane(jTextField3, true);
        else
            FormatujDane(jTextField3, false);
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
        if (tryb == 1)
            FormatujDane(jTextField4, true);
        else
            FormatujDane(jTextField4, false);
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jRadioButtonGranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonGranActionPerformed
        // TODO add your handling code here:
        tryb = 1;
        przelaczTryb();
    }//GEN-LAST:event_jRadioButtonGranActionPerformed

    private void jRadioButtonpProstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonpProstActionPerformed
        // TODO add your handling code here:
        tryb = 2;
        przelaczTryb();
    }//GEN-LAST:event_jRadioButtonpProstActionPerformed

    private void jButtonPobierzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPobierzActionPerformed
        // TODO add your handling code here:
        if (!jTextField1.getText().isEmpty()
                || !jTextField2.getText().isEmpty()
                || !jTextField3.getText().isEmpty()
                || !jTextField4.getText().isEmpty()) {

            DANE._1_W_LAT = Double.parseDouble(jTextField1.getText());
            DANE._2_S_LON = Double.parseDouble(jTextField2.getText());
            DANE._3_E_SZER_R = Double.parseDouble(jTextField3.getText());
            DANE._4_N_WYS = Double.parseDouble(jTextField4.getText());

            DANE.coZaznaczone.add(jCheckBox1.isSelected());
            DANE.coZaznaczone.add(jCheckBox2.isSelected());
            DANE.coZaznaczone.add(jCheckBox3.isSelected());
            DANE.coZaznaczone.add(jCheckBox4.isSelected());
            DANE.coZaznaczone.add(jCheckBox5.isSelected());

            WatekPobierz watekPobierania = new WatekPobierz(TrafficFlow, drogi, punktyLista,
                    jProgressBarOSMDOWNLOAD, jProgressBarTFDOWNLOAD,
                    jProgressBarOSMREAD, jProgressBarTFREAD,
                    tryb);
            watekPobierania.execute();
        }
    }//GEN-LAST:event_jButtonPobierzActionPerformed

    private void jRadioButtonOkragActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonOkragActionPerformed
        // TODO add your handling code here:
        tryb = 3;
        przelaczTryb();
    }//GEN-LAST:event_jRadioButtonOkragActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        System.out.println("STOP DEBUG)");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void wywolajListenery() {
        jTextField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                FormatujDane(jTextField1, true);
            }
        });

        jTextField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                FormatujDane(jTextField2, true);
            }
        });

        jTextField3.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                FormatujDane(jTextField3, tryb == 1);
            }
        });

        jTextField4.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                FormatujDane(jTextField4, tryb == 1);
            }
        });

        jTextFieldNazwaPliku.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                DANE.nazwaPliku = jTextFieldNazwaPliku.getText().trim();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonPobierz;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelPobOSM;
    private javax.swing.JLabel jLabelPobOSM1;
    private javax.swing.JLabel jLabelPobTF;
    private javax.swing.JLabel jLabelPobTF1;
    private javax.swing.JProgressBar jProgressBarOSMDOWNLOAD;
    private javax.swing.JProgressBar jProgressBarOSMREAD;
    private javax.swing.JProgressBar jProgressBarTFDOWNLOAD;
    private javax.swing.JProgressBar jProgressBarTFREAD;
    private javax.swing.JRadioButton jRadioButtonGran;
    private javax.swing.JRadioButton jRadioButtonOkrag;
    private javax.swing.JRadioButton jRadioButtonpProst;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextFieldNazwaPliku;
    // End of variables declaration//GEN-END:variables
}
