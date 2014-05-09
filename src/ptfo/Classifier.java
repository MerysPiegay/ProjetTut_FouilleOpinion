package ptfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class Classifier {

    String userid;
    String password;
    String URL;
    java.sql.Connection conn;

    Classifier() throws SQLException {
        userid = "p1306440";
        password = "191847";
        URL = "jdbc:oracle:thin:@iuta.univ-lyon1.fr:1521:orcl";
        conn = java.sql.DriverManager.getConnection(URL, userid, password);
        java.sql.DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        if (conn != null) {
            System.out.println("Connexion établie");
        } else {
            System.out.println("Connexion échouée");
        }
    }

    int classifier(Phrase p) throws SQLException {
        int classe = 0;
        for (String mot : p.mots) {
            mot = mot.toLowerCase();
            Statement lanceRequete1;
            lanceRequete1 = conn.createStatement();
            ResultSet requete1;
            requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
                    + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                    + mot + "' and (CGRAM = 'NOM' or CGRAM = 'VER' or CGRAM = 'ADV' or CGRAM = 'ADJ') ) ");
            while (requete1.next()) {
                //System.out.println(requete1.getString("LEMME"));
                Statement lanceRequete2;
                lanceRequete2 = conn.createStatement();
                ResultSet requete2;
                mot = requete1.getString("LEMME");
                if(mot.length()>1){
                requete2 = lanceRequete2.executeQuery("select * from MOTS where LEMME = '" + mot + "'");
                requete2.next();
                Statement lanceRequete2_1;
                lanceRequete2_1 = conn.createStatement();
                ResultSet requete2_1;
                requete2_1 = lanceRequete2_1.executeQuery("select count(ID_LEMME) from MOTS where LEMME = '" + mot + "'");
                requete2_1.next();
                if (requete2_1.getInt(1) != 0) {
                    //System.out.println("\t"+ mot+"\n\t"+requete2.getInt("CLASSE") +"\n\t"+ requete2.getInt("OCCUR")+"\n\t"+ requete1.getString("CGRAM"));
                    classe += requete2.getInt("CLASSE") / requete2.getInt("OCCUR");
                }
                requete2.close();
                lanceRequete2.close();
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
        c=new Commentaire("205/60/15 91V Pneus très dangereux sur sol autre que totalement sel, et encore ... il y a beaucoup mieux, et a des prix très abordable ");
        int classe=0;
        for (String p : c.phrases) {
            classe+=l.classifier(new Phrase(p));
        }
        System.out.println(classe);
        
    }
}
