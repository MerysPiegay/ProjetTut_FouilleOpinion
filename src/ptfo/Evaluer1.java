 package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
*
* @author MerysPIEGAY
*/
public class Evaluer1 {

    Double ratio;
    Classifier classifieur;
    int fauxpos, fauxneg, fauxneu, nbphrase, vraipos, vraineg, vraineu;
    ArrayList<Integer> allfauxpos;

    /**
*
* @throws SQLException
*/
    public Evaluer1() throws SQLException {

        classifieur = new Classifier();
    }

    /**
*
* @return
* @throws SQLException
*/
    public Double getRatio() throws SQLException {
        allfauxpos = new ArrayList<>();
        fauxpos = 0;
        fauxneg = 0;
        fauxneu = 0;
        vraipos = 0;
        vraineg = 0;
        vraineu = 0;
        Connection co;
        co = new Connection();
        Statement lanceRequete1;
        lanceRequete1 = co.conn.createStatement();
        ResultSet requete1;
        requete1 = lanceRequete1.executeQuery("select * from PHRASE");
        Double total = 0.;
        Double ok = 0.;
        while (requete1.next()) {
            nbphrase++;
            Commentaire c;
            c = new Commentaire(requete1.getString("PHRASE"));
            int classe = 0;
            for (String p : c.phrases) {
                classe += classifieur.classifier(new Phrase(p));
                Statement lanceRequete2_2_1;
                lanceRequete2_2_1 = co.conn.createStatement();
                System.out.println(p.replace("'","''"));
                lanceRequete2_2_1.executeUpdate("update PHRASE set NOTE = "
                        + classe
                        + " where PHRASE = '"
                        + p.replace("'","''") + "'");
            }
            int rate = requete1.getInt("CLASSE");
            if ((classe != 0 && (classe / Math.abs(classe)) == rate) || classe == rate) {
                ok++;
                if (classe != 0 && classe / Math.abs(classe) > 0) {
                    vraipos++;
                } else {
                    if (classe != 0 && classe / Math.abs(classe) < 0) {
                        vraineg++;
                    } else {
                        vraineu++;
                    }
                }

            } else {
                switch (rate) {
                    case 1:
                        fauxpos++;
                        allfauxpos.add(requete1.getInt("ID_PHRASE"));
                        break;
                    case -1:
                        fauxneg++;
                        break;
                    case 0:
                        fauxneu++;
                        break;
                }
            }
            total++;
            System.out.println(total + "\t" + rate);
            System.out.println(ok + "\t" + classe);
            System.out.println(c);
        }
        requete1.close();
        lanceRequete1.close();
        ratio = ok / total;
        return ratio;
    }

    public static void main(String[] args) throws SQLException {

        Evaluer1 e;
        e = new Evaluer1();
        System.out.println(e.getRatio());

        System.out.println("total " + e.nbphrase);
        System.out.println("faux pos " + e.fauxpos);
        System.out.println("faux neu " + e.fauxneu);
        System.out.println("faux neg " + e.fauxneg);
        System.out.println("vrai pos " + e.vraipos);
        System.out.println("vrai neu " + e.vraineu);
        System.out.println("vrai neg " + e.vraineg);
        System.out.println(e.allfauxpos);
    }
}