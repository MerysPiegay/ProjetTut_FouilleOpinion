
package ptfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseCommentaires {

    /* meme chose qu'un lexique mais sans methode pour comparer a un lexique */
    String path;
    ArrayList<ArrayList<String>> lignes = new ArrayList();
    ArrayList<String> colonnes = new ArrayList();

    BaseCommentaires(String path) {
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

    public static void main(String[] args) {
        Lexique l = new Lexique("src/ptfo/temp2.csv");
        BaseCommentaires bc = new BaseCommentaires("src/ptfo/frdbtyre.csv");
        Lexique liste_pneus = new Lexique("src/ptfo/pneus_sans_dup.csv");

        Phrase p = new Phrase(bc.lignes.get(2).get(4));
        System.out.println(p.mots);
        
        Phrase simplep = p.simplifier();
        System.out.println(simplep);
    }
}
