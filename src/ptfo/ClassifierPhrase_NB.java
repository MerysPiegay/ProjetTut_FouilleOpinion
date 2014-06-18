package ptfo;
/**/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassifierPhrase_NB {

    Connection co;

    ClassifierPhrase_NB() throws SQLException {

        co = new Connection();
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean estPertinent(Phrase phrase) throws SQLException {
        /*int pertinent = 0;
         int nbmot = 0;
         for (String mot : phrase.mots) {
         mot = mot.toLowerCase();
         Statement lanceRequete1;
         lanceRequete1 = co.conn.createStatement();
         ResultSet requete1;
         requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
         + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
         + mot + "') and (CGRAM = 'NOM' or CGRAM = 'VER' or CGRAM = 'ADV' or CGRAM = 'ADJ')");

         nbmot++;
         if (requete1.next()) {
         mot = requete1.getString("LEMME");
         Statement lanceRequet1;
         lanceRequet1 = co.conn.createStatement();
         ResultSet requet1;

         requet1 = lanceRequet1.executeQuery("select * from PERTINENTS where MOT='" + mot + "'");
         if (requet1.next()) {
         //System.out.println(mot + " ++++++ " + requete1.getString("MOT"));
         pertinent++;
         }
         lanceRequet1.close();
         requet1.close();
         } else {

         Statement lanceRequet2;
         lanceRequet2 = co.conn.createStatement();
         ResultSet requet2;
         requet2 = lanceRequet2.executeQuery("select * from PNEUS");
         boolean trouve = false;
         while (requet2.next() && !trouve) {
         for (int i = 0; i < 5; i++) {

         if (requet2.getString("DESIGNATION" + i) != (null)) {
         if (requet2.getString("DESIGNATION" + i).equalsIgnoreCase(mot)
         && !requet2.getString("DESIGNATION" + i).equalsIgnoreCase("le")
         && !isNumeric(requet2.getString("DESIGNATION" + i))) {
         //                System.out.println(requet2.getString("DESIGNATION" + i));
         //              System.out.println(mot);
         trouve = true;
         pertinent++;
         }
         }
         }
         }
         lanceRequet2.close();
         requet2.close();
         }
         lanceRequete1.close();
         requete1.close();

         }
         //System.out.println("PHRASE :" + phrase.phrase + "\nnb mots : " + nbmot + "\nnb mots pertinents : " + pertinent);

         return (pertinent > 0);*/
        return true;
    }

    public double classifier(Phrase p) throws SQLException {
        int classe = 0;
        //if (estPertinent(p)) {
        for (String mot : p.mots) {
            mot = mot.toLowerCase();
            Statement lanceRequete1;
            lanceRequete1 = co.conn.createStatement();
            ResultSet requete1;
            requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
                    + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                    + mot + "' )and (CGRAM = 'NOM' "
                    //+ "or CGRAM = 'VER' "
                    + "or CGRAM = 'ADV' or CGRAM = 'ADJ')  ");
            if (requete1.next()) {
                //System.out.println(requete1.getString("LEMME"));
                Statement lanceRequete2;
                lanceRequete2 = co.conn.createStatement();
                ResultSet requete2;
                mot = requete1.getString("LEMME");
                if (mot.length() > 1) {
                    requete2 = lanceRequete2.executeQuery("select * from MOTS_HY_TEST where LEMME = '" + mot + "'");
                    if (requete2.next()) {
                        Statement lanceRequete2_1;
                        lanceRequete2_1 = co.conn.createStatement();
                        ResultSet requete2_1;
                        requete2_1 = lanceRequete2_1.executeQuery("select count(ID_LEMME) from MOTS_HY_TEST where LEMME = '" + mot + "'");
                        requete2_1.next();
                        if (requete2_1.getInt(1) != 0
                                && Math.abs((double) (requete2.getInt("CLASSE")) / (double) requete2.getInt("OCCUR")) >= .0
                                && requete2.getString("LEMME").length() > 2
                                && requete2.getInt("OCCUR") > 3) {
                            System.out.println(mot + "\t" + ((double) (requete2.getInt("CLASSE"))));
                            classe += requete2.getInt("CLASSE") /*/ requete2.getInt("OCCUR")*/;
                        }
                        lanceRequete2_1.close();
                    } else {
                        System.out.println("\033[31mLe mot " + mot + " est inconnu");
                    }
                    requete2.close();
                    lanceRequete2.close();

                } else {
                    System.out.println("\033[31mLe mot " + mot + " est inconnu");
                }
                requete1.close();
            }
            lanceRequete1.close();
        }
        if (p.detectNegation()) {
            classe *= -1;
        }
        /* else {
         System.out.println("\033[34mnon pertinent");
         Statement lanceRequete1;
         lanceRequete1 = co.conn.createStatement();
         lanceRequete1.executeUpdate("insert into PHRASE_NP values(select max(id)from PHRASE_NP,'"+p.phrase+"'");
         }*/
        if (classe != 0) {
            classe = classe / Math.abs(classe);
        }
        return classe;
    }

    public static void main(String[] args) throws SQLException {
        ClassifierPhrase_NB l;
        l = new ClassifierPhrase_NB();
        Commentaire c;
        c = new Commentaire(" pneu bruyant et pas très confortable,par contre sur l'a3 ça accroche vraiment bien la route même sur sol humide,a recommender si conduite sportive ");
        int classe = 0;
        for (String p : c.phrases) {
            classe += l.classifier(new Phrase(p));

            System.out.println(p);
        }
        System.out.println(classe);
    }
}
