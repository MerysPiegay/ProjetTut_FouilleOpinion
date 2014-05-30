/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author eyepop
 */
public class Pertinence {

    public static void main(String[] args) throws SQLException {
        Connection co;
        co = new Connection();
        Statement lanceRequete1;
        lanceRequete1 = co.conn.createStatement();
        ResultSet requete1;
        requete1 = lanceRequete1.executeQuery("select * from RCOMMENTAIRE_BACKUP");
        while (requete1.next()) {
            Commentaire c;
            c = new Commentaire(requete1.getString("RCOMMENT"));
            int classe = 0;
            for (String p : c.phrases) {
                for (String s : new Phrase(p).mots) {
                    if (!s.equals("")) {
                        s = s.toLowerCase();
                        Statement lanceRequete1_1;
                        lanceRequete1_1 = co.conn.createStatement();
                        ResultSet requete1_1;
                        requete1_1 = lanceRequete1_1.executeQuery("select count(MOT) from LEXIQUE where MOT = '"
                                + s + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                                + s + "'  )and (CGRAM = 'NOM' or CGRAM = 'VER' or CGRAM = 'ADV' or CGRAM = 'ADJ') ");
                        requete1_1.next();
                        Statement lanceRequete1_2;
                        lanceRequete1_2 = co.conn.createStatement();
                        ResultSet requete1_2;
                        requete1_2 = lanceRequete1_2.executeQuery("select * from LEXIQUE where MOT = '"
                                + s + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                                + s + "' and (CGRAM = 'NOM' or CGRAM = 'VER' or CGRAM = 'ADV' or CGRAM = 'ADJ') ) ");
                        requete1_2.next();
                        if (requete1_1.getInt(1) > 0) {
                            s = requete1_2.getString("LEMME");
                            Statement lanceRequete2;
                            lanceRequete2 = co.conn.createStatement();
                            ResultSet existe;
                            existe = lanceRequete2.executeQuery("select count(MOT) from PERTINENTS where MOT = '" + s + "'");
                            existe.next();
                            if (existe.getInt(1) == 0) {
                                Statement lanceRequete3;
                                lanceRequete3 = co.conn.createStatement();
                                lanceRequete2.executeUpdate("insert into PERTINENTS " + "values((select count(MOT) from PERTINENTS),'" + s + "',1)");
                                System.out.print("*************___");
                            } else {
                                Statement lanceRequete3;
                                lanceRequete3 = co.conn.createStatement();
                                lanceRequete2.executeUpdate("update PERTINENTS set OCCUR = OCCUR + 1 where MOT = '" + s + "'");
                                System.out.print("+++++++++++++___");
                            }

                            lanceRequete2.close();
                            existe.close();
                            System.out.println(s);
                        }
                        lanceRequete1_1.close();
                        requete1_1.close();
                        lanceRequete1_2.close();
                        requete1_2.close();

                    }
                }
            }
        }
        lanceRequete1.close();
        requete1.close();
    }
}
