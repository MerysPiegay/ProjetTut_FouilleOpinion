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
import java.lang.Math;

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
                Pattern p = Pattern.compile(".*" + s.toLowerCase() + ".*");
                Matcher m = p.matcher(lignes.get(i).get(j).toLowerCase());
                if (m.matches()) {
                    similaire += lignes.get(i).get(j) + " | ";
                }
            }
        }
        if (similaire.equals("")) {
            similaire = plusProche(s);
        }
        return similaire;
    }

    /*Renvoi le mot le plus proche du string. (min de levenshtein)*/
    public String plusProche(String s) {
        String proche;
        ArrayList tab;
        tab = new ArrayList<Integer>();
        for (int i = 0; i < lignes.size(); i++) {
            if (Math.abs(lignes.get(i).get(0).length() - s.length()) <= 1) {

                // if(Math.abs(lignes.get(i).get(0).length()-s.length())<1){
                tab.add(levenshtein(s.toLowerCase(), lignes.get(i).get(0).toLowerCase()));
                // }
            } else {
                tab.add(10000);
            }
        }
        //System.out.println(lignes.get(min(tab)).get(0));
        proche = ((int) tab.get(min(tab)) > s.length() / 2 ? s : lignes.get(min(tab)).get(0));

        return proche;
    }

    /*Renvoi la distance de levenshtein entre deux strings*/
    public int levenshtein(String s, String p) {
        // ArrayList a;
        // a = new ArrayList<Integer>();
        int[][] tab;
        tab = new int[s.length() + 1][p.length() + 1];
        for (int i = 0; i <= s.length(); i++) {
            tab[i][0] = i;
        }
        for (int j = 0; j <= p.length(); j++) {
            tab[0][j] = j;
        }
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= p.length(); j++) {

                tab[i][j] = Math.min(Math.min(tab[i - 1][j] + 1,
                        tab[i][j - 1] + 1),
                        tab[i - 1][j - 1] + ((s.charAt(i - 1) == p.charAt(j - 1)) ? 0 : 1));
            }

        }
        return tab[s.length()][p.length()];
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

    /* sert à récupérer les indices de chaque occurence d'un mot s
     */
    public ArrayList allIndex(String s, int j) {
        ArrayList allind;
        allind = new ArrayList<>();

        for (int i = 0; i < lignes.size(); i++) {
            if (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                while (lignes.get(i).get(j).equalsIgnoreCase(s)) {
                    allind.add(i);
                    i++;
                }
                return allind;
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

    /* Renvoi l'indice du maximum dans un arraylist*/
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

    /*Renvoi l'indice du minimum dans un arraylist*/
    public int min(ArrayList<Integer> a) {
        int minimum;
        minimum = 0;
        for (int i = 1; i < a.size(); i++) {
            if (a.get(minimum) > a.get(i)) {
                minimum = i;
            }
        }
        return minimum;
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

    String getGram(String mot) {
        String gram = "";

        try {
            gram += lignes.get(getFreqLemme(mot)).get(3);
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
        Lexique l = new Lexique("src/ptfo/pneus_sans_dup.csv");
        System.out.println(l.correspond("T/a KO"));

    }
}
