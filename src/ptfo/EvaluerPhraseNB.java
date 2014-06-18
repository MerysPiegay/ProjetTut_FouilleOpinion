package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author MerysPIEGAY
 */
public class EvaluerPhraseNB {

    Double ratio;
    ClassifierPhrase_NB classifieur;
    int fauxpos, fauxneg, fauxneu, nbphrase, vraipos, vraineg, vraineu;
    ArrayList<Integer> allfauxneu;

    /**
     *
     * @throws SQLException
     */
    public EvaluerPhraseNB() throws SQLException {

        classifieur = new ClassifierPhrase_NB();
    }

    /**
     *
     * @return @throws SQLException
     */
    public Double getRatio() throws SQLException {
        allfauxneu = new ArrayList<>();
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
        requete1 = lanceRequete1.executeQuery("select * from PHRASE_DEMO");
        Double total = 0.;
        Double ok = 0.;
        while (requete1.next()) {
            Phrase p;
            p = new Phrase(requete1.getString("PHRASE"));
            double classe;
            classe = classifieur.classifier(p);
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
                        break;
                    case -1:
                        fauxneg++;
                        break;
                    case 0:
                        fauxneu++;
                        allfauxneu.add(requete1.getInt("ID_COMMENTAIRE"));
                        break;
                }
            }
            Statement lanceRequete2_2_1;
            lanceRequete2_2_1 = co.conn.createStatement();

            Statement lanceRequete2_2_2;
            lanceRequete2_2_2 = co.conn.createStatement();
            ResultSet requete2_2_2;
            requete2_2_2 = lanceRequete2_2_2.executeQuery("select count(ID_PHRASE) from PHRASE_HY_TEST_2");
            int idmax=0;
            if(requete2_2_2.next())idmax=requete2_2_2.getInt(1);
            lanceRequete2_2_1.executeUpdate("insert into PHRASE_HY_TEST_2 "
                    + "values ("+idmax+",'" + p.phrase + "'," + rate + "," + requete1.getInt("ID_COMMENTAIRE") + "," + classe + ")");
            lanceRequete2_2_1.close();

            total++;
            System.out.println(total + "\t" + rate);
            System.out.println(ok + "\t" + classe);
            System.out.println(p);
        }
        requete1.close();
        lanceRequete1.close();
        ratio = ok / total;
        return ratio;
    }

    public static void main(String[] args) throws SQLException {

        EvaluerPhraseNB e;
        e = new EvaluerPhraseNB();
        System.out.println("\033[32m" + e.getRatio());

        System.out.println("total " + e.nbphrase);
        System.out.println("faux pos " + e.fauxpos);
        System.out.println("faux neu " + e.fauxneu);
        System.out.println("faux neg " + e.fauxneg);
        System.out.println("vrai pos " + e.vraipos);
        System.out.println("vrai neu " + e.vraineu);
        System.out.println("vrai neg " + e.vraineg);
        Collections.sort(e.allfauxneu);
        System.out.println(e.allfauxneu);
    }
}
