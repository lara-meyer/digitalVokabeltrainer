/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vokabeltrainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import vokabeltrainer.src.gui.GUI;

public class Vokabeltrainer {

    public static ArrayList<Kurs> kursListe = new ArrayList<>();

    //erstellt schonmal den Writer und Reader, damit in mehreren Methodenabschnitten aufrufbar unabhängig von Schleifen etc.,
    //aber übergibt noch keine Datei, weil hier die IOException nicht gethrowt werden kann
    private static BufferedWriter trainOut;
    private static BufferedReader trainIn;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //hier wird das Programm gestartet: es wird eine GUI erstellt, Reader aufgerufen, die die Dateien einlesen, passende Writer erstellt und die GUI mit 
        //den jetzt eingelesenen Daten aktualisiert; ab da läuft quasi alles über die Listener der GUI-Komponenten
        try {
            GUI gui = new GUI();
            trainIn = new BufferedReader(new InputStreamReader(new FileInputStream("Kursliste.csv"),"UTF-8"));
            listeEinlesen(gui); //liest alle Kurse ein, die lesen ihre Lektion ein und die wiederum ihre Vokabeln

            listeSpeichern(); //das gerade Eingelesene wird direkt wieder abgespeichert, weil die Datei leer ist, nachdem der Reader drübergelaufen ist
            
            gui.setAlleKurse(kursListe);
            gui.menuPanel = gui.updateMenuPanel(gui, alleLektionen());
            gui.menuPanel.updateUI();            
            
        } catch (IOException e) { //hier fangen wir Fehler auf, die ganz zum Schluss noch übrig sind und sonst nirgendwo behandelt werden
            System.out.println("Upsi. Irgendwo ist ein Input/Output schief gelaufen, aber ich weiß nicht genau wo. Sorry... ");
            //die nachfolgende Zeile kann entkommentiert werden, wenn die provisorische Fehlermeldung auftritt, um genauere Informationen zu bekommen (so in alle try-catch-Blöcken im Programm geregelt)
            //System.out.println(e.getMessage()); 
        }
    }

    //überschreibt vorhandene Datei "Kursliste.csv" mit allen Elementen, die zum Zeitpunkt des Aufrufs in der kursListe sind
    public static void listeSpeichern() { 
        try {
            //bei jedem Aufruf muss ein neuer Writer geöffnet werden, weil er sonst nach dem ersten mal mit close() geschlossen ist und nicht mehr auf die 
            //Datei geschrieben werden kann
            trainOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Kursliste.csv"),"UTF-8"));
            for (Kurs kurs : kursListe) {
                trainOut.write(kurs.getName() + ";" + "Lektionslisten\\" + kurs.getName() + ".csv" + ";");
                trainOut.newLine();
            }
            trainOut.write("endOfList"); //markiert hinterher für den Reader ganz definitiv das Ende der Liste
            trainOut.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Kursliste.");
            //System.out.println(e.getMessage());
        }
    }    

    //liest Zeile für Zeile die Datei "Kursliste.csv" ein, teilt am ";" und speichert entsprechend Kursnamen und Dateinamen der Lektionsliste in der kursListe ab
    private static void listeEinlesen(GUI pGui) {
        try {
            String zeile = trainIn.readLine();
            if (zeile != null) { //wenn Datei nicht leer
                while (!zeile.equals("endOfList")) { //"endOfList" markiert das Ende der Datei, wird bei listeSpeichern() immer ans Ende gesetzt
                    String[] split = zeile.split(";"); //teilt am ";"
                    kursListe.add(new Kurs(split[0], split[1], pGui)); //fügt abgespeicherte Kurse wieder zur kursListe hinzu mit den in "split" gespeicherten Informationen
                    zeile = trainIn.readLine();
                }
            }
            trainIn.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der Kursliste.");
            //System.out.println(e.getMessage());
        }
    }
    
    //hier werden aus allen Kursen die Lektionen in eine Liste geschrieben, somit sind sie darin dann nach Kurs gruppiert und werden in der Reihenfolge auf
    //die GUI übergeben und dort als Buttons angezeigt
    public static ArrayList<Lektion> alleLektionen(){        
        ArrayList<Lektion> alleLek = new ArrayList<>();
        kursListe.forEach((kurs) -> {
            for (int i = 0; i < kurs.getAnzahlLek(); i++) {
                alleLek.add(kurs.getLektionAt(i));
            }
        });
        return alleLek;
    }

}
