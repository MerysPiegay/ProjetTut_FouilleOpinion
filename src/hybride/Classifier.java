package hybride;

import static java.lang.System.exit;
import java.sql.*;


public class Classifier {

    Connection co;

    Classifier() throws SQLException {
        co = new Connection();
    }
    //même fonction que dans Learning.java (voir si y a pas moyen de créer une classe pour éviter des redondances de code)
    public String lemme(String mot) throws SQLException {
        Statement lanceRequete1;
        lanceRequete1 = co.conn.createStatement();
        ResultSet requete1;
        requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
                + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                + mot + "' )and (CGRAM = 'NOM' "
                + "or CGRAM = 'VER' "
                + "or CGRAM = 'ADV' or CGRAM = 'ADJ')  ");
        String lemme = "";
        if (requete1.next()) {
            lemme = requete1.getString("LEMME");
        }
        lanceRequete1.close();
        requete1.close();
        return lemme;
    }
    /*permet de classifier une table entière. 
    prend en argument 
    le nom de la table à noter, 
    le nom de la table dans laquelle les phrases notées seront stockées 
    et le nom de la table contenant les mots appris grace à la classe Learning 
    */
    public void classifierBase(String baseComBase, String baseComTest, String baseMotsNote) throws SQLException {
        Statement lanceRequete;
        lanceRequete = co.conn.createStatement();
        ResultSet requete;
        requete = lanceRequete.executeQuery("select * from " + baseComBase);
        while (requete.next()) {
            classifier(new Phrase(requete.getString("PHRASE")), requete.getInt("ID_PHRASE"), baseMotsNote, baseComBase, baseComTest);
        }
        lanceRequete.close();
        requete.close();
    }
    /*permet de classifier un objet Phrase. 
    prend en argument 
    l'objet Phrase à noter, 
    l'id de la phrase à noter,
    le nom de la table contenant les mots appris grace à la classe Learning , 
    le nom de la table à noter 
    et le nom de la table dans laquelle les phrases notées seront stockées
    */
    public void classifier(Phrase phrase, int id, String baseMotsNote, String baseComBase, String baseComTest) throws SQLException {
        double comPos, comNeu, comNeg, comTot;
        Statement lanceRequete;
        lanceRequete = co.conn.createStatement();
        ResultSet requete;
        requete = lanceRequete.executeQuery("select count(*) from " + baseComBase + " where CLASSE=1");
        requete.next();
        comPos = requete.getInt(1);
        requete.close();
        lanceRequete.close();
        Statement lanceRequete_;
        lanceRequete_ = co.conn.createStatement();
        ResultSet requete_;
        requete_ = lanceRequete_.executeQuery("select count(*) from " + baseComBase + " where CLASSE=0");
        requete_.next();
        comNeu = requete_.getInt(1);
        requete_.close();
        lanceRequete_.close();
        Statement lanceRequete__;
        lanceRequete__ = co.conn.createStatement();
        ResultSet requete__;
        requete__ = lanceRequete__.executeQuery("select count(*) from " + baseComBase + " where CLASSE=-1");
        requete__.next();
        comNeg = requete__.getInt(1);
        requete__.close();
        lanceRequete__.close();
        comTot = comPos + comNeu + comNeg;
        double notePos = 1;
        double noteNeu = 1;
        double noteNeg = 1;
        for (String mot : phrase.mots) {

            String lemme = lemme(mot);
            if (!lemme.equals("") && lemme.length() > 1) {
                mot = lemme.toLowerCase();
                double occPos, occNeu, occNeg;
                Statement lanceRequete1_;
                lanceRequete1_ = co.conn.createStatement();
                ResultSet requete1_;
                requete1_ = lanceRequete1_.executeQuery("select * from PERTINENTS"
                        + " where MOT='" + mot + "'");
                //if (!requete1_.next()) {
                    Statement lanceRequete1;
                    lanceRequete1 = co.conn.createStatement();
                    ResultSet requete1;
                    requete1 = lanceRequete1.executeQuery("select * from " + baseMotsNote
                            + " where LEMME='" + mot + "'");
                    if (requete1.next()) {
                        occPos = requete1.getInt("OCCUR_POS");
                        occNeu = requete1.getInt("OCCUR_NEU");
                        occNeg = requete1.getInt("OCCUR_NEG");
                        notePos *= (occPos!=0)?(occPos / comPos):1/comPos;
                        noteNeu *= (occNeu!=0)?(occNeu / comNeu):1/comNeu;
                        noteNeg *= (occNeg!=0)?(occNeg / comNeg):1/comNeu;
                    }
                    requete1.close();
                    lanceRequete1.close();
               // }
                requete1_.close();
                lanceRequete1_.close();
            }
        }
        notePos *= comPos / comTot;
        noteNeu *= comNeu / comTot;
        noteNeg *= comNeg / comTot;
        PreparedStatement ajoutComTest;
        ajoutComTest = co.conn.prepareStatement("insert into " + baseComTest + " values (?,?,?)");
        ajoutComTest.setInt(1, id);
        ajoutComTest.setString(2, phrase.phrase);
        int classe = 0;
        if (Math.max(noteNeu, Math.max(notePos, noteNeg)) == notePos) {
            classe = 1;
        } else {
            if (Math.max(noteNeu, Math.max(notePos, noteNeg)) == noteNeu) {
                classe = 0;
            } else {
                if (Math.max(noteNeu, Math.max(notePos, noteNeg)) == noteNeg) {
                    classe = -1;
                } else {
                    System.out.println("Erreur");
                    exit(-1);
                }
            }
        }
        if (phrase.detectNegation()) {
            classe *= -1;
        }
        ajoutComTest.setInt(3, classe);
        ajoutComTest.executeUpdate();
        ajoutComTest.close();
        System.out.println("phrase : " + phrase + "\n\tclasse : " + classe);
    }

    public static void main(String[] args) throws SQLException {
        Classifier c;
        c = new Classifier();
        c.classifierBase("PHRASE_NB", "PHRASE_HY_TEST", "MOTS_HY");
    }

}
