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
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import vokabeltrainer.Karteikarte;
import vokabeltrainer.Kurs;
import vokabeltrainer.Lektion;
import vokabeltrainer.SystemInReader;
import static vokabeltrainer.Vokabeltrainer.alleLektionen;
import static vokabeltrainer.Vokabeltrainer.kursListe;

/**
 *
 * @author Lara Meyer
 */
public class GUI extends JFrame {

    public JPanel kartenPanel;
    public JPanel menuPanel;
    private JPanel statusPanel;
    private JPanel southPanel;
    private JPanel richtungPanel;
    public JLabel vokAbfrage;
    private Lektion[] alleLektionen;
//    private JButton button0;
//    private JButton button1;
//    private JButton button2;
//    private JButton button3;
//    private JButton button4;
    private JTextArea eingabefeld;
    private JButton kreuz;
    private JButton tick;
    private JButton hilfssatz;
    private JLabel statuslabel;
    public Karteikarte aktKarte;
    private String antwort;
    private Lektion aktLektion;
    private int abfrageIndex;

    //Konstruktor erstellen
     public GUI(Lektion[] alleLektionen) {
        //super Konstruktor mit Fenstertitel aufrufen
        super("Digitaler Vokabeltrainer");
        this.alleLektionen = alleLektionen;

        //Größe und weitere Details zum JFrame angeben 
        setSize(1000, 500);
        setLayout(new BorderLayout()); //Layout muss festgelegt werden
        add(menuPanel = createMenuPanel(), BorderLayout.WEST); //sorgt dafür, dass das Panel auch dem Frame zugefügt wird
        updateMenuPanel();
        add(kartenPanel = createKartenPanel(), BorderLayout.CENTER);
        add(statusPanel = createStatusPanel(), BorderLayout.NORTH);
        add(southPanel = createSouthPanel(this), BorderLayout.SOUTH);
        add(richtungPanel = createRichtungPanel(), BorderLayout.EAST);
        //pack(); //würde Größe an Inhalt anpassen. Habe ich aber zunächst händisch eingestellt
        setLocationRelativeTo(null); //das Fenster in die Mitte vom Bildschirm setzen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);//JFrame sichtbar machen

    }

    //muss für Konstruktor erstmal leere Buttons erstellen, weil das sost mit der Reihenfolge der Übergabewerte beim Aufrufen in main nicht passt
    private JPanel createMenuPanel() {
        JPanel menupanel = new JPanel();
//        //Layout festlegen
        menupanel.setLayout(new BoxLayout(menupanel, BoxLayout.Y_AXIS));
//        //Farbe festlegen
        menupanel.setBackground(new java.awt.Color(0, 145, 153));

//        button0 = new JButton();
//        button0.setFont(new Font("Dialog", 0, 20));
//
//        button1 = new JButton();
//        button1.setFont(new Font("Dialog", 0, 20));
//
//        button2 = new JButton();
//        button2.setFont(new Font("Dialog", 0, 20));
//
//        button3 = new JButton();
//        button3.setFont(new Font("Dialog", 0, 20));
//
//        button4 = new JButton();
//        button4.setFont(new Font("Dialog", 0, 20));

//        //Die Buttons dem Panel hinzufügen
//        menupanel.add(button0);
//        menupanel.add(button1);
//        menupanel.add(button2);
//        menupanel.add(button3);
//        menupanel.add(button4);
        updateMenuPanel();
        return menupanel;
    }

    //hier werden die leeren Buttons dann entfernt und durch die neuen mit richitger Aufschrift ersetzt
    public JPanel updateMenuPanel() {

        //Buttons erstellen
        
        menuPanel.removeAll();
        
        for (int i = 0; i < alleLektionen.length; ++i) {
    // die Variable muss hier final sein, damit sie im ActionListener verwendet werden kann
    final Lektion lekt = alleLektionen[i];
    JButton but = new JButton(lekt.getMeinKurs() + " - " + lekt.getName());
    but.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // if-Abfrage wird nicht benötigt, denn wenn es die Lektion nicht mehr gibt, sollte auch der Button weg sein!
            aktLektion = lekt;
            abfrageIndex = 0;
            lekt.abfrageZ(GUI.this, 0);
            eingabefeld.setText("");
        }
    });
    but.setFont(new Font("Dialog", 0, 20));
    menuPanel.add(but);
}

//        //hier jeweils im ActionListener die Lektion zuweisen, die in lekAuflisten() an entsprechender Stelle steht
//        Lektion lektion0 = alleLektionen[0];
//        button0 = new JButton(lektion0.getMeinKurs() + " - " + lektion0.getName());
//        button0.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (alleLektionen[0] != null) {
//                    aktLektion = alleLektionen[0];
//                    abfrageIndex = 0;
//                    alleLektionen[0].abfrageZ(pGui, 0);
//                    eingabefeld.setText("");
//                }
//            }
//        });
//        button0.setFont(new Font("Dialog", 0, 20));
//
//        button1 = new JButton(alleLektionen[1].getMeinKurs() + " - " + alleLektionen[1].getName());
//        button1.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (alleLektionen[1] != null) {
//                    aktLektion = alleLektionen[1];
//                    abfrageIndex = 0;
//                    alleLektionen[1].abfrageZ(pGui, 0);
//                    eingabefeld.setText("");
//                }
//            }
//        });
//        button1.setFont(new Font("Dialog", 0, 20));
//
//        button2 = new JButton(alleLektionen[2].getMeinKurs() + " - " + alleLektionen[2].getName());
//        button2.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (alleLektionen[2] != null) {
//                    aktLektion = alleLektionen[2];
//                    abfrageIndex = 0;
//                    alleLektionen[2].abfrageZ(pGui, 0);
//                    eingabefeld.setText("");
//                }
//            }
//        });
//        button2.setFont(new Font("Dialog", 0, 20));
//
//        button3 = new JButton(alleLektionen[3].getMeinKurs() + " - " + alleLektionen[3].getName());
//        button3.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (alleLektionen[3] != null) {
//                    aktLektion = alleLektionen[3];
//                    abfrageIndex = 0;
//                    alleLektionen[3].abfrageZ(pGui, 0);
//                    eingabefeld.setText("");
//                }
//            }
//        });
//        button3.setFont(new Font("Dialog", 0, 20));
//
//        button4 = new JButton(alleLektionen[4].getMeinKurs() + " - " + alleLektionen[4].getName());
//        button4.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (alleLektionen[4] != null) {
//                    aktLektion = alleLektionen[4];
//                    abfrageIndex = 0;
//                    alleLektionen[4].abfrageZ(pGui, 0);
//                    eingabefeld.setText("");
//                }
//            }
//        });
//        button4.setFont(new Font("Dialog", 0, 20));
//
//        //Die Buttons dem Panel hinzufügen
//        menuPanel.add(button0);
//        menuPanel.add(button1);
//        menuPanel.add(button2);
//        menuPanel.add(button3);
//        menuPanel.add(button4);
        return menuPanel;
    }

    private JPanel createKartenPanel() {
        kartenPanel = new JPanel();
        vokAbfrage = new JLabel("Vokabel Ausgangssprache"); //hier Verbindung zu den Vokabeln, die angezeigt werden sollen
        vokAbfrage.setFont(new Font("Dialog", 0, 20));
        kartenPanel.add(vokAbfrage);
        // Legt eine weiße einfache Linie als Border um das JPanel
        kartenPanel.setBorder(BorderFactory.createLineBorder(Color.white));
        return kartenPanel;
    }

    public JLabel setAbfrage(String pAbfrage) {
        kartenPanel.remove(vokAbfrage);
        vokAbfrage = new JLabel(pAbfrage);
        vokAbfrage.setFont(new Font("Dialog", 0, 20));
        kartenPanel.add(vokAbfrage);
        // Legt eine weiße einfache Linie als Border um das JPanel
        kartenPanel.setBorder(BorderFactory.createLineBorder(Color.white));
        return vokAbfrage;
    }

    private JPanel createRichtungPanel() {
        JPanel richtungpanel = new JPanel();
        richtungpanel.setLayout(new BoxLayout(richtungpanel, BoxLayout.Y_AXIS));
        ButtonGroup richtungBG = new ButtonGroup();
        JRadioButton zielsprgefr = new JRadioButton("Zielsprache gefragt");
        zielsprgefr.setFont(new Font("Dialog", 0, 20));
        richtungBG.add(zielsprgefr);
        richtungpanel.add(zielsprgefr);
        JRadioButton ausgsprgefr = new JRadioButton("Ausgangssprache gefragt");
        ausgsprgefr.setFont(new Font("Dialog", 0, 20));
        richtungBG.add(ausgsprgefr);
        richtungpanel.add(ausgsprgefr);

        JPanel scorepanel = new JPanel();
        JLabel scorelabel = new JLabel("Score: 13/20 gelernt; noch: 7"); //hier entsprechend Verbindung zu dem errechneten Score aus Code. Ggf. für Zahlen halt Variablne einsetzen, sodass es veränderkich wird?
        scorelabel.setFont(new Font("Dialog", 0, 20));
        scorepanel.add(scorelabel);
        richtungpanel.add(scorepanel);

        return richtungpanel;
    }

    private JPanel createSouthPanel(GUI pGui) {
        JPanel southpanel = new JPanel();

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
        southpanel.add(eingabepanel);

        JPanel checkpanel = new JPanel();
        tick = new JButton();
        ImageIcon itick = new ImageIcon(new ImageIcon("./Tick.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        tick.setIcon(itick);
        tick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                antwort = eingabefeld.getText();
                useTick();
                eingabefeld.setText("");
                abfrageIndex++;
                if (abfrageIndex < aktLektion.getAnzahlLek()) {
                    aktLektion.abfrageZ(pGui, abfrageIndex);
                }else{//fängt nach letzter Vokabel wieder bei erster an
                    abfrageIndex = 0;
                    aktLektion.abfrageZ(pGui, abfrageIndex);
                }
            }
        });
        checkpanel.add(tick);

        kreuz = new JButton();
        ImageIcon ikreuz = new ImageIcon(new ImageIcon("./Kreuz.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        kreuz.setIcon(ikreuz);
        checkpanel.add(kreuz);

        hilfssatz = new JButton();
        ImageIcon ihilfssatz = new ImageIcon(new ImageIcon("./Message.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        hilfssatz.setIcon(ihilfssatz);
        checkpanel.add(hilfssatz);
        southpanel.add(checkpanel);

        return southpanel;
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

    public void setAlleLektionen(Lektion[] pArray) {
        alleLektionen = pArray;
    }

    public void useTick() {
        if (antwort.equals(aktKarte.getVokZ())) {
            if (aktKarte.getStatus() != 3) {
                int stat = aktKarte.getStatus() + 1;
                aktKarte.setStatus(stat);
                updateStatusPanel(aktKarte.getStatus());
            }
        } else {
            aktKarte.setStatus(0);
            updateStatusPanel(aktKarte.getStatus());
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
    }

}
