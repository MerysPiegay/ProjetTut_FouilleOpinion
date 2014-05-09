/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author eyepop
 */
public class Commentaire {

    ArrayList<String> phrases;
    String[] elements;
    Commentaire(String commentaire) {
        elements = commentaire.split("[.?!]");
        phrases = new ArrayList((Arrays.asList(elements)));
    }

    public static void main(String[] args) {

    }
}
