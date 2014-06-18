package hybride;

import ptfo.*;
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
            if (Pattern.matches("moins|ne|pas|jamais|peu|rien|aucun|aucune|sans|n|impossible", elements[i])) { // expression regulière
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
 
    public static void main(String[] args) {
        //Phrase p = new Phrase("fédéral");
        //System.out.println(p.phrase);
        //p.detectNegation();
        String s="1234567890";
        System.out.println(s.substring(1,2));
    }
}
