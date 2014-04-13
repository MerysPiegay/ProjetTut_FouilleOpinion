/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author eyepop
 */
public class Lexique {

    String path; // chemin vers le lexique, adresse du fichier
    ArrayList<ArrayList<String>> lignes = new ArrayList(); // 
    ArrayList<String> colonnes = new ArrayList(); // a voir si on peut s'en passer.

    
    Lexique(String path) {
        String[] elements;
        this.path = path;
        try {
            Scanner scanner = new Scanner(new File(path));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                elements = line.split("\t");
                ArrayList<String> ligne = new ArrayList();
                ligne.addAll(Arrays.asList(elements));
                lignes.add(ligne);
            }
            scanner.close();
            /*  */
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Phrase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /* Regarde dans tout le tableau ligneS si contient le mot s */
    public boolean contient(String s) {
        for (int i = 0; i < lignes.size(); i++) {
            for (int j = 0; j < lignes.get(0).size(); j++) {
                if (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /* Regarde si tu as une coorespondance avec s dans lignes
    * Uniquement si on veut un mot entouré d'autres mots (ex. Avdan Neova)
    *
    */
    public String correspond(String s) {
        String similaire = "";
        for (int i = 0; i < lignes.size(); i++) {
            for (int j = 0; j < lignes.get(0).size(); j++) {
                Pattern p = Pattern.compile(".* "+s.toLowerCase()+" .*");
                Matcher m = p.matcher(lignes.get(i).get(j).toLowerCase());
                if (m.matches()) {
                    similaire += lignes.get(i).get(j) + " | ";
                }
            }
        }
        return similaire;
    }
    
    /* Renvoie un tableau d'indice int qui donne la colonne et la ligne de s
    * dans le AL lignes 
    */
    public int[] indiceDe(String s, int j) {

        int[] indices;
        indices = new int[2];
        indices[0] = -1;
        indices[1] = -1;
        for (int i = 0; i < lignes.size(); i++) {
            //   for (int j = 0; j <= 10; j++) {
            if (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                indices[0] = i;
                indices[1] = j;
                return indices;
            }
        }
        //}
        return indices;
    }
    /* 
    *  va chercher si on mot a une ou plusieurs occurences
    */
    public int occurenceDe(String s, int j) {

        int occur = 0;
        for (int i = 0; i < lignes.size(); i++) {
            //   for (int j = 0; j <= 10; j++) {
            if (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                while (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                    occur++;
                    i++;
                }
                return occur;
            }
        }
        //}
        return occur;
    }
    /* sert à récupérer les indices de chaque occurence d'un mot s
    */
    public ArrayList allIndex(String s, int j) {
        ArrayList allind;
        allind = new ArrayList<>();
        if (this.occurenceDe(s, j) > 0) {
            for (int i = 0; i < lignes.size(); i++) {
                if (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                    while (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                        allind.add(i);
                        i++;
                    }
                    return allind;
                }
            }
        }
        return null;
    }
    /* recupere l'indice de la plus grande fréquence d'apparition*/
    public int mostFreq(String s, int j) {
        int mostfreq = 0;
        ArrayList allind = allIndex(s, j);
        ArrayList<Double> freqs;
        freqs = new ArrayList<Double>();
        for (Object i : allind) {
            freqs.add(Double.parseDouble(lignes.get((int) i).get(7)));
        }
        mostfreq = (int) allind.get(0) + max(freqs);
        return mostfreq;
    }
    
    /* compare les frequences et retourne la max*/
    public int max(ArrayList<Double> a) {
        int maximum;
        maximum = 0;
        for (int i = 1; i < a.size(); i++) {
            if (a.get(maximum) < a.get(i)) {
                //maximum = a.get(i);
                maximum = i;
            }
        }
        return maximum;
    }
    
    /* renvoie l'indice du lemme le plus fréquent */
    public int getFreqLemme(String s) throws Exception {
        int ilemm = -1;
        if (contient(s)) {
            int i = mostFreq(s, 1);
            String lemme = lignes.get(i).get(2);
            ilemm = mostFreq(lemme, 1);
        } else {
            throw new Exception("Le mot '" + s + "' est introuvable...");
        }
        return ilemm;
    }
    /*renvoie le gram d'un mot*/
    String getGram(String mot, Lexique l) {
        String gram = "";

        try {
            gram += l.lignes.get(l.getFreqLemme(mot)).get(3);
        } catch (Exception ex) {

        }

        return gram;
    }
    /*sert a ecrire dans un fichier */
    void toFile(String newFile) throws IOException {
        File newfile = new File(newFile);
        PrintWriter out = new PrintWriter(new FileWriter(newfile));

// Write each string in the array on a separate line  
        for (ArrayList<String> ligne : lignes) {
            for (String s : ligne) {
                out.print(s + '\t');
            }
            out.print('\n');

        }
    }

    public static void main(String[] args) throws IOException {
        Lexique l = new Lexique("src/phrase/pneus_sans_dup.csv");
        System.out.println(l.correspond("KM2"));
        
    }
}
