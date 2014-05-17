package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Classifier {

    Classifier() throws SQLException {

    }

    int classifier(Phrase p) throws SQLException {
        Connection co;
        co = new Connection();
        int classe = 0;
        for (String mot : p.mots) {
            mot = mot.toLowerCase();
            Statement lanceRequete1;
            lanceRequete1 = co.conn.createStatement();
            ResultSet requete1;
            requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
                    + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                    + mot + "' and (CGRAM = 'NOM' or CGRAM = 'VER' or CGRAM = 'ADV' or CGRAM = 'ADJ') ) ");
            while (requete1.next()) {
                //System.out.println(requete1.getString("LEMME"));
                Statement lanceRequete2;
                lanceRequete2 = co.conn.createStatement();
                ResultSet requete2;
                mot = requete1.getString("LEMME");
                if (mot.length() > 1) {
                    requete2 = lanceRequete2.executeQuery("select * from MOTS where LEMME = '" + mot + "'");
                    requete2.next();
                    Statement lanceRequete2_1;
                    lanceRequete2_1 = co.conn.createStatement();
                    ResultSet requete2_1;
                    requete2_1 = lanceRequete2_1.executeQuery("select count(ID_LEMME) from MOTS where LEMME = '" + mot + "'");
                    requete2_1.next();
                    if (requete2_1.getInt(1) != 0
                            && Math.abs((double) (requete2.getInt("CLASSE")) / (double) requete2.getInt("OCCUR")) >= .5) {
                        System.out.println("\t" + ((double) (requete2.getInt("CLASSE")) / (double) requete2.getInt("OCCUR")));
                        classe += requete2.getInt("CLASSE");
                    }
                    requete2.close();
                    lanceRequete2.close();
                    requete2_1.close();
                    lanceRequete2_1.close();
                }
            }
            requete1.close();
            lanceRequete1.close();
        }
        if (p.detectNegation()) {
            classe *= -1;
        }
        return classe;
    }

    public static void main(String[] args) throws SQLException {
        Classifier l;
        l = new Classifier();
        Commentaire c;
        c = new Commentaire("bons");
        int classe = 0;
        for (String p : c.phrases) {
            classe += l.classifier(new Phrase(p));
        }
        System.out.println(classe);

    }
}
