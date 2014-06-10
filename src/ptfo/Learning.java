package ptfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Learning {

    Learning() throws SQLException {

    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void apprentissage() throws SQLException {
        Connection co;
        co = new Connection();

        Statement lanceRequete;
        lanceRequete = co.conn.createStatement();
        ResultSet requete;
        requete = lanceRequete.executeQuery("select * from COMMENTAIRE");
        while (requete.next()) {
            Commentaire comm;
            comm = new Commentaire(requete.getString("POSTE"));
            for (String phrases : comm.phrases) {
                Phrase phrase;
                phrase = new Phrase(phrases);
                //System.out.println(phrase.mots);

                for (String mot : phrase.mots) {
                    if (mot.length() > 0) {
                        mot = mot.toLowerCase();
                        Statement lanceRequete1;
                        lanceRequete1 = co.conn.createStatement();
                        ResultSet requete1;
                        requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
                                + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                                + mot + "') and (CGRAM = 'NOM' or CGRAM = 'VER' or CGRAM = 'ADV' or CGRAM = 'ADJ')  ");
                        while (requete1.next()) {
                            Statement lanceRequete2;
                            lanceRequete2 = co.conn.createStatement();
                            ResultSet requete2;
                            mot = requete1.getString("LEMME");
                            if (!(mot.equals("être") || mot.equals("avoir"))) {
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
                                    lanceRequete3.executeUpdate("insert into MOTS " + "values ((select count(ID_LEMME)from MOTS),'" + requete1.getString("LEMME") + "'," + requete.getInt("CLASSE") + ",1)");
                                    lanceRequete3.close();
                                } else {
                                    requete2.next();

                                    int rate = requete2.getInt("CLASSE") + requete.getInt("CLASSE");
                                    System.out.println("_________________ " + rate + " ___________________________");

                                    PreparedStatement reqins;

                                    reqins = co.conn.prepareStatement("update MOTS set CLASSE = " + rate + ", OCCUR = OCCUR + 1 where LEMME = " + "'" + requete1.getString("LEMME") + "'");
                                    reqins.executeUpdate();
                                    reqins.close();

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
                    System.out.println(mot);
                }

            }

            requete.close();
            lanceRequete.close();

        }

    }

    public static void main(String[] args) throws SQLException {
        Learning l;
        l = new Learning();
        l.apprentissage();
    }
}
