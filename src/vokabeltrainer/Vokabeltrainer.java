/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vokabeltrainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.ArrayList;
import vokabeltrainer.src.gui.GUI;

public class Vokabeltrainer {

    public static ArrayList<Kurs> kursListe = new ArrayList<>();

    //erstellt schonmal den Writer, damit in mehreren Methodenabschnitten aufrufbar unabhängig von Schleifen etc.,
    //aber übergibt noch keine Datei, weil hier die IOException nicht gethrowt werden kann
    private static BufferedWriter trainOut;
    private static BufferedReader trainIn;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try { //hier muss das Programm rein     
            GUI gui = new GUI();
            //erstmal alles Gespeicherte einlesen:
            trainIn = new BufferedReader(new FileReader("Kursliste.csv"));
            listeEinlesen(gui); //liest alle Kurse ein, die lesen ihre Lektion ein und die wiederum ihre Vokabeln

            //hier Writer auch oben, weil er nur ganz am Ende beim Schließen einmal aufgerufen wird, deswegen kein Problem mit close()
            trainOut = new BufferedWriter(new FileWriter("Kursliste.csv"));
            listeSpeichern();
            
            gui.menuPanel = gui.updateMenuPanel(gui, alleLektionen());
            gui.menuPanel.updateUI();            
            
        } catch (IOException e) { //hier fangen wir Fehler auf, die ganz zum Schluss noch übrig sind und sonst nirgendwo behandelt werden
            System.out.println("Upsi. Irgendwo ist ein Input/Output schief gelaufen, aber ich weiß nicht genau wo. Sorry... ");
            //System.out.println(e.getMessage());
        }
    }

    //brauchen wir ja jetzt quasi auch nicht mehr
    public static void printMenu(GUI pGui) {

        System.out.println("Welche Aktion möchten Sie ausführen?");
        System.out.println("0: Programm beenden");
        System.out.println("1: neue Lektion hinzufügen");
        System.out.println("Lektion üben:");
//        int menuNr = 2;
//        for (Kurs kurs : kursListe) {//lässt jeden Kurs seine Lektionen auflisten; übergibt dabei jeweils die Zahl, mit der die Auflistung weitergehen muss
//            int menuParam = menuNr;
//            menuNr = kurs.lekAuflisten(menuParam);
//        }
        //menuNr ist jetzt um 1 größer als die letzte Zahl, die aufgelistet wurde
        
        int menuEing = SystemInReader.readInt();
        while (menuEing != 0) {
            while (menuEing >= 11 || menuEing < 0) {
                System.out.println("Keine Option. Bitte eine der angezeigten Zahlen eingeben.");
                menuEing = SystemInReader.readInt();
            }
            switch (menuEing) {//man kann im Moment nur 1 auswählen
                //Hier müssten wir abfragen, wie viele Menüpunkte ausgegeben wurden und dementsprechend das Menü anpassen, aber switch braucht konstante cases.
                //Ich glaube, das geht mit switch nicht, gibt es in Swing irgendwas, was den ganzen Menü Kram leichter macht? Kann man da jeder der oben
                //angezeigten Optionen direkt einen Listener zuteilen?
                //Und noch nach Kursen unterteilen mit Pop-up Menü?
                case 1:
                    System.out.println("Zu welchem Kurs gehört die Lektion?");
                    String eingKursname = SystemInReader.readString();
                    System.out.println("Alles richtig geschrieben? : " + eingKursname); //die Sache mit den Tippfehlern
                    System.out.println("1: nein");
                    System.out.println("2: ja");
                    int janein = SystemInReader.readInt();
                    if(janein == 1){
                        System.out.println("Neu eingeben:");
                        eingKursname = SystemInReader.readString();
                    }                            

                    boolean vorhanden = false;
                    for (Kurs kurs : kursListe) { //wenn schon ein Kurs mit dem eingegebenen Namen existiert, soll die Lektion zu diesem Kur hinzugefügt werden
                        if (kurs.getName().equals(eingKursname)) {
                            kurs.addLektion();
                            vorhanden = true;
                        }
                    }

                    //wenn das Programm hier angekommen ist und vorhanden nicht auf true gestellt wurde, existiert noch kein Kurs mit dem eingegebenen Namen 
                    //also wird ein neuer erstellt und dort eine Lektion hinzugefügt
                    if (vorhanden == false) {
                        kursListe.add(new Kurs(eingKursname, pGui));
                    }

                    //hier gehts direkt wieder ins Hauptmenü
                    System.out.println();
                    printMenu(pGui);
                    menuEing = SystemInReader.readInt();
                    break;
            }
        }
        //wenn ~0~ eingegeben wurde, um das Programm zu beenden wird alles gespeichert und das Programm beendet; hier könnte man noch eine Nachfrage einfügen, 
        //ob wirklich beendet werden soll
        System.exit(0);

    }

    public static void listeSpeichern() { //überschreibt vorhandene Datei "Kursliste.csv" mit allen Elementen, die jetzt in der kursListe sind        
        try {
            for (Kurs kurs : kursListe) {
                trainOut.write(kurs.getName() + ";" + "Lektionslisten\\" + kurs.getName() + ".csv" + ";");
                trainOut.newLine();
            }
            trainOut.write("endOfList");
            trainOut.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Kursliste.");
            //System.out.println(e.getMessage());
        }
    }

    //liest Zeile für Zeile die Datei "Kursliste.csv" ein, teilt am ";" und speichert entsprechend Kursnamen und Dateinamen der Lektionsliste in der kursListe ab
    //Problem mit Umlauten und wahrscheinlich auch nicht-lateinischen Schriftsätzen
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
            trainIn.close(); //immer schließen, sonst gehts nicht
        } catch (IOException e) {
            System.out.println("Fehler beim Einlesen der Kursliste.");
            //System.out.println(e.getMessage());
        }
    }

//    public static Lektion[] lekAuflisten() {
//        Lektion[] aufgelisteteLek = new Lektion[5];
//        int index = 0;
//        for (Kurs kurs : kursListe) {//lässt jeden Kurs seine Lektionen auflisten; übergibt dabei jeweils die Zahl, mit der die Auflistung weitergehen muss
//            index = kurs.lekAuflisten(aufgelisteteLek, index);
//        }  
//        return aufgelisteteLek;
//    }
    
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
