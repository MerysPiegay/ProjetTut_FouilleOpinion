package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
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

            PreparedStatement lanceRequete3;
            String select = "select * from COMMENTAIRE_2";
            lanceRequete3 = conn.prepareStatement(select);

            ResultSet requete;
            requete = lanceRequete3.executeQuery();
            int rate = 0;
            String note = "";
            int update;
            boolean saisie_correcte;
            while (requete.next()) {
                  int total = 0;
                  System.out.println(requete.getString("CLASSE"));
                  boolean pertinent = false;
                  if (requete.getString("CLASSE") == null) {
                        Commentaire comment;
                        comment = new Commentaire(requete.getString("POSTE"));
                        for (String phrase : comment.phrases) {
                              System.out.println("\n\n-----------------------------------------------------------");
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
                                    pertinent = true;
                                    total += rate;
                                    phrase = phrase.replaceAll("'", "''");
                                    PreparedStatement lanceRequete4_0;
                                    String selectMaxID = "select max(ID_PHRASE) from PHRASE_DEMO";
                                    lanceRequete4_0 = conn.prepareStatement(selectMaxID);
                                    ResultSet requete4_0;
                                    requete4_0 = lanceRequete4_0.executeQuery();
                                    requete4_0.next();
                                    int id = requete4_0.getInt(1) + 1;
                                    lanceRequete4_0.close();
                                    requete4_0.close();
                                    PreparedStatement lanceRequete4;
                                    String insert = "insert into PHRASE_DEMO values(?,?,?,?)";
                                    lanceRequete4 = conn.prepareStatement(insert);
                                    lanceRequete4.setInt(1, id);
                                    lanceRequete4.setString(2, phrase);
                                    lanceRequete4.setInt(3, rate);
                                    lanceRequete4.setInt(4, requete.getInt("ID_POSTE"));
                                    update = lanceRequete4.executeUpdate();
                              }

                        }
                        PreparedStatement lanceRequete5;
                        PreparedStatement lanceRequete6;
                        String update1 = "update COMMENTAIRE_2 SET CLASSE=? where ID_POSTE =?";
                        lanceRequete5 = conn.prepareStatement(update1);
                        lanceRequete5.setString(1, String.valueOf(total));
                        lanceRequete5.setInt(2, requete.getInt("ID_POSTE"));
                        String update2 = "update COMMENTAIRE_2 SET CLASSE='np' where ID_POSTE =? ";
                        lanceRequete6 = conn.prepareStatement(update2);
                        lanceRequete6.setInt(1, requete.getInt("ID_POSTE"));
                        if (pertinent) {
                              lanceRequete5.executeUpdate();
                        } else {
                              lanceRequete6.executeUpdate();
                        }
                        lanceRequete5.close();
                        lanceRequete6.close();
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
