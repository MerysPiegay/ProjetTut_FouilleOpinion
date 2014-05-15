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
        String note="";
        int update;
        boolean saisie_correcte;
        while (requete.next()) {
            System.out.println(requete.getString("CLASSE"));

            if (requete.getString("CLASSE") == null) {
                Commentaire comment;
                comment = new Commentaire(requete.getString("POSTE"));
                for ( String phrase : comment.phrases) {
                    System.out.println(phrase);
                    do {
                        saisie_correcte = true;
                        System.out.println("entrez la note du commentaire entre n,p,b,i :");
                        s = new Scanner(System.in);
                        
                        if(note.equalsIgnoreCase("n")||note.equalsIgnoreCase("p")||note.equalsIgnoreCase("b")||note.equalsIgnoreCase("i")){
                            rate = s.nextInt();
                            saisie_correcte = true;
                        } else {
                            System.out.println("Erreur de saisie\n");
                            saisie_correcte = false;
                        }
                        s.nextLine();
                    } while (!saisie_correcte);
                    switch(note){
                        case "n":
                            rate=-1;
                            break;
                        case "p":
                            rate=1;
                            break;
                        case "b":
                            rate=0;
                            break;
                        case "i":
                            rate=-2;
                            break;
                    }
                    if (rate <= 1 && rate >= -1 ) {
                        update = lanceRequete4.executeUpdate("INSERT INTO PHRASE (PHRASE,CLASSE,ID_COMMENTAIRE)"+" VALUES(" +phrase +","+rate+"," +requete.getString("ID_POSTE)"));
                    } else {
                        requete.close();
                        exit(0);
                    }
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
