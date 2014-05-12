package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Evaluer {

    String userid;
    String password;
    String URL;
    java.sql.Connection conn;
    Double ratio;
    Classifier classifieur;

    Evaluer() throws SQLException {
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
        classifieur = new Classifier();
    }

    public Double getRatio() throws SQLException {
        Statement lanceRequete1;
        lanceRequete1 = conn.createStatement();
        ResultSet requete1;
        requete1 = lanceRequete1.executeQuery("select * from RCOMMENTAIRE");
        Double total=0.;
        Double ok=0.;
        while (requete1.next()) {
            Commentaire c;
            c = new Commentaire(requete1.getString("RCOMMENT"));
            int classe=0;
            for (String p : c.phrases) {
                classe += classifieur.classifier(new Phrase(p));
            }
            int rate=requete1.getInt("RATE");
            if((classe!=0 && (classe/Math.abs(classe)) == rate) || classe==rate){
                ok++;
                System.out.println("++++++++");

            }
            total++;
            System.out.println(total+"\t"+rate);
            System.out.println(ok+"\t"+classe);
            System.out.println(c);
        }
        requete1.close();
        lanceRequete1.close();
        ratio=ok / total;
        return ratio;
    }

    public static void main(String[] args) throws SQLException {

        Evaluer e;
        e = new Evaluer();
        System.out.println(e.getRatio());
    }
}
