/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern; // contient Pattern

/**
 *
 * @author eyepop
 */
public class Phrase {

    public String phrase; /* le commentaire dans sons ensemble*/
    String[] elements; /* On ne peut pas faire un split sur un AL, ensemble de mots*/
    ArrayList<String> mots;

    Phrase(String s) {
        phrase = s;
        elements = phrase.split("[ .,'?!]");
        mots = new ArrayList((Arrays.asList(elements)));
        phrase = join(mots);
    }

    /**
     * @param args the command line arguments
     */
    @Override
    public String toString() {
        return phrase;
    }

    boolean detectNegation() {
        for (int i = 0; i < elements.length; i++) {
            if (Pattern.matches("ne|pas|jamais|rien|aucun|aucune|n|impossible", elements[i])) { // expression regulière
                System.out.println("NEGATION DÉTÉCTÉE !!!!!!!!!!!!");
                return true;
            }
        }
        System.out.println("Aucune négation détéctée !");
        return false;
    }
    /* Fais le contraire de split, met le contenu d'un AL dans un même string */
    String join(ArrayList<String> tab) {
        String joinstring = "";
        for (String s : tab) {
            joinstring += s + " ";
        }
        return joinstring;
    }
    
    /* change les mots du commentaire par le lemme correspondant le plus frequent */
    Phrase lemmatise() {
        Lexique l = new Lexique("src/phrase/temp2.csv"); // récup de la phrase brute
        Lexique liste_pneus = new Lexique("src/phrase/pneus_sans_dup.csv");
        Phrase finalp = new Phrase("");
        for (String mot : mots) {
            try {
                if (liste_pneus.contient(mot)) {
                    finalp.mots.add(mot);
                } else {

                    finalp.mots.add(l.lignes.get(l.getFreqLemme(mot)).get(1));
                }
            } catch (Exception ex) {
                System.out.println("Exception : " + ex.getMessage());

            }
        }
        finalp.phrase = finalp.join(finalp.mots);
        return finalp; // renvoie la phrase lémmatisée
    }
    /* Même utilisation que lemmatiser sauf qu'on récupère que les mots pertinents 
    *
    */
    Phrase simplifier() {
        Lexique l = new Lexique("src/phrase/temp2.csv");
        Lexique liste_pneus = new Lexique("src/phrase/pneus_sans_dup.csv");
        Phrase finalp = new Phrase("");
        for (String mot : mots) {
            try {
                String lemme = l.lignes.get(l.getFreqLemme(mot)).get(1);
                String lemgram = l.getGram(lemme, l);
                if (lemgram.equalsIgnoreCase("NOM")
                        || lemgram.equalsIgnoreCase("ADJ")
                        || lemgram.equalsIgnoreCase("ADV")
                        || lemgram.equalsIgnoreCase("VER")
                        || lemgram.equalsIgnoreCase("AUX")) {
                    finalp.mots.add(lemme);
                } else {

                    String obj = liste_pneus.correspond(mot);
                    if (!obj.equals("")) {
                        finalp.mots.add(obj);

                    }

                }
                finalp.phrase = finalp.join(finalp.mots);

                return finalp;
            } catch (Exception ex) {
                Logger.getLogger(Phrase.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return finalp;
    }

    public static void main(String[] args) {

    }
}
