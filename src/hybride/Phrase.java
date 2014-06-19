package hybride;

import ptfo.*;
import java.util.*;
import java.util.regex.Pattern; // contient Pattern

/**
 *
 * @author MerysPIEGAY
 */
public class Phrase {

    public String phrase; /* la phrase dans son ensemble*/

    public String[] elements; /* On ne peut pas faire un split sur un AL, ensemble de mots*/

    public ArrayList<String> mots;

    /**
     *
     * @param s
     */
    public Phrase(String s) {
        phrase = s;
        //on supprime les accents (à compléter)
        phrase = phrase.replace("é", "e");
        phrase = phrase.replace("è", "e");
        phrase = phrase.replace("ê", "e");
        phrase = phrase.replace("ù", "u");
        phrase = phrase.replace("à", "a");
        //on découpe la phrase en mots
        elements = phrase.split("[ .\",\'=!/():;_?\\+\\-%*$€¿¡]");
        mots = new ArrayList((Arrays.asList(elements)));
        //on joint les mots avec des espaces
        phrase = join(mots);
    }

    @Override
    public String toString() {
        return phrase;
    }
    
    //cette méthode détecte la négation dans un objet Phrase (à améliorer)
    public boolean detectNegation() {
        for (int i = 0; i < elements.length; i++) {
            if (Pattern.matches("moins|ne|pas|jamais|peu|rien|aucun|aucune|sans|n|impossible", elements[i])) {
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
    public String join(ArrayList<String> tab) {
        String joinstring = "";
        for (String s : tab) {
            joinstring += s + " ";
        }
        return joinstring;
    }
 
    public static void main(String[] args) {

    }
}
