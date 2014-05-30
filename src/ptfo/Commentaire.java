package ptfo;

import java.util.ArrayList;
import java.util.Arrays;

public class Commentaire {

    ArrayList<String> phrases;
    String[] elements;
    
    Commentaire(String commentaire) {
        elements = commentaire.split("[.?!]|[, ]mais[, ]|[, ]certes[, ]|[, ]par contre[, ]|[, ]en revanche[, ]|[, ]néanmoins[, ]|[, ]neanmoins |[, ]bien que[, ]");
        phrases = new ArrayList((Arrays.asList(elements)));
    }

    @Override
    public String toString() {
        return "phrases=" + phrases ;
    }

    public static void main(String[] args) {
        Commentaire c;
        c= new Commentaire("il est vrai que blabla en revanche le blibli est mieux");
        System.out.println(c);
    }
}
