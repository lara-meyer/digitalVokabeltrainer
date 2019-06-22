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
    public Kurs(String pName, GUI pGui) {
        kName = pName;
        gui = pGui;

        //erstellt im Ordner "Kurslisten" eine csv-Datei, die nach dem Kursnamen benannt ist und in der die Namen aller Lektionen gespeichert werden 
        //sollen, die zu diesem Kurs gehören
        kursFile = new File("Lektionslisten\\" + kName + ".csv");

        try {
            kursOut = new BufferedWriter(new FileWriter(kursFile));
        } catch (IOException e) {
            System.out.println("Fehler beim Erstellen des Kurses (FileWriter).");
            //System.out.println(e.getMessage());
        }

        lekListe.add(new Lektion(kName, gui)); //wenn ich einen Kurs erstelle, soll auch direkt eine Lektion hinzugefügt werden, sonst brauch ich den Kurs nicht
        listeSpeichern();

        try {
            //Reader hier, weil nur einmal benutzt wird
            kursIn = new BufferedReader(new FileReader(kursFile));
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
            kursIn = new BufferedReader(new FileReader(kursFile));
            listeEinlesen();
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der Lektionsliste (FileReader).");
            //System.out.println(e.getMessage());
        }

        listeSpeichern();
    }

    //Neue Lektion wird über "lekListe.add(new Lektion())" zur lekListe hinzugefügt, beliebig oft, dann muss am Ende listeSpeichern() aufgerufen werden, 
    //weil immer die ganze Datei überschrieben wird. Hier werden dann die Informationen von jedem Element der lekListe in die Datei geschrieben. Bei 
    //Programmaufruf muss dann die Datei wieder in die lekListe eingelesen werden, damit alle Elemente wieder vorhanden sind und beim nächsten Speichern 
    //wieder mitgeschrieben werden.
    private void listeSpeichern() {
        try {
            //Writer hier, damit bei jedem Speichern ein neuer Stream geöffnet wird; gibt sonst Probleme mit dem close()
            kursOut = new BufferedWriter(new FileWriter(kursFile));
            for (Lektion lek : lekListe) {
                kursOut.write(lek.getName() + ";" + lek.getVollGelernt() + ";" + lek.getMeinKurs() + ";" + "Vokabellisten\\" + lek.getMeinKurs() + "_" + lek.getName() + ".csv" + ";");
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
                    String pMeinKurs = split[2];
                    String pFile = split[3];

                    lekListe.add(new Lektion(pName, pVollGel, pMeinKurs, pFile, gui));//fügt abgespeicherte Kurse wieder zur kursListe hinzu mit den in "split" gespeicherten Informationen
                    zeile = kursIn.readLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der Lektionsliste.");
            //System.out.println(e.getMessage());
        }
    }
    
    public Object[] lekAuflisten(Lektion[] pArray, int pIndex){
        Object[] indexUndArray = new Object[2];
        int index = pIndex;
        for (Lektion lektion : lekListe) {
            pArray[index] = lektion;
            index++;
        }
        return indexUndArray;//gibt zurück, bei welcher Zahl die Auflistung jetzt angekommen ist
    }

    //brauchten wir mal, jetzt nicht mehr, lassen wir aber erstmal drin
    public void addLektion() {
        lekListe.add(new Lektion(kName, gui));
        listeSpeichern();
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
    
    public int getAnzahlLek(){//kann glaub ich raus
        return lekListe.size();
    }

}
