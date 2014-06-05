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
        String note = "";
        int update;
        boolean saisie_correcte;
        while (requete.next()) {
            int total = 0;
            System.out.println(requete.getString("CLASSE"));
            boolean pertinent=false;
            if (requete.getString("CLASSE") == null) {
                Commentaire comment;
                comment = new Commentaire(requete.getString("POSTE"));
                for (String phrase : comment.phrases) {
                    System.out.println(phrase);
                    saisie_correcte = true;
                    System.out.println("\nentrez la note du commentaire entre n,p,b,i :");
                    do {
                        s = new Scanner(System.in);
                        note = s.nextLine();
                        switch (note) {
                            case "n":
                                rate = -1;
                                saisie_correcte = true;
                                break;
                            case "p":
                                rate = 1;
                                saisie_correcte = true;
                                break;
                            case "b":
                                rate = 0;
                                saisie_correcte = true;
                                break;
                            case "i":
                                rate = -2;
                                saisie_correcte = true;
                                break;
                            default:
                                System.out.println("erreur de notation");
                                saisie_correcte = false;
                                break;
                        }
                    } while (!saisie_correcte);
                    if (rate <= 1 && rate >= -1) {
                        pertinent=true;
                        total += rate;
                        phrase = phrase.replaceAll("'", "''");
                        Statement lanceRequete4_0;
                        lanceRequete4_0 = conn.createStatement();
                        ResultSet requete4_0;
                        requete4_0 = lanceRequete4_0.executeQuery("select max(ID_PHRASE) from PHRASE_BACKUP");
                        requete4_0.next();
                        int id = requete4_0.getInt(1) + 1;
                        lanceRequete4_0.close();
                        requete4_0.close();
                        update = lanceRequete4.executeUpdate("INSERT INTO PHRASE_BACKUP"
                                + ""
                                + " VALUES("
                                + id
                                + ",'" + phrase + "',"
                                + rate + ","
                                + requete.getInt("ID_POSTE")
                                + ")");
                    }

                }
                Statement lanceRequete5;
                lanceRequete5 = conn.createStatement();
                if(pertinent){
                    lanceRequete5.executeUpdate("update COMMENTAIRE SET CLASSE=" + String.valueOf(total) + "where ID_POSTE = " + requete.getInt("ID_POSTE"));
                }else{
                    lanceRequete5.executeUpdate("update COMMENTAIRE SET CLASSE='np' where ID_POSTE = " + requete.getInt("ID_POSTE"));
                }
                lanceRequete5.close();
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
