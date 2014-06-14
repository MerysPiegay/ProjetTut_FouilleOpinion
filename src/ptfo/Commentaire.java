package ptfo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author MerysPiegay
 */
public class Commentaire {

    public ArrayList<String> phrases;
    
    public String[] elements;
    
    /**
     *
     * @param commentaire
     */
    public Commentaire(String commentaire) {
        elements = commentaire.split("[.?!(),:;]|[, ]donc[, ]|[, ]mais[, ]|[, ]et[, ]|[, ]certes[, ]|[, ]par contre[, ]|[, ]en revanche[, ]|[, ]néanmoins[, ]|[, ]neanmoins |[, ]bien que[, ]");
        phrases = new ArrayList((Arrays.asList(elements)));
    }

    @Override
    public String toString() {
        return "phrases=" + phrases ;
    }

    public static void main(String[] args) {
        Commentaire c;
        c= new Commentaire("il est vrai que blabla.en revanche le blibli est mieux");
        System.out.println(c.phrases.size());
    }
}
