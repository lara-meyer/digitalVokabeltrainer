/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vokabeltrainer;

/**
 *
 * @author Lena
 */
public class Karteikarte {

    private String vokA;
    private String vokZ;
    private String hilfssatz;
    private boolean gelernt;
    private int status; //status von 0-3 für die vier Lampen, 0 entspricht der roten Lampe, ab da aufsteigend
    private boolean favorit;
    private Lektion meineLektion;


    public Karteikarte(Lektion pMeineLektion) {
        setVokA();
        setVokZ();
        menuHS();        
        gelernt = false;
        status = 0;
        favorit = false;
        meineLektion = pMeineLektion;
    }
    
    public Karteikarte(String pVokA, String pVokZ, String pHS, boolean pGel, int pStatus, boolean pFav, Lektion pMeineLektion){
        vokA = pVokA;
        vokZ = pVokZ;
        hilfssatz = pHS;
        gelernt = pGel;
        status = pStatus;
        favorit = pFav;
        meineLektion = pMeineLektion;
    }
    
    private void setVokA(){
        System.out.println("Vokabel Ausgangssprache?");
        String pVokA = SystemInReader.readString();
        this.vokA = pVokA;
    }

    public String getVokA() {
        return vokA;
    }
    
    private void setVokZ(){
        System.out.println("Vokabel Zielsprache?");
        String pVokZ = SystemInReader.readString();
        vokZ = pVokZ;
    }

    public String getVokZ() {
        return vokZ;
    }
    
    private void menuHS() {
        System.out.println("Hilfssatz hinzufügen?");
        System.out.println("1: ja");
        System.out.println("2: nein");
        int eingHS = SystemInReader.readInt();
        if (eingHS == 1) {
            System.out.println("Hilfssatz?");
            String pHS = SystemInReader.readString();
            this.hilfssatz = pHS;
        }else if (eingHS == 2){
            this.hilfssatz = "Es wurde kein Hilfssatz eingegeben.";
        }
    }

    public String getHilfssatz() {
        return hilfssatz;
    }

    public void setStatus(int pWert) {//übernimmt zusätzlich Funktion von setGelernt, wenn erforderlich, daher keine extra setGelernt()
        status = pWert;
        if(pWert == 3){
            gelernt = true;
        }
        meineLektion.listeSpeichern(); //damit Status der Lampe beim nächsten Programmstart wieder übernommen wird
    }

    public int getStatus() {
        return status;
    }

    public boolean getGelernt() {
        return gelernt;
    }

    public void setFavorit(boolean pFavorit) {
        favorit = pFavorit;
        meineLektion.listeSpeichern();
    }

    public boolean getFavorit() {
        return favorit;
    }
    
    public Lektion getMeineLektion(){
        return meineLektion;
    }

}
