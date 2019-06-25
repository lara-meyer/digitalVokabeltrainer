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
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import vokabeltrainer.Karteikarte;
import vokabeltrainer.Kurs;
import vokabeltrainer.Lektion;
import vokabeltrainer.Vokabeltrainer;

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
    public JLabel anzeigeLoesung; //damit soll korrekte Übersetzung angezeigt werden, wenn auf Kreuz geklickt wird
    public JLabel anzeigeHS; //damit soll Hilfssatz angezeigt werden, wenn auf Sprechblase geklickt wird
    public JTextArea eingabefeld;
    public JButton kreuz;
    public JButton tick;
    public JButton hilfssatz;
    public JLabel statuslabel;
    public Karteikarte aktKarte;
    public String antwort;
    public Lektion aktLektion;
    public int abfrageIndex;
    //"fZielsprGefr == true" bedeutet, dass der Nutzer die Vokabelbedeutung in der Zielsprache eingeben muss; soll default bei Programmstart sein:
    public boolean fZielsprGefr = true; 
    public JLabel scorelabel;
    public String eingKurs;
    private ArrayList<Kurs> alleKurse;
    private Kurs aktKurs; //für Eingabe einer neuen Lektion

    public GUI() {
        //super Konstruktor mit Fenstertitel aufrufen
        super("Digitaler Vokabeltrainer");
        //Größe und weitere Details zum JFrame angeben 
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout()); //Layout muss festgelegt werden
        add(menuPanel = createMenuPanel(this), BorderLayout.WEST); //sorgt dafür, dass das Panel auch dem Frame zugefügt wird
        add(kartenPanel = createKartenPanel(), BorderLayout.CENTER);
        add(statusPanel = createStatusPanel(), BorderLayout.NORTH);
        add(southPanel = createSouthPanel(this), BorderLayout.SOUTH);
        add(richtungPanel = createRichtungPanel(), BorderLayout.EAST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);//JFrame sichtbar machen

    }

    //erstmal nur mit dem Button für eine neue Lektion, weil die Lektionen zu dem Zeitpunkt noch nicht eingelesen sein können, aber die GUI zum Einlesen schon 
    //benötigt wird, Lektions-Buttons werden dann nach dem Einlesen nachträglich hinzugefügt
    private JPanel createMenuPanel(GUI pGui) {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new java.awt.Color(193, 255, 193));

        JButton neueLektion = new JButton("neue Lektion");
        neueLektion.setFont(new Font("Dialog", 0, 20));
        neueLektion.setForeground(Color.red);
        neueLektion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kartenPanel.removeAll();
                eingabeKurs(pGui);
            }
        });
        menuPanel.add(neueLektion);
        return menuPanel;
    }

    //bei Eingabe einer neuen Lektion wird zunächst gefragt, zu welchem Kurs die Lektion gehören und wie sie heißen soll
    public void eingabeKurs(GUI pGui) {
        //Überschrift zum Textfeld, damit der Nutzer weiß, was er eingeben soll
        JLabel labEingKurs = new JLabel("Zu welchem Kurs gehört die Lektion?");
        labEingKurs.setFont(new Font("Dialog", 0, 20));
        labEingKurs.setBackground(new java.awt.Color(193, 255, 193));
        labEingKurs.setOpaque(true);
        labEingKurs.setSize(400, 50);
        labEingKurs.setLocation(200, 210);
        kartenPanel.add(labEingKurs);

        //Textfeld, in das eingegeben werden soll
        JTextArea eingabeKurs = new JTextArea(1, 20);
        eingabeKurs.setFont(new Font("Dialog", 0, 20));
        eingabeKurs.setLineWrap(true);
        eingabeKurs.setWrapStyleWord(true);
        eingabeKurs.setOpaque(true);
        eingabeKurs.setSize(400, 50);
        eingabeKurs.setLocation(200, 250);
        kartenPanel.add(eingabeKurs);

        //s. oben
        JLabel labNeueLektion = new JLabel("Wie heißt die Lektion?");
        labNeueLektion.setFont(new Font("Dialog", 0, 20));
        labNeueLektion.setForeground(Color.gray);
        labNeueLektion.setOpaque(true);
        labNeueLektion.setSize(400, 50);
        labNeueLektion.setLocation(200, 310);
        kartenPanel.add(labNeueLektion);

        //s. oben
        JTextArea neueLektion = new JTextArea(1, 20);
        neueLektion.setFont(new Font("Dialog", 0, 20));
        neueLektion.setLineWrap(true);
        neueLektion.setWrapStyleWord(true);
        neueLektion.setOpaque(true);
        neueLektion.setSize(400, 50);
        neueLektion.setLocation(200, 350);
        kartenPanel.add(neueLektion);

        //speichert/verarbeitet Eingaben und fordert dann zur Eingabe der Vokabeln auf
        JButton kursBestätigen = new JButton("bestätigen");
        kursBestätigen.setFont(new Font("Dialog", 0, 15));
        kursBestätigen.setOpaque(true);
        kursBestätigen.setSize(150, 50);
        kursBestätigen.setLocation(700, 370);
        kursBestätigen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eingKurs = eingabeKurs.getText();
                String neueLektName = neueLektion.getText();
                boolean vorhanden = false;
                for (Kurs kurs : alleKurse) { //wenn schon ein Kurs mit dem eingegebenen Namen existiert, soll die Lektion zu diesem Kurs hinzugefügt werden
                    if (kurs.getName().equals(eingKurs)) {
                        aktKurs = kurs; //damit auch außerhalb dieser Methode darauf zugegriffen werden kann
                        aktKurs.addLektion(neueLektName); //beinhaltet Speichern der Liste
                        vorhanden = true;
                    }
                }
                //wenn hier "vorhanden" immer noch auf false steht, existiert noch kein Kurs mit dem eingegebenen Namen, also wird einer erstellt
                if (vorhanden == false) {
                    Kurs neuerKurs = new Kurs(eingKurs, pGui, neueLektName);
                    alleKurse.add(neuerKurs);
                    Vokabeltrainer.listeSpeichern();
                    aktKurs = neuerKurs;
                }
                kartenPanel.removeAll();
                eingabeLektion();
                eingKurs = null;
            }
        });
        kartenPanel.add(kursBestätigen);
        
        kartenPanel.updateUI();
    }

    public void eingabeLektion() {//hier sollen jetzt die Vokabeln der neuen Lektion eingegeben werden

        //das, was hier eingegeben wird, wird als Zielsprache gespeichert
        JLabel labEingZielspr = new JLabel("Vokabelbedeutung in der Zielsprache?");
        labEingZielspr.setFont(new Font("Dialog", 0, 20));
        labEingZielspr.setForeground(Color.gray);
        labEingZielspr.setOpaque(true);
        labEingZielspr.setSize(400, 50);
        labEingZielspr.setLocation(200, 100);
        kartenPanel.add(labEingZielspr);

        JTextArea eingabeZielspr = new JTextArea(1, 20);
        eingabeZielspr.setFont(new Font("Dialog", 0, 20));
        eingabeZielspr.setLineWrap(true);
        eingabeZielspr.setWrapStyleWord(true);
        eingabeZielspr.setOpaque(true);
        eingabeZielspr.setSize(400, 50);
        eingabeZielspr.setLocation(200, 140);
        kartenPanel.add(eingabeZielspr);

        //das, was hier eingegeben wird, wird als Ausgangssprache gespeichert
        JLabel labEingAusgspr = new JLabel("Vokabelbedeutung in der Ausgangssprache?");
        labEingAusgspr.setFont(new Font("Dialog", 0, 20));
        labEingAusgspr.setForeground(Color.gray);
        labEingAusgspr.setOpaque(true);
        labEingAusgspr.setSize(400, 50);
        labEingAusgspr.setLocation(200, 200);
        kartenPanel.add(labEingAusgspr);

        JTextArea eingabeAusgspr = new JTextArea(1, 20);
        eingabeAusgspr.setFont(new Font("Dialog", 0, 20));
        eingabeAusgspr.setLineWrap(true);
        eingabeAusgspr.setWrapStyleWord(true);
        eingabeAusgspr.setOpaque(true);
        eingabeAusgspr.setSize(400, 50);
        eingabeAusgspr.setLocation(200, 240);
        kartenPanel.add(eingabeAusgspr);
        
        //das, was hier eingegeben wird, wird als Hilfssatz gespeichert
        JLabel labEingHS = new JLabel("Hilfssatz?");
        labEingHS.setFont(new Font("Dialog", 0, 20));
        labEingHS.setForeground(Color.gray);
        labEingHS.setOpaque(true);
        labEingHS.setSize(400, 50);
        labEingHS.setLocation(200, 300);
        kartenPanel.add(labEingHS);

        JTextArea eingabeHS = new JTextArea("Es wurde kein Hilfssatz eingegeben.", 3, 20);
        eingabeHS.setFont(new Font("Dialog", 0, 20));
        eingabeHS.setLineWrap(true);
        eingabeHS.setWrapStyleWord(true);
        eingabeHS.setOpaque(true);
        eingabeHS.setSize(400, 100);
        eingabeHS.setLocation(200, 340);
        kartenPanel.add(eingabeHS);

        //wenn man noch eine Vokabel hinzufügen möchte
        JButton nextVok = new JButton("nächste Vokabel");
        nextVok.setFont(new Font("Dialog", 0, 15));
        nextVok.setOpaque(true);
        nextVok.setSize(150, 50);
        nextVok.setLocation(700, 300);
        nextVok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String neueVokA = eingabeAusgspr.getText();
                String neueVokZ = eingabeZielspr.getText();
                String neueVokHS = eingabeHS.getText();
                aktLektion.addVokabel(neueVokA, neueVokZ, neueVokHS);
                eingabeAusgspr.setText("");
                eingabeZielspr.setText("");
                eingabeHS.setText("Es wurde kein Hilfssatz eingegeben.");
            }
        });
        kartenPanel.add(nextVok);

        //wenn man alle Vokabeln eingegeben hat und die Lektion speichern will
        JButton lektSpeichern = new JButton("Lektion speichern");
        lektSpeichern.setFont(new Font("Dialog", 0, 15));
        lektSpeichern.setOpaque(true);
        lektSpeichern.setSize(150, 50);
        lektSpeichern.setLocation(700, 400);
        lektSpeichern.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String neueVokA = eingabeAusgspr.getText();
                String neueVokZ = eingabeZielspr.getText();
                String neueVokHS = eingabeHS.getText();
                aktLektion.addVokabel(neueVokA, neueVokZ, neueVokHS);
                kartenPanel.removeAll();
                buildKartenPanelAbfrage();
                aktKurs = null;
                aktLektion = null;
            }
        });
        kartenPanel.add(lektSpeichern);

        kartenPanel.updateUI();
    }

    public JPanel updateMenuPanel(GUI pGui, ArrayList<Lektion> pAlleLektionen) {
        //Buttons für alle gespeicherten, eingelesenen Lektion erstellen
        for (Lektion lektion : pAlleLektionen) {
            lektButtonErstellen(lektion, pGui);
        }
        return menuPanel;
    }
    
    //damit aus dem Konstruktor für neue Lektionen heraus ein Button hinzugefügt werden kann
    public void lektButtonErstellen(Lektion pLektion, GUI pGui){
        JButton lektButton = new JButton(pLektion.getMeinKurs().getName() + " - " + pLektion.getName());
            lektButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    kartenPanel.removeAll();
                    buildKartenPanelAbfrage();
                    aktLektion = pLektion;
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
            lektButton.setFont(new Font("Dialog", 0, 20));
            menuPanel.add(lektButton);
    }

    private JPanel createKartenPanel() {
        kartenPanel = new JPanel();
        kartenPanel.setLayout(null); //damit den Komponenten vokAbfrage, anzeigeLoesung und anzeigeHS (siehe buildKartenPanelAbfrage()) Positionen zugewiesen werden können
        kartenPanel.setBackground(new java.awt.Color(193, 255, 193));
        buildKartenPanelAbfrage();

        // Legt eine weiße einfache Linie als Border um das JPanel
        kartenPanel.setBorder(BorderFactory.createLineBorder(Color.white));
        return kartenPanel;
    }

    //erstellt die Felder, die bei der Abfrage gebraucht werden, da beim Eingeben einer neuen Lektion das KartenPanel anders genutzt wird und dadurch diese
    //Methode an verschiedenen Stellen aufgerufen werden muss
    public void buildKartenPanelAbfrage() {
        //hier wird später je nach Abfragemodus, also je nach Wert von fZielsprGefr, eine der Vokabelbedeutungen angezeigt; als "Startbildschirm", bevor eine
        //Lektion ausgewählt wurde, steht hier der Hinweis, dass eine Lektion ausgewählt werden soll, das wird dann später überschrieben
        vokAbfrage = new JLabel("Bitte Lektion wählen");
        vokAbfrage.setFont(new Font("Dialog", 0, 30));
        vokAbfrage.setOpaque(true);
        vokAbfrage.setSize(400, 50);
        vokAbfrage.setLocation(380, 30);
        kartenPanel.add(vokAbfrage);

        //hier wird die Lösung angezeigt, wenn auf den Kreuz-Button geklickt wird; vorher ist die Anzeige leer
        anzeigeLoesung = new JLabel();
        anzeigeLoesung.setFont(new Font("Dialog", 0, 30));
        anzeigeLoesung.setOpaque(true);
        anzeigeLoesung.setSize(400, 50);
        anzeigeLoesung.setLocation(380, 100);
        kartenPanel.add(anzeigeLoesung);

        //hier wird der Hilfssatz angezeigt, wenn auf den Sprecblasen-Button geklickt wird
        anzeigeHS = new JLabel();
        anzeigeHS.setFont(new Font("Dialog", 0, 30));
        anzeigeHS.setOpaque(true);
        anzeigeHS.setSize(800, 100);
        anzeigeHS.setLocation(50, 500);
        kartenPanel.add(anzeigeHS);
        
        kartenPanel.updateUI();
    }

    //aktualisiert, was auf dem Label vokAbfrage steht; ob Ausgangs- oder Zielsprache entscheidet abfrage() in der Klasse Lektion, dort wird diese Methode 
    //dann mit entsprechendem Parameter aufgerufen
    public void setAbfrage(String pAbfrage) {
        vokAbfrage.setText(pAbfrage);
        kartenPanel.updateUI();
    }

    //zeigt die korrekte Antwort auf dem Panel anzeigeLoesung an; wird im Listener des Kreuz-Buttons mit entsprechendem Parameter (Ausgangs- oder Zielsprache)
    //aufgerufen
    public void showLoesung(String pLoesung) {
        anzeigeLoesung.setText(pLoesung);
        kartenPanel.updateUI();
    }

    //zeigt Hilfssatz auf Panel anzeigeHS an; wird im Listener des Sprechblasen-Buttons aufgerufen
    public void showHS(String pHS) {
        anzeigeHS.setText("<html>" + pHS + "</html>");
        kartenPanel.updateUI();
    }

    //hier kann die Abfragerichtung eingestellt werden
    private JPanel createRichtungPanel() {
        richtungPanel = new JPanel();
        richtungPanel.setLayout(new BoxLayout(richtungPanel, BoxLayout.Y_AXIS));
        richtungPanel.setBackground(new java.awt.Color(193, 255, 193));
        ButtonGroup richtungBG = new ButtonGroup(); //damit immer nur eine der beiden Optionen ausgewählt sein kann
        
        JRadioButton bZielsprGefr = new JRadioButton("Zielsprache gefragt");
        bZielsprGefr.setFont(new Font("Dialog", 0, 20));
        bZielsprGefr.setSelected(true); //damit am Anfang auch ausgewählt, da fZielsprGefr bei Programmstart auf true steht, dementsprechend auch die Anzeige
        bZielsprGefr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fZielsprGefr = true; //damit alle Methoden, die an der Abfrage beteiligt sind, wissen, dass jetzt nach der Zielsprache gefragt ist
                //damit die Anzeige auch direkt gewechselt wird und nicht erst bei der nächsten Vokabel:
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
        richtungPanel.add(bZielsprGefr);

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
        richtungPanel.add(bAusgsprGefr);

        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(new java.awt.Color(193, 255, 193));
        scorelabel = new JLabel("Score: 0/0 gelernt - noch: 0"); //sobald eine Lektion ausgwählt ist, werden hier die 0en durch die entsprechenden Zahlen ersetzt
        scorelabel.setFont(new Font("Dialog", 0, 20));
        scorePanel.add(scorelabel);
        richtungPanel.add(scorePanel);

        return richtungPanel;
    }

    //auf diesem Panel befinden sich das Eingabefeld für die Abfrage und die drei Buttons zum Überprüfen, Lösung und Hilfssatz anzeigen; außerdem wir über den
    //Listener des Kreuz-Buttons temporär der Button "weiter" hinzugefügt, um zur nächsten Vokabel zu gelangen
    private JPanel createSouthPanel(GUI pGui) {
        southPanel = new JPanel();
        southPanel.setBackground(new java.awt.Color(193, 255, 193));
        JPanel eingabepanel = new JPanel();
        eingabepanel.setBackground(new java.awt.Color(193, 255, 193));
        eingabefeld = new JTextArea(3, 20);
        eingabefeld.setText("Übersetzung eingeben...");
        eingabefeld.setFont(new Font("Dialog", 0, 20));
        eingabefeld.setLineWrap(true);//Zeilenumbrüche, falls nötig
        eingabefeld.setWrapStyleWord(true);//nur nach ganzen Wörtern
        eingabepanel.add(eingabefeld);
        southPanel.add(eingabepanel);

        JPanel checkpanel = new JPanel();//Panel für die drei Buttons Häkchen, Kreuz und Sprechblase
        checkpanel.setBackground(new java.awt.Color(193, 255, 193));
        tick = new JButton();
        ImageIcon iTick = new ImageIcon(new ImageIcon("./Tick.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        tick.setIcon(iTick);
        tick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                antwort = eingabefeld.getText(); //damit alle Methoden, die damit arbeiten müssen, wissen, was vom Nutzer als Antwort eingegeben wurde
                useTick();//eigentliche Überprüfung der Eingabe
                eingabefeld.setText("");//damit bei nächster Vokabel wieder leer
                if (anzeigeHS.getText() != null) {//wenn die Antwort mithilfe des Hilfssatzes gefunden, muss auch der Hilfssatz wieder ausgeblendet werden
                    anzeigeHS.setText("");
                }
                abfrageIndex++;
                if (abfrageIndex < aktLektion.getAnzahlVok()) {//ruft Abfrage für nächste Vokabel in der Liste auf, solange noch innerhalb der Liste
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
                if (aktKarte != null) { //zur Abischerung, falls der Button geklickt wird, bevor eine Lektion ausgewählt wurde
                    if (fZielsprGefr == true) { //zeigt je nach aktuellem Abfragemodus die entsprechende Lösung an
                        showLoesung(aktKarte.getVokZ());
                    } else {
                        showLoesung(aktKarte.getVokA());
                    }
                    JButton weiter = new JButton("weiter"); //um zur nächsten Vokabel zu gelangen, wenn man die Lösung zur Kenntnis genommen hat
                    weiter.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            aktKarte.setStatus(0); //setzt Lampe zurück auf rot, da der Nutzer keine Antwort geben konnte
                            eingabefeld.setText("");
                            anzeigeLoesung.setText(""); //entfernt angezeigte Lösung
                            if (anzeigeHS.getText() != null) { //falls trotz angezeigtem Hilfssatz das Kreuz geklickt wurde, muss auch der entfernt werden
                                anzeigeHS.setText("");
                            }
                            abfrageIndex++;
                            if (abfrageIndex < aktLektion.getAnzahlVok()) {//ruft Abfrage für nächste Vokabel auf, solange noch innerhalb der Liste
                                aktLektion.abfrage(pGui, abfrageIndex);
                            } else {//fängt nach letzter Vokabel wieder bei erster an
                                abfrageIndex = 0;
                                aktLektion.abfrage(pGui, abfrageIndex);
                            }
                            southPanel.remove(weiter); //entfernt den Button, weil er nur bei angezeigter Lösung vorhanden sein soll
                            southPanel.updateUI(); //damit alle Änderungen übernommen und agezeigt werden
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

    //dieses Panel enthält die Lampe, die signalisiert, wie oft man eine Vokabel schon richtig beantwortet hat
    private JPanel createStatusPanel() {
        JPanel statuspanel = new JPanel();
        statuspanel.setBackground(new java.awt.Color(193, 255, 193));
        //Lampe wird zuerst mit ausgeschaltetem Icon erstellt, ändert sich, sobald tatsächlich eine Vokabel abgefragt wird
        statuslabel = new JLabel();
        ImageIcon statusausgeschaltet = new ImageIcon(new ImageIcon("./Lampe_ausgeschaltet.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        statuslabel.setIcon(statusausgeschaltet);
        statuspanel.add(statuslabel);
        
        return statuspanel;
    }

    public void useTick() { //hier findet die eigentliche Überprüfung der Eingabe statt
        if (fZielsprGefr == true) { //gucken, in welchem Modus gerade abgefragt wird; hier muss Zielsprache eingegeben werden
            if (antwort.equals(aktKarte.getVokZ())) { //prüfen, ob Eingabe mit gespeicherter Übersetzung übereinstimmt
                //erhöht Status der aktuell abgefragten Karteikarte um 1; da aufgrund der Überprufung in abfrage() eine Karte mit grüner Lampe gar nicht
                //angezeigt wird, muss dieser Fall hier nicht mehr berücksichtigt werden
                int statZ = aktKarte.getStatus() + 1;
                aktKarte.setStatus(statZ);
                
                //ich hätte gerne, dass man das Umspringen der Lampe noch sieht, bevor die nächste Vokabel angezeigt wird
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        updateStatusPanel(aktKarte.getStatus());
                    }
                }, 10000);
                
                updateScore(); //falls in diesem Schritt eine Vokabel auf grün gesetzt wird, muss der Score mitzählen
            } else { //wenn falsche Antwort gegeben, wird Lampe komplett zurück auf rot gesetzt
                aktKarte.setStatus(0);
                updateStatusPanel(aktKarte.getStatus());
            }
        } else { //anderer Abfragemodus, hier muss Ausgangssprache eingegeben werden
            if (antwort.equals(aktKarte.getVokA())) { //prüfen, ob Eingabe mit gespeicherter Übersetzung übereinstimmt
                //s. oben
                int statA = aktKarte.getStatus() + 1;
                aktKarte.setStatus(statA);
                updateStatusPanel(aktKarte.getStatus());
                updateScore();
            } else { //wenn falsche Antwort gegeben, wird Lampe auf rot gesetzt
                aktKarte.setStatus(0);
                updateStatusPanel(aktKarte.getStatus());
            }
        }
    }

    //hier wird das Icon der Lampe an den Status der aktuell abgefragten Vokabel angepasst, der Status wird als Parameter übergeben
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

    //zählt zusammen, wie viele Vokabeln der ausgewählten Lektion schon auf grün (= Status 3) stehen, und fragt ab, wie viele Vokabeln überhaupt in der 
    //Lektion sind; daraus kann dann auch der "noch"-Wert errechnet werden
    public void updateScore() {
        int fullScore = aktLektion.getAnzahlVok();
        int anzGelernt = aktLektion.getAnzahlGel();
        int anzRest = fullScore - anzGelernt;
        scorelabel.setText("Score: " + anzGelernt + "/" + fullScore + " - noch:" + anzRest);
        richtungPanel.updateUI();
        
        if(anzRest==0){
            aktLektion.setVollGelernt();
            showGelerntScreen();
        }
    }

    public void setAlleKurse(ArrayList<Kurs> pKurse) {
        alleKurse = pKurse;
    }
    
    public void showGelerntScreen(){
        kartenPanel.removeAll();
        
        JLabel bildGelerntScreen = new JLabel(new ImageIcon("./Sterne.png"));
        bildGelerntScreen.setOpaque(true);
        bildGelerntScreen.setSize(500, 400);
        bildGelerntScreen.setLocation(200, 80);
        kartenPanel.add(bildGelerntScreen);
        
        JLabel schriftGelerntScreen = new JLabel("Diese Lektion hast du vollständig gelernt. Herzlichen Glückwunsch!");
        schriftGelerntScreen.setFont(new Font("Dialog", 0, 25));
        schriftGelerntScreen.setSize(800, 200);
        schriftGelerntScreen.setLocation(10, 500);
        kartenPanel.add(schriftGelerntScreen);
        
        kartenPanel.updateUI();
        updateStatusPanel(3);
    }

}
