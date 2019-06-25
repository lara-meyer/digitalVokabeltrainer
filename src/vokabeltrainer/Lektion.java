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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vokabeltrainer.src.gui.GUI;

/**
 *
 * @author Lena
 */
public class Lektion {

    private final String lName;
    private boolean vollGelernt;
    private Karteikarte aktKarte;
    private ArrayList<Karteikarte> vokListe = new ArrayList<>();
    private File lektFile;
    private Kurs meinKurs;
    private GUI gui;

    //erstellt schonmal den Writer, damit in mehreren Methodenabschnitten aufrufbar unabhängig von Schleifen etc.,
    //aber übergibt noch keine Datei, weil noch keine definiert
    private BufferedWriter lektOut;
    private BufferedReader lektIn;

    public Lektion(String pName, Kurs pMeinKurs, GUI pGui) { //Konstruktor für wenn eine Lektion neu erstellt wird
        lName = pName;
        meinKurs = pMeinKurs;
        gui = pGui;

        //direkt neuen Button erstellen und auf GUI anzeigen:
        gui.lektButtonErstellen(this, gui);
        gui.menuPanel.updateUI();

        //erstellt im Ordner "Vokabellisten" eine csv-Datei, die nach dem Lektionsnamen benannt wird und in der die Inhalte aller Karteikarten gespeichert  
        //werden, die zu dieser Lektion gehören 
        lektFile = new File("Vokabellisten\\" + meinKurs.getName() + "_" + lName + ".csv");

        try {
            //übergibt dem Writer jetzt die Datei, auf die er schreiben soll
            lektOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lektFile), "UTF-8"));
        } catch (IOException e) {
            System.out.println("Fehler beim Erstellen der Lektion.");
            //System.out.println(e.getMessage());
        }
        listeSpeichern(); //das gerade Eingelesene wird direkt wieder abgespeichert, weil die Datei leer ist, nachdem der Reader drübergelaufen ist

        try {
            lektIn = new BufferedReader(new InputStreamReader(new FileInputStream(lektFile), "UTF-8"));
        } catch (IOException e) {
            System.out.println("Fehler beim Erstellen der Lektion.");
            //System.out.println(e.getMessage());
        }
    }

    //Konstruktor für wenn die gespeicherten Lektionen eingelesen werden
    public Lektion(String pName, boolean pVollGel, Kurs pMeinKurs, String pFile, GUI pGui) {
        lName = pName;
        vollGelernt = pVollGel;
        meinKurs = pMeinKurs;
        lektFile = new File(pFile);
        gui = pGui;
        try {
            lektIn = new BufferedReader(new InputStreamReader(new FileInputStream(lektFile), "UTF-8"));
            listeEinlesen();
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der gespeicherten Vokabeln (FileReader).");
            //System.out.println(e.getMessage());
        }

        listeSpeichern(); //das gerade Eingelesene wird direkt wieder abgespeichert, weil die Datei leer ist, nachdem der Reader drübergelaufen ist
    }

    //überschreibt vorhandene Datei mit allen Elementen, die zum Zeitpunkt des Aufrufs in der vokListe sind
    public void listeSpeichern() {
        try {
            lektOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lektFile), "UTF-8"));
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
        if (!vollGelernt) {
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
            } else {//wenn Lampe schon grün, wird die nächste Karteikarte der Liste aufgerufen
                abfrage(pGui, (pIndex + 1));
            }
        } else {
            gui.showGelerntScreen();
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

    //muss nur einmal verändert, also auf true gesetzt werden, dann ist die Lektion fertig und kann/soll/muss nicht wieder zurückgesetzt werden (so ist es
    //zumindest vorgesehen)
    public void setVollGelernt() {
        vollGelernt = true;
        meinKurs.listeSpeichern();

    }

    public boolean getVollGelernt() {
        return vollGelernt;
    }

    public void setMeinKurs(Kurs pMeinKurs) {
        meinKurs = pMeinKurs;
    }

    public Kurs getMeinKurs() {
        return meinKurs;
    }

    public int getAnzahlVok() {
        int anzahl = vokListe.size();
        return anzahl;
    }

    public int getAnzahlGel() { //geht vokListe durch und zählt dabei, bei wievielen der Karteikarten das Attribut gelernt true ist
        int anzahlGel = 0;
        for (Karteikarte karte : vokListe) {
            if (karte.getGelernt() == true) {
                anzahlGel++;
            }
        }
        return anzahlGel;
    }

    public void addVokabel(String pVokA, String pVokZ, String pHS) { //damit aus der GUI heraus neue Vokabeln hinzugefügt werden können
        vokListe.add(new Karteikarte(this, pVokA, pVokZ, pHS));
        listeSpeichern();
    }

}
