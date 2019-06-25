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
import vokabeltrainer.src.gui.GUI;

/**
 *
 * @author Lena
 */
public class Kurs {

    private final String kName;
    private ArrayList<Lektion> lekListe = new ArrayList<>();
    private File kursFile;
    private GUI gui;

    //erstellt schonmal Writer und Reader, damit in mehreren Methodenabschnitten aufrufbar unabhängig von Schleifen etc.,
    //aber übergibt noch keine Datei, weil noch keine definiert
    private BufferedWriter kursOut;
    private BufferedReader kursIn;


    //Konstruktor für wenn ein Kurs neu erstellt wird, also wenn bei einer neuen Lektion ein Kursname eingegeben wird, der noch nicht existiert
    public Kurs(String pName, GUI pGui, String pLektName) {
        kName = pName;
        gui = pGui;

        //erstellt im Ordner "Kurslisten" eine csv-Datei, die nach dem Kursnamen benannt ist und in der die Namen aller Lektionen gespeichert werden,
        //die zu diesem Kurs gehören
        kursFile = new File("Lektionslisten\\" + kName + ".csv");

        try {
            kursOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(kursFile),"UTF-8"));
        } catch (IOException e) {
            System.out.println("Fehler beim Erstellen des Kurses (FileWriter).");
            //System.out.println(e.getMessage());
        }

        Lektion neueLektion = new Lektion(pLektName, this, gui);
        lekListe.add(neueLektion);
        listeSpeichern();
        gui.aktLektion = neueLektion; //damit die neu eingegebenen Vokabeln auch in der neu erstellten Lektion gespeichert werden

        try {
            kursIn = new BufferedReader(new InputStreamReader(new FileInputStream(kursFile),"UTF-8"));
        } catch (IOException e) {
            System.out.println("Fehler beim Erstellen des Kurses (FileReader).");
            //System.out.println(e.getMessage());
        }
    }

    public Kurs(String pName, String pFile, GUI pGui) { //Konstruktor für wenn die gespeicherten Kurse eingelesen werden
        gui = pGui;
        kName = pName;
        kursFile = new File(pFile);
        try {
            kursIn = new BufferedReader(new InputStreamReader(new FileInputStream(kursFile),"UTF-8"));
            listeEinlesen();
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der Lektionsliste (FileReader).");
            //System.out.println(e.getMessage());
        }

        listeSpeichern(); //das gerade Eingelesene wird direkt wieder abgespeichert, weil die Datei leer ist, nachdem der Reader drübergelaufen ist
    }

    //überschreibt vorhandene Datei mit allen Elementen, die zum Zeitpunkt des Aufrufs in der lekListe sind
    public void listeSpeichern() { //public, damit aus Lektion heraus gespeichert werden kann, wenn eine Lektion auf vollGelernt gesetzt wird
        try {
            //Writer hier, damit bei jedem Speichern ein neuer Stream geöffnet wird; gibt sonst Probleme mit dem close()
            kursOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(kursFile),"UTF-8"));
            for (Lektion lek : lekListe) {
                kursOut.write(lek.getName() + ";" + lek.getVollGelernt() + ";" + "Vokabellisten\\" + lek.getMeinKurs().getName() + "_" + lek.getName() + ".csv" + ";");
                kursOut.newLine();
            }
            kursOut.write("endOfList");
            kursOut.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Lektionsliste.");
            //System.out.println(e.getMessage());
        }
    }

    private void listeEinlesen() {
        try {
            String zeile = kursIn.readLine();
            if (zeile != null) {
                while (!zeile.equals("endOfList")) {
                    String[] split = zeile.split(";"); //teilt am ";"

                    //Parameter typecasten zu dem, was sie im Konstruktor sein müssen:
                    String pName = split[0];
                    boolean pVollGel = Boolean.parseBoolean(split[1]);
                    Kurs pMeinKurs = this;
                    String pFile = split[2];

                    //fügt abgespeicherte Kurse wieder zur kursListe hinzu mit den in "split" gespeicherten Informationen
                    lekListe.add(new Lektion(pName, pVollGel, pMeinKurs, pFile, gui));
                    zeile = kursIn.readLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der Lektionsliste.");
            //System.out.println(e.getMessage());
        }
    }

    //diese Methode kann von der GUI aus aufgerufen werden, wenn eine neue Lektion erstellt wird
    public void addLektion(String pLektName) {
        Lektion neueLekt = new Lektion(pLektName, this, gui);
        lekListe.add(neueLekt);
        listeSpeichern();
        gui.aktLektion = neueLekt;
    }

    public Lektion getLektionAt(int pIndex) {
        return lekListe.get(pIndex);
    }

    public String getName() {
        return kName;
    }

    public File getFile() {
        return kursFile;
    }
    
    public int getAnzahlLek(){
        return lekListe.size();
    }

}
