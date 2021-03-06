package ptfo;

import java.util.*;
import java.util.regex.Pattern; // contient Pattern

/**
 *
 * @author MerysPIEGAY
 */
public class Phrase {

    public String phrase; /* le commentaire dans son ensemble*/

    public String[] elements; /* On ne peut pas faire un split sur un AL, ensemble de mots*/

    public ArrayList<String> mots;

    /**
     *
     * @param s
     */
    public Phrase(String s) {
        phrase = s;
        //elements = phrase.split("\\+"); 

        phrase = phrase.replace("é", "e");
        phrase = phrase.replace("è", "e");
        phrase = phrase.replace("ê", "e");
        phrase = phrase.replace("ù", "u");
        phrase = phrase.replace("à", "a");
        elements = phrase.split("[ .\",\'=!/():;_?\\+\\-%*$€¿¡]"); // MODIF : ajout des parenthèses
        mots = new ArrayList((Arrays.asList(elements)));
        phrase = join(mots);
    }

    @Override
    public String toString() {
        return phrase;
    }

    /**
     *
     * @return
     */
    public boolean detectNegation() {
        for (int i = 0; i < elements.length; i++) {
            if (Pattern.matches("ne|pas|jamais|peu|rien|aucun|aucune|sans|n|impossible", elements[i])) { // expression regulière
                System.out.println("NEGATION DÉTÉCTÉE !!!!!!!!!!!!");
                return true;
            } else {
                if (i < elements.length - 2) {
                    if (Pattern.matches("loins?", elements[i])
                            && Pattern.matches("d", elements[i + 1])
                            && Pattern.matches("être", elements[i + 2])) {
                        System.out.println("NEGATION DÉTÉCTÉE !!!!!!!!!!!!");
                        return true;
                    }
                }
            }
        }
        System.out.println("Aucune négation détéctée !");
        return false;
    }
    /* Fais le contraire de split, met le contenu d'un AL dans un même string */

    /**
     *
     * @param tab
     * @return
     */
    public String join(ArrayList<String> tab) {
        String joinstring = "";
        for (String s : tab) {
            joinstring += s + " ";
        }
        return joinstring;
    }

    /* change les mots du commentaire par le lemme correspondant le plus frequent */
    /**
     *
     * @return
     */
    public Phrase lemmatise() {
        Lexique l = new Lexique("src/ptfo/temp2.csv"); // récup de la phrase brute
        Lexique liste_pneus = new Lexique("src/ptfo/pneus_sans_dup.csv");
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

    /**
     *
     * @return
     */
    public Phrase simplifier() {
        Lexique l = new Lexique("src/ptfo/temp2.csv");
        Lexique liste_pneus = new Lexique("src/ptfo/pneus_sans_dup.csv");
        Phrase finalp = new Phrase("");
        for (String mot : mots) {
            if (!mot.equals("")) {
                try {
                    String lemme = l.lignes.get(l.getFreqLemme(mot.toLowerCase())).get(1);
                    String lemgram = l.getGram(lemme);
                    if (lemgram.equalsIgnoreCase("NOM")
                            || lemgram.equalsIgnoreCase("ADJ")
                            || lemgram.equalsIgnoreCase("ADV")
                            || lemgram.equalsIgnoreCase("VER")
                            || lemgram.equalsIgnoreCase("AUX")) {
                        finalp.mots.add(lemme);
                    }

                } catch (Exception ex) {
                    //Logger.getLogger(Phrase.class.getName()).log(Level.SEVERE, null, ex);
                    String obj = liste_pneus.correspond(mot);
                    finalp.mots.add(obj);
                }
            }
        }
        finalp.phrase = finalp.join(finalp.mots);
        return finalp;
    }

    public static void main(String[] args) {
        Phrase p = new Phrase("fédéral");
        System.out.println(p.phrase);
        p.detectNegation();

    }
}
