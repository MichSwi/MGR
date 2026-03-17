/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mgr;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextField;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    private Double temp_LAT, temp_LON;

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
            double wartosc;
            if (doWyswietlenia.endsWith(" km")) {
                wartosc = Double.parseDouble(doWyswietlenia.trim().substring(0, doWyswietlenia.trim().length() - 3));
            } else {
                wartosc = Double.parseDouble(doWyswietlenia);
            }

            if (czyWspolzedna) {
                df = new DecimalFormat("0.000000");
                koncowka = "";
            } else {
                df = new DecimalFormat("0.000");
                koncowka = " km";
            }

            doWyswietlenia = df.format(wartosc);
            doWyswietlenia = wpisane.replace(",", ".");
            if (doWyswietlenia.trim().endsWith(" km")) {
                textField.setText(doWyswietlenia);
            } else {
                textField.setText(doWyswietlenia + koncowka);
            }
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
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        miasto_input = new javax.swing.JTextField();
        miasto_info = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        wys_input = new javax.swing.JTextField();
        szer_input = new javax.swing.JTextField();

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

        jLabel8.setText("Wpisz nazwę miasta:");

        jButton1.setText("Wyszukaj");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        miasto_input.setText("Wpisz miasto");
        miasto_input.setToolTipText("");
        miasto_input.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                miasto_inputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                miasto_inputFocusLost(evt);
            }
        });
        miasto_input.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                miasto_inputCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        miasto_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                miasto_inputKeyReleased(evt);
            }
        });

        jLabel9.setText("Wysokość obszaru:");

        jLabel10.setText("Szerokość obszaru:");

        jButton2.setText("Wpisz współrzędne");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        wys_input.setEnabled(false);
        wys_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wys_inputActionPerformed(evt);
            }
        });
        wys_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                wys_inputKeyReleased(evt);
            }
        });

        szer_input.setEnabled(false);
        szer_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                szer_inputActionPerformed(evt);
            }
        });
        szer_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                szer_inputKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(52, 52, 52)
                                .addComponent(miasto_input, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(miasto_info, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(wys_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(szer_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(19, 19, 19))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jButton2)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(miasto_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(miasto_info, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(wys_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(szer_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(jButton2)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldNazwaPliku, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(76, 76, 76))
            .addGroup(layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addComponent(jRadioButtonGran)
                .addGap(38, 38, 38)
                .addComponent(jRadioButtonpProst)
                .addGap(28, 28, 28)
                .addComponent(jRadioButtonOkrag)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(264, 264, 264)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonPobierz, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonGran)
                    .addComponent(jRadioButtonpProst)
                    .addComponent(jRadioButtonOkrag))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPobOSM)
                            .addComponent(jCheckBox1))
                        .addGap(19, 19, 19)
                        .addComponent(jProgressBarOSMDOWNLOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabelPobOSM1)
                                    .addComponent(jCheckBox2))
                                .addGap(7, 7, 7)
                                .addComponent(jProgressBarOSMREAD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(186, 186, 186)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabelPobTF)
                                    .addComponent(jCheckBox3)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldNazwaPliku, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))))
                        .addGap(25, 25, 25)
                        .addComponent(jProgressBarTFDOWNLOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPobTF1)
                            .addComponent(jCheckBox4))
                        .addGap(6, 6, 6)
                        .addComponent(jProgressBarTFREAD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jCheckBox5)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonPobierz, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                && !jTextField2.getText().isEmpty()
                && !jTextField3.getText().isEmpty()
                && !jTextField4.getText().isEmpty()) {

            DANE._1_W_LAT = Double.parseDouble(jTextField1.getText());
            DANE._2_S_LON = Double.parseDouble(jTextField2.getText());
            DANE._3_E_SZER_R = Double.parseDouble(jTextField3.getText());
            DANE._4_N_WYS = Double.parseDouble(jTextField4.getText());

            DANE.coZaznaczone.add(jCheckBox1.isSelected());
            DANE.coZaznaczone.add(jCheckBox2.isSelected());
            DANE.coZaznaczone.add(jCheckBox3.isSelected());
            DANE.coZaznaczone.add(jCheckBox4.isSelected());
            DANE.coZaznaczone.add(jCheckBox5.isSelected());

            WatekPobierz watekPobierania = new WatekPobierz(drogi, punktyLista,
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String miasto = miasto_input.getText().trim();

        if (miasto.isEmpty()) {
            miasto_info.setVisible(true);
            miasto_info.setText("Podaj nazwę miasta");
            miasto_info.setForeground(Color.RED);
            wys_input.setEnabled(false);
            szer_input.setEnabled(false);
            wys_input.setText("");
            szer_input.setText("");
            return;
        }

        String url = "https://nominatim.openstreetmap.org/search"
                + "?format=jsonv2"
                + "&limit=10"
                + "&q=" + URLEncoder.encode(miasto, StandardCharsets.UTF_8);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "MojaAplikacjaMagisterska/1.0 kontakt: mojmail@domena.pl")
                    .header("Accept", "application/json")
                    .header("Accept-Language", "pl")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                miasto_info.setVisible(true);
                miasto_info.setText("Błąd HTTP: " + response.statusCode());
                miasto_info.setForeground(Color.RED);
                wys_input.setEnabled(false);
                szer_input.setEnabled(false);
                wys_input.setText("");
                szer_input.setText("");
                System.out.println(response.body());
                return;
            }

            JSONArray arr = new JSONArray(response.body());
            boolean znaleziono = false;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String addresstype = o.optString("addresstype");

                if ("city".equals(addresstype) || "town".equals(addresstype) || "village".equals(addresstype)) {
                    this.temp_LAT = Double.parseDouble(o.getString("lat"));
                    this.temp_LON = Double.parseDouble(o.getString("lon"));

                    miasto_info.setVisible(true);
                    miasto_info.setText("Znaleziono: " + o.getString("display_name"));
                    miasto_info.setForeground(Color.GREEN);
                    wys_input.setEnabled(true);
                    szer_input.setEnabled(true);

                    znaleziono = true;
                    break;
                }
            }

            if (!znaleziono) {
                miasto_info.setVisible(true);
                miasto_info.setText("Nie udało się znaleźć \"" + miasto + "\"");
                miasto_info.setForeground(Color.RED);
                wys_input.setEnabled(false);
                szer_input.setEnabled(false);
                wys_input.setText("");
                szer_input.setText("");
            }

        } catch (IOException | InterruptedException e) {
            miasto_info.setVisible(true);
            miasto_info.setText("Błąd połączenia: " + e.getMessage());
            miasto_info.setForeground(Color.RED);
            wys_input.setEnabled(false);
            szer_input.setEnabled(false);
            wys_input.setText("");
            szer_input.setText("");
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        jRadioButtonGran.setSelected(true);
        this.tryb = 1;
        double szer = Double.parseDouble(szer_input.getText().substring(0, szer_input.getText().length() - 3));
        double wys = Double.parseDouble(wys_input.getText().substring(0, wys_input.getText().length() - 3));
        szer = szer * 0.009;
        wys = wys * 0.009;

        jTextField1.setText(Double.toString((temp_LON - szer / 2)));
        jTextField2.setText(Double.toString(temp_LAT - wys / 2));
        jTextField3.setText(Double.toString(temp_LON + szer / 2));
        jTextField4.setText(Double.toString(temp_LAT + wys / 2));


    }//GEN-LAST:event_jButton2ActionPerformed

    private void wys_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wys_inputActionPerformed
        // TODO add your handling code here:
        FormatujDane(wys_input, false);
    }//GEN-LAST:event_wys_inputActionPerformed

    private void szer_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_szer_inputActionPerformed
        // TODO add your handling code here:
        FormatujDane(szer_input, false);

    }//GEN-LAST:event_szer_inputActionPerformed

    private void miasto_inputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_miasto_inputFocusGained
        // TODO add your handling code here:

        if (miasto_input.getText().equals("Wpisz miasto")) {
            miasto_input.setText("");
        }
    }//GEN-LAST:event_miasto_inputFocusGained

    private void miasto_inputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_miasto_inputFocusLost
        // TODO add your handling code here:
        if (miasto_input.getText().isBlank()) {
            miasto_input.setText("Wpisz miasto");
        }
    }//GEN-LAST:event_miasto_inputFocusLost

    private void miasto_inputCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_miasto_inputCaretPositionChanged
        // TODO add your handling code here:
        if (miasto_input.getText().isBlank() == false) {
            jButton1.setEnabled(true);
        } else {
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_miasto_inputCaretPositionChanged

    private void miasto_inputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_miasto_inputKeyReleased
        // TODO add your handling code here:
        if (miasto_input.getText().isBlank() == false) {
            jButton1.setEnabled(true);
        } else {
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_miasto_inputKeyReleased

    private void wys_inputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_wys_inputKeyReleased
        // TODO add your handling code here:


    }//GEN-LAST:event_wys_inputKeyReleased

    private void szer_inputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_szer_inputKeyReleased
        // TODO add your handling code here:


    }//GEN-LAST:event_szer_inputKeyReleased

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
        wys_input.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                FormatujDane(wys_input, false);
            }
        });
        szer_input.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                FormatujDane(szer_input, false);
            }
        });

//        wys_input.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyReleased(KeyEvent e) {
//                FormatujDane(wys_input, false);
//
//                if (wys_input.getText().isBlank() == false) {
//
//                    try {
//                        String wys = wys_input.getText().trim().substring(0, wys_input.getText().trim().length() - 3);;
//                        String szer = szer_input.getText().trim().substring(0, szer_input.getText().trim().length() - 3);
//                        if (wys_input.getText().isBlank() == false
//                                && szer_input.getText().isBlank() == false
//                                && Double.parseDouble(wys) > 0
//                                && Double.parseDouble(szer) > 0) {
//                            jButton2.setEnabled(true);
//                        } else {
//                            jButton2.setEnabled(false);
//                        }
//                    } catch (NumberFormatException e2) {
//                        jButton2.setEnabled(false);
//                    }
//                } else {
//                    jButton2.setEnabled(false);
//                }
//            }
//        });
        szer_input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                FormatujDane(szer_input, false);

                if (szer_input.getText().isBlank() == false) {

                    try {
                        String wys = wys_input.getText().trim().substring(0, wys_input.getText().trim().length() - 3);;
                        String szer = szer_input.getText().trim().substring(0, szer_input.getText().trim().length() - 3);
                        if (wys_input.getText().isBlank() == false
                                && szer_input.getText().isBlank() == false
                                && Double.parseDouble(wys) > 0
                                && Double.parseDouble(szer) > 0) {
                            jButton2.setEnabled(true);
                        } else {
                            jButton2.setEnabled(false);
                        }
                    } catch (NumberFormatException e2) {
                        jButton2.setEnabled(false);
                    }
                } else {
                    jButton2.setEnabled(false);
                }
            }
        });

        miasto_input.addActionListener(e -> jButton1.doClick());
        wys_input.addActionListener(e -> jButton2.doClick());
        szer_input.addActionListener(e -> jButton2.doClick());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonPobierz;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelPobOSM;
    private javax.swing.JLabel jLabelPobOSM1;
    private javax.swing.JLabel jLabelPobTF;
    private javax.swing.JLabel jLabelPobTF1;
    private javax.swing.JPanel jPanel1;
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
    private javax.swing.JLabel miasto_info;
    private javax.swing.JTextField miasto_input;
    private javax.swing.JTextField szer_input;
    private javax.swing.JTextField wys_input;
    // End of variables declaration//GEN-END:variables
}
