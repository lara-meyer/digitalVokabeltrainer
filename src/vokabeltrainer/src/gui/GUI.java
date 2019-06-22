/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vokabeltrainer.src.gui; //muss immer korrekten Namen mit den Verzeichnissen durch Punkte getrennt erhalten

//Import-Anweisungen
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
//import javax.swing.JDialog; wäre einfaches Fenster ohne Menü
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.EventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import vokabeltrainer.Karteikarte;
import vokabeltrainer.Kurs;
import vokabeltrainer.Lektion;

/**
 *
 * @author Lara Meyer
 */
public class GUI extends JFrame {

    public JPanel kartenPanel;
    public JPanel menuPanel;
    public JPanel statusPanel;
    public JPanel southPanel;
    public JPanel richtungPanel;
    public JLabel vokAbfrage;
    public JLabel anzeigeLoesung; //hier soll korrekte Übersetzung angezeigt werden, wenn auf Kreuz geklickt wird
    public JLabel anzeigeHS; //hier soll Hilfssatz angezeigt werden, wenn auf Spreechblase geklickt wird
    public JTextArea eingabefeld;
    public JButton kreuz;
    public JButton tick;
    public JButton hilfssatz;
    public JLabel statuslabel;
    public Karteikarte aktKarte;
    public String antwort;
    public Lektion aktLektion;
    public int abfrageIndex;
    public boolean fZielsprGefr = true; //fZielsprGefr == true bedeutet, dass der Nutzer die Vokabelbedeutung in der Zielsprache eingeben muss
    public JLabel scorelabel;
    public JButton weiter;

    //Konstruktor erstellen
    public GUI() {
        //super Konstruktor mit Fenstertitel aufrufen
        super("Digitaler Vokabeltrainer");
        //Größe und weitere Details zum JFrame angeben 
        //setSize(1000, 500);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout()); //Layout muss festgelegt werden
        add(menuPanel = createMenuPanel(), BorderLayout.WEST); //sorgt dafür, dass das Panel auch dem Frame zugefügt wird
        add(kartenPanel = createKartenPanel(), BorderLayout.CENTER);
        add(statusPanel = createStatusPanel(), BorderLayout.NORTH);
        add(southPanel = createSouthPanel(this), BorderLayout.SOUTH);
        add(richtungPanel = createRichtungPanel(), BorderLayout.EAST);
        //pack(); //würde Größe an Inhalt anpassen. Habe ich aber zunächst händisch eingestellt
        setLocationRelativeTo(null); //das Fenster in die Mitte vom Bildschirm setzen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);//JFrame sichtbar machen

    }

    //muss für Konstruktor erstmal leere Buttons erstellen, weil das sonst mit der Reihenfolge der Übergabewerte beim Aufrufen in main nicht passt, weil die
    //Lektionen zu dem Zeitpunkt noch nicht eingelesen sein können
    private JPanel createMenuPanel() {
        JPanel menupanel = new JPanel();
        //Layout festlegen
        menupanel.setLayout(new BoxLayout(menupanel, BoxLayout.Y_AXIS));
        //Farbe festlegen
        menupanel.setBackground(new java.awt.Color(188, 143, 143));
        return menupanel;
    }

    public JPanel updateMenuPanel(GUI pGui, ArrayList<Lektion> pAlleLektionen) {
        //Buttons erstellen
        for (Lektion lektion : pAlleLektionen) {
            JButton button = new JButton(lektion.getMeinKurs() + " - " + lektion.getName());
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    aktLektion = lektion;
                    abfrageIndex = 0;
                    updateScore();
                    aktLektion.abfrage(pGui, abfrageIndex);
                    eingabefeld.setText("");
                    if (anzeigeLoesung.getText() != null) {
                        anzeigeLoesung.setText("");
                    }
                    kartenPanel.updateUI();
                }
            });
            button.setFont(new Font("Dialog", 0, 20));
            menuPanel.add(button);
        }

        return menuPanel;
    }

    private JPanel createKartenPanel() {
        kartenPanel = new JPanel();
        kartenPanel.setLayout(null);
        vokAbfrage = new JLabel("Vokabel Ausgangssprache"); //hier Verbindung zu den Vokabeln, die angezeigt werden sollen
        vokAbfrage.setFont(new Font("Dialog", 0, 30));
        kartenPanel.add(vokAbfrage);
        vokAbfrage.setOpaque(true);
        vokAbfrage.setSize(400, 50);
        vokAbfrage.setLocation(380, 30);

        anzeigeLoesung = new JLabel();
        anzeigeLoesung.setFont(new Font("Dialog", 0, 30));
        kartenPanel.add(anzeigeLoesung);
        anzeigeLoesung.setOpaque(true);
        anzeigeLoesung.setSize(400, 50);
        anzeigeLoesung.setLocation(380, 100);

        anzeigeHS = new JLabel();
        anzeigeHS.setFont(new Font("Dialog", 0, 30));
        kartenPanel.add(anzeigeHS);
        anzeigeHS.setOpaque(true);
        anzeigeHS.setSize(800, 100);
        anzeigeHS.setLocation(50, 500);

        // Legt eine weiße einfache Linie als Border um das JPanel
        kartenPanel.setBorder(BorderFactory.createLineBorder(Color.white));
        return kartenPanel;
    }

    public void setAbfrage(String pAbfrage) {
        vokAbfrage.setText(pAbfrage);
        kartenPanel.updateUI();
    }

    public void showLoesung(String pLoesung) {
        anzeigeLoesung.setText(pLoesung);
        kartenPanel.updateUI();
    }

    public void showHS(String pHS) {
        anzeigeHS.setText("<html>" + pHS + "</html>");
        kartenPanel.updateUI();
    }

    private JPanel createRichtungPanel() {
        JPanel richtungpanel = new JPanel();
        richtungpanel.setLayout(new BoxLayout(richtungpanel, BoxLayout.Y_AXIS));
        ButtonGroup richtungBG = new ButtonGroup();

        JRadioButton bZielsprGefr = new JRadioButton("Zielsprache gefragt");
        bZielsprGefr.setFont(new Font("Dialog", 0, 20));
        bZielsprGefr.setSelected(true); //damit am Anfang auch ausgewählt, da fZielsprGefr bei Programmstart auf true steht, dementsprechend auch die Anzeige
        bZielsprGefr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fZielsprGefr = true;
                if (aktKarte != null) {
                    setAbfrage(aktKarte.getVokA());
                    if (anzeigeLoesung.getText() != null) {
                        anzeigeLoesung.setText("");
                    }
                    kartenPanel.updateUI();
                }
            }
        });
        richtungBG.add(bZielsprGefr);
        richtungpanel.add(bZielsprGefr);

        JRadioButton bAusgsprGefr = new JRadioButton("Ausgangssprache gefragt");
        bAusgsprGefr.setFont(new Font("Dialog", 0, 20));
        bAusgsprGefr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fZielsprGefr = false;
                if (aktKarte != null) {
                    setAbfrage(aktKarte.getVokZ());
                    if (anzeigeLoesung.getText() != null) {//wenn noch Übersetzung vom Kreuz angezeigt, soll die weggenommen werden
                        anzeigeLoesung.setText("");
                    }
                    kartenPanel.updateUI();
                }
            }
        });
        richtungBG.add(bAusgsprGefr);
        richtungpanel.add(bAusgsprGefr);

        JPanel scorepanel = new JPanel();
        scorelabel = new JLabel("Score: 0/0 gelernt - noch: 0"); //hier entsprechend Verbindung zu dem errechneten Score aus Code. Ggf. für Zahlen halt Variablne einsetzen, sodass es veränderkich wird?
        scorelabel.setFont(new Font("Dialog", 0, 20));
        scorepanel.add(scorelabel);
        richtungpanel.add(scorepanel);

        return richtungpanel;
    }

    private JPanel createSouthPanel(GUI pGui) {
        southPanel = new JPanel();

        JPanel eingabepanel = new JPanel();
        //1-zeiliges und 50-spaltiges Textfeld wird         
        eingabefeld = new JTextArea(3, 20);
        //Text für das Textfeld wird gesetzt
        eingabefeld.setText("Übersetzung eingeben..."); //hier den Bezug zu den Vokabeln herstellen, die angezeigt werden sollen
        eingabefeld.setFont(new Font("Dialog", 0, 20));
        //Zeilenumbruch wird eingeschaltet
        eingabefeld.setLineWrap(true);
        //Zeilenumbrüche nur nach ganzen Wörtern
        eingabefeld.setWrapStyleWord(true);
        eingabepanel.add(eingabefeld);
        southPanel.add(eingabepanel);

        JPanel checkpanel = new JPanel();
        tick = new JButton();
        ImageIcon iTick = new ImageIcon(new ImageIcon("./Tick.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        tick.setIcon(iTick);
        tick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                antwort = eingabefeld.getText();
                useTick();
                eingabefeld.setText("");
                if (anzeigeHS.getText() != null) {
                    anzeigeHS.setText("");
                }
                abfrageIndex++;
                if (abfrageIndex < aktLektion.getAnzahlVok()) {//ruft Abfrage für nächste Vokabel in der Liste auf
                    aktLektion.abfrage(pGui, abfrageIndex);
                } else {//fängt nach letzter Vokabel wieder bei erster an
                    abfrageIndex = 0;
                    aktLektion.abfrage(pGui, abfrageIndex);
                }
            }
        });
        checkpanel.add(tick);

        kreuz = new JButton();
        ImageIcon iKreuz = new ImageIcon(new ImageIcon("./Kreuz.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        kreuz.setIcon(iKreuz);
        kreuz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aktKarte != null) {
                    if (fZielsprGefr == true) {
                        showLoesung(aktKarte.getVokZ());
                    } else {
                        showLoesung(aktKarte.getVokA());
                    }
                    weiter = new JButton("weiter");
                    weiter.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            aktKarte.setStatus(0);
                            eingabefeld.setText("");
                            anzeigeLoesung.setText("");
                            if (anzeigeHS.getText() != null) {
                                anzeigeHS.setText("");
                            }
                            abfrageIndex++;
                            if (abfrageIndex < aktLektion.getAnzahlVok()) {//ruft Abfrage für nächste Vokabel in der Liste auf
                                aktLektion.abfrage(pGui, abfrageIndex);
                            } else {//fängt nach letzter Vokabel wieder bei erster an
                                abfrageIndex = 0;
                                aktLektion.abfrage(pGui, abfrageIndex);
                            }
                            southPanel.remove(weiter);
                            southPanel.updateUI();
                        }
                    });
                    southPanel.add(weiter);
                }
            }
        });
        checkpanel.add(kreuz);

        hilfssatz = new JButton();
        ImageIcon iHilfssatz = new ImageIcon(new ImageIcon("./Message.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        hilfssatz.setIcon(iHilfssatz);
        hilfssatz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHS(aktKarte.getHilfssatz());
            }
        });
        checkpanel.add(hilfssatz);

        southPanel.add(checkpanel);
        return southPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statuspanel = new JPanel();
        //an diese Labels die Bedingung anknüpfen, dass sie nur angezeigt werden, wenn der entsprechende Integer dafür besteht.

        statuslabel = new JLabel();
        ImageIcon statusausgeschaltet = new ImageIcon(new ImageIcon("./Lampe_ausgeschaltet.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        statuslabel.setIcon(statusausgeschaltet);

        statuspanel.add(statuslabel);
        return statuspanel;
    }

    public void useTick() {//hier findet die eigentliche Überprüfung der Eingabe statt
        if (fZielsprGefr == true) {//gucken, in welchem Modus gerade abgefragt wird; hier muss Zielsprache eingegeben werden
            if (antwort.equals(aktKarte.getVokZ())) {//prüfen, ob Eingabe mit gespeicherter Übersetzung übereinstimmt
                //erhöht Status der aktuell abgefragten Karteikarte um 1; da aufgrund der Überprufung in abfrage() eine Karte mit grüner Lampe gar nicht
                //angezeigt wird, muss dieser Fall hier nicht mehr berücksichtigt werden
                int statZ = aktKarte.getStatus() + 1;
                aktKarte.setStatus(statZ);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        updateStatusPanel(aktKarte.getStatus());
                    }
                }, 10000);                
                updateScore();
            } else {//wenn falsche Antwort gegeben, wird Lampe auf rot gesetzt
                aktKarte.setStatus(0);
                updateStatusPanel(aktKarte.getStatus());
                updateScore();
            }
        } else {//anderer Abfragemodus, hier muss Ausgangssprache eingegeben werden
            if (antwort.equals(aktKarte.getVokA())) {//prüfen, ob Eingabe mit gespeicherter Übersetzung übereinstimmt
                //s. oben
                System.out.println(aktKarte.getVokA() + " - richtig");
                int statA = aktKarte.getStatus() + 1;
                aktKarte.setStatus(statA);
                updateStatusPanel(aktKarte.getStatus());
                updateScore();
            } else {//wenn falsche Antwort gegeben, wird Lampe auf rot gesetzt
                System.out.println(antwort);
                System.out.println(aktKarte.getVokA() + " - falsch");
                aktKarte.setStatus(0);
                updateStatusPanel(aktKarte.getStatus());
                updateScore();
            }
        }
    }

    public void updateStatusPanel(int pStatus) {
        switch (pStatus) {
            case 0:
                ImageIcon statusRot = new ImageIcon(new ImageIcon("./Lampe_rot.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
                statuslabel.setIcon(statusRot);
                statusPanel.updateUI();
                break;

            case 1:
                ImageIcon statusOrange = new ImageIcon(new ImageIcon("./Lampe_orange.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
                statuslabel.setIcon(statusOrange);
                statusPanel.updateUI();
                break;

            case 2:
                ImageIcon statusGelb = new ImageIcon(new ImageIcon("./Lampe_gelb.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
                statuslabel.setIcon(statusGelb);
                statusPanel.updateUI();
                break;

            case 3:
                ImageIcon statusGruen = new ImageIcon(new ImageIcon("./Lampe_gruen.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
                statuslabel.setIcon(statusGruen);
                statusPanel.updateUI();
                break;
        }
        statusPanel.updateUI();
    }

    public void updateScore() {
        int fullScore = aktLektion.getAnzahlVok();
        int anzGelernt = aktLektion.getAnzahlGel();
        int anzRest = fullScore - anzGelernt;
        scorelabel.setText("Score: " + anzGelernt + "/" + fullScore + " - noch:" + anzRest);
        richtungPanel.updateUI();
    }

}
