package ptfo;

import static java.lang.System.exit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CommentaireManip {

    String userid;
    String password;
    String URL;
    java.sql.Connection conn;

    CommentaireManip() throws SQLException {
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

 
    public void noteCommentaire() throws SQLException {
        Scanner s;

        Statement lanceRequete3;
        lanceRequete3 = conn.createStatement();
        Statement lanceRequete4;
        lanceRequete4 = conn.createStatement();
        ResultSet requete;
        requete = lanceRequete3.executeQuery("select * from COMMENTAIRE");
        int rate = 0;
        int update;
        boolean saisie_correcte;
        while (requete.next()) {
            System.out.println(requete.getString("CLASSE"));

            if (requete.getString("CLASSE") == null) {
                System.out.println(requete.getString("POSTE"));
                do {
                    saisie_correcte = true;
                    System.out.println("entrez la note du commentaire entre -1 et 1 :");
                    s = new Scanner(System.in);
                    try {
                        rate = s.nextInt();
                        saisie_correcte = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Erreur de saisie\n");
                        saisie_correcte = false;
                    }
                    s.nextLine();
                } while (!saisie_correcte);
                if (rate <= 1 && rate >= -1) {
                    update = lanceRequete4.executeUpdate("update COMMENTAIRE set CLASSE ="
                            + rate
                            + " where ID_POSTE = " + requete.getString("ID_POSTE"));
                } else {
                    requete.close();
                    exit(0);
                }
            }
        }
        requete.close();
    }

  

    public static void main(String[] args) throws SQLException {
        CommentaireManip h;
        h = new CommentaireManip();
        h.noteCommentaire();

    }

}
