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
 * @author yougo
 */
public class NaiveBayes {

      String userid;
      String password;
      String URL;
      java.sql.Connection conn;

      NaiveBayes() throws SQLException {
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

      void noter() throws SQLException {
            double note;
            Statement lanceRequete1;
            lanceRequete1 = conn.createStatement();
            ResultSet requete1;
            requete1 = lanceRequete1.executeQuery("select * from PHRASE");
            while (requete1.next()) {
                  Phrase phrase = new Phrase(requete1.getString("PHRASE"));
                  requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE where CLASSE = 1");
                  int commPosit = requete1.getInt(0);
                  requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE where CLASSE = -1");
                  int commNegat = requete1.getInt(0);
                  requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE");
                  int commTot = requete1.getInt(0);
                  System.out.println(commPosit);
                  System.out.println(commNegat);
                  System.out.println(commTot);
            }
            requete1.close();
            lanceRequete1.close();

      }

      /**
       * @param args the command line arguments
       */
      public static void main(String[] args) throws SQLException {
            NaiveBayes nb;
            nb = new NaiveBayes();
            nb.noter();
      }

}
