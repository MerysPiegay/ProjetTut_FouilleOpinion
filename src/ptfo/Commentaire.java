package ptfo;

import java.util.ArrayList;
import java.util.Arrays;

public class Commentaire {

    ArrayList<String> phrases;
    String[] elements;
    Commentaire(String commentaire) {
        elements = commentaire.split("[.?!]");
        phrases = new ArrayList((Arrays.asList(elements)));
    }

    @Override
    public String toString() {
        return "phrases=" + phrases ;
    }

    public static void main(String[] args) {

    }
}
