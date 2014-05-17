package ptfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Learning {

    Learning() throws SQLException {

    }

    void apprentissage() throws SQLException {
        Connection co;
        co = new Connection();
        Statement lanceRequete;
        lanceRequete = co.conn.createStatement();
        ResultSet requete;
        requete = lanceRequete.executeQuery("select * from RCOMMENTAIRE");
        while (requete.next()) {
            Phrase phrase;
            phrase = new Phrase(requete.getString("RCOMMENT"));
            //System.out.println(phrase.mots);

            for (String mot : phrase.mots) {
                if (mot.length() > 0) {
                    mot = mot.toLowerCase();
                    Statement lanceRequete1;
                    lanceRequete1 = co.conn.createStatement();
                    ResultSet requete1;
                    requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
                            + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                            + mot + "' and (CGRAM = 'NOM' or CGRAM = 'VER' or CGRAM = 'ADV' or CGRAM = 'ADJ') ) ");
                    while (requete1.next()) {
                        Statement lanceRequete2;
                        lanceRequete2 = co.conn.createStatement();
                        ResultSet requete2;
                        mot = requete1.getString("LEMME");
                        requete2 = lanceRequete2.executeQuery("select * from MOTS where LEMME = '" + mot + "'");

                        Statement lanceRequete2_1;
                        lanceRequete2_1 = co.conn.createStatement();
                        ResultSet requete2_1;

                        requete2_1 = lanceRequete2_1.executeQuery("select count(ID_LEMME) from MOTS where LEMME = '" + mot + "'");
                        requete2_1.next();
                        System.out.println("|||||||||" + requete2_1.getInt(1));
                        if (requete2_1.getInt(1) == 0) {
                            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            Statement lanceRequete3;
                            lanceRequete3 = co.conn.createStatement();
                            lanceRequete3.executeUpdate("insert into MOTS " + "values ((select count(ID_LEMME)from MOTS),'" + requete1.getString("LEMME") + "'," + requete.getInt("RATE") + ",1)");
                            lanceRequete3.close();
                        } else {
                            requete2.next();

                            int rate = requete2.getInt("CLASSE") + requete.getInt("RATE");
                            System.out.println("_________________ " + rate + " ___________________________");

                            PreparedStatement reqins;
                            reqins = co.conn.prepareStatement("update MOTS set CLASSE = " + rate + ", OCCUR = OCCUR + 1 where LEMME = " + "'" + requete1.getString("LEMME") + "'");
                            /*      reqins.setInt(1, rate);
                             reqins.setInt(2, 1);
                             reqins.setString(3, "'" + requete1.getString("LEMME") + "'");
                             */ reqins.executeUpdate();
                            reqins.close();

                        }
                        requete2.close();
                        lanceRequete2.close();
                        requete2_1.close();
                        lanceRequete2_1.close();

                    }
                    requete1.close();
                    lanceRequete1.close();
                }
                System.out.println(mot);
            }

        }

        requete.close();
        lanceRequete.close();

    }

    public static void main(String[] args) throws SQLException {
        Learning l;
        l = new Learning();
        l.apprentissage();
    }
}
