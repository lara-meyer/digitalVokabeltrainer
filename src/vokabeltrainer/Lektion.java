/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vokabeltrainer;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vokabeltrainer.src.gui.GUI;

/**
 *
 * @author Lena
 */
public class Lektion { //Problem: Lektionen verschiedener Sprachen dürfen nicht gleich heißen, evtl. lösen über Unterordner?

    private final String lName;
    private boolean vollGelernt;
    private Karteikarte aktKarte;
    private ArrayList<Karteikarte> vokListe = new ArrayList<>();
    private File lektFile;
    private String meinKurs;
    private GUI gui;
    private String antwort;

    //erstellt schonmal den Writer, damit in mehreren Methodenabschnitten aufrufbar unabhängig von Schleifen etc.,
    //aber übergibt noch keine Datei, weil noch keine definiert
    private BufferedWriter lektOut;
    private BufferedReader lektIn;

    public Lektion(String pMeinKurs, GUI pGui) { //Konstruktor für wenn eine Lektion neu erstellt wird
        System.out.println("Lektionsname?");
        String pName = SystemInReader.readString();
        lName = pName;
        meinKurs = pMeinKurs;
        gui = pGui;

        //erstellt im Ordner "Vokabellisten" eine csv-Datei, die nach dem Lektionsnamen benannt wird und in der die Inhalte aller Karteikarten gespeichert werden 
        //sollen, die zu dieser Lektion gehören 
        lektFile = new File("Vokabellisten\\" + meinKurs + "_" + lName + ".csv");

        try {
            //hier kein Problem mit Writer und close(), weil zu einer bestehenden Lektion nicht nachträglich etwas hinzugefügt werden soll
            lektOut = new BufferedWriter(new FileWriter(lektFile)); //übergibt dem Writer jetzt die Datei, auf die er schreiben soll
        } catch (IOException e) {
            System.out.println("Fehler beim Erstellen der Lektion.");
            //System.out.println(e.getMessage());
        }

        vokListe.add(new Karteikarte(this));
        listeSpeichern();

        try {
            lektIn = new BufferedReader(new FileReader(lektFile));
        } catch (IOException e) {
            System.out.println("Fehler beim Erstellen der Lektion.");
            //System.out.println(e.getMessage());
        }
    }

    public Lektion(String pName, boolean pVollGel, String pMeinKurs, String pFile, GUI pGui) { //Konstruktor für wenn die gespeicherten Lektionen eingelesen werden
        gui = pGui;
        lName = pName;
        vollGelernt = pVollGel;
        meinKurs = pMeinKurs;
        lektFile = new File(pFile);
        try {
            lektIn = new BufferedReader(new FileReader(lektFile));
            listeEinlesen();
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der gespeicherten Vokabeln (FileReader).");
            //System.out.println(e.getMessage());
        }

        try {
            lektOut = new BufferedWriter(new FileWriter(lektFile));
            listeSpeichern();
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der gespeicherten Vokabeln (FileWriter).");
            //System.out.println(e.getMessage());
        }
    }

    //Neue Karteikarte wird über "vokListe.add(new Karteikarte())" zur vokListe hinzugefügt, beliebig oft, dann muss am Ende "listeSpeichern()" aufgerufen
    //werden, weil immer die ganze Datei überschrieben wird. Hier werden dann die Informationen von jedem Element der vokListe in die Datei geschrieben. Bei 
    //Programmaufruf muss dann die Datei wieder in die vokListe eingelesen werden, damit alle Elemente wieder vorhanden sind und beim nächsten Speichern 
    //wieder mitgeschrieben werden.
    public void listeSpeichern() {
        try {
            lektOut = new BufferedWriter(new FileWriter(lektFile));
            for (Karteikarte krt : vokListe) {
                lektOut.write(krt.getVokA() + ";" + krt.getVokZ() + ";" + krt.getHilfssatz() + ";" + krt.getGelernt() + ";" + krt.getStatus() + ";" + krt.getFavorit() + ";");
                lektOut.newLine();
            }
            lektOut.write("endOfList");
            lektOut.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Vokabelliste.");
            //System.out.println(e.getMessage());
        }
    }

    private void listeEinlesen() {
        try {
            String zeile = lektIn.readLine();
            if (zeile != null) {
                while (!zeile.equals("endOfList")) {
                    String[] split = zeile.split(";");

                    //Parameter typecasten zu dem, was sie im Konstruktor sein müssen
                    String pVokA = split[0];
                    String pVokZ = split[1];
                    String pHS = split[2];
                    boolean pGel = Boolean.parseBoolean(split[3]);
                    int pStatus = Integer.parseInt(split[4]);
                    boolean pFav = Boolean.parseBoolean(split[5]);

                    vokListe.add(new Karteikarte(pVokA, pVokZ, pHS, pGel, pStatus, pFav, this));
                    zeile = lektIn.readLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der Vokabelliste.");
            //System.out.println(e.getMessage());
        }
    }

    public void abfrage(GUI pGui, int pIndex) { //fragt je nach Wert von fZielsprGefr ab, bei true muss Nutzer Zielsprache eingeben, bei false Ausgangssprache
        aktKarte = vokListe.get(pIndex);
        pGui.aktKarte = aktKarte;
        if (aktKarte.getGelernt() == false) {//wenn die Lampe noch nicht grün ist, wird abgefragt
            if (pGui.fZielsprGefr == true) {
                pGui.setAbfrage(aktKarte.getVokA());
            } else {
                pGui.setAbfrage(aktKarte.getVokZ());
            }
            pGui.kartenPanel.updateUI();
            pGui.updateStatusPanel(aktKarte.getStatus());
        }else{//wenn Lampe schon grün, wird die nächste Karteikarte der Liste aufgerufen
            abfrage(pGui, (pIndex+1));
        }
    }

    public Karteikarte getVokAt(int pIndex) {
        return vokListe.get(pIndex);
    }

    public String getName() {
        return lName;
    }

    public File getFile() {
        return lektFile;
    }

    public void setVollGelernt() {
        vollGelernt = true;
    }

    public boolean getVollGelernt() {
        return vollGelernt;
    }

    public void setAktKarte(Karteikarte pAktuell) {
        aktKarte = pAktuell;
    }

    public Karteikarte getAktKarte() {
        return aktKarte;
    }

    public void setMeinKurs(String pMeinKurs) {
        meinKurs = pMeinKurs;
    }

    public String getMeinKurs() {
        return meinKurs;
    }

    public void setAntwort(String pAntwort) {
        antwort = pAntwort;
    }

    public int getAnzahlVok() {
        int anzahl = vokListe.size();
        return anzahl;
    }
    
    public int getAnzahlGel(){ //geht vokListe durch und zählt dabei, bei wievielen der Karteikarten das Attribut gelernt true ist
        int anzahlGel=0;
        for (Karteikarte karte : vokListe) {
            if(karte.getGelernt()==true){
                anzahlGel++;
            }
        }
        return anzahlGel;
    }

}
