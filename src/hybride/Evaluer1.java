package hybride;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author MerysPIEGAY
 */
public class Evaluer1 {

    Double ratio;
    Classifier classifieur;
    int pospos, negneg, neuneu, posneg, negpos, posneu, neupos, neuneg, negneu;
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
     * @return @throws SQLException
     */
    public void getRatio(String baseComBase, String baseComTest) throws SQLException {

        Connection co;
        co = new Connection();
        Statement lanceRequete1;
        lanceRequete1 = co.conn.createStatement();
        ResultSet requete1;
        requete1 = lanceRequete1.executeQuery("select * from " + baseComTest);
        while (requete1.next()) {
            Statement lanceRequete2;
            lanceRequete2 = co.conn.createStatement();
            ResultSet requete2;
            requete2 = lanceRequete2.executeQuery("select * from " + baseComBase
                    + " where ID_PHRASE = " + requete1.getInt("ID_PHRASE"));
            if (requete2.next()) {
                int classeTest = requete1.getInt("CLASSE");
                int classeBase = requete2.getInt("CLASSE");
                if (classeTest == classeBase) {
                    if (classeTest == 1) {
                        pospos++;
                    }
                    if (classeTest == 0) {
                        neuneu++;
                    }
                    if (classeTest == -1) {
                        negneg++;
                    }
                } else {
                    if (classeTest == 1 && classeBase == 0) {
                        neupos++;
                    }
                    if (classeTest == 0 && classeBase == 1) {
                        posneu++;
                    }
                    if (classeTest == -1 && classeBase == 0) {
                        neuneg++;
                    }
                    if (classeTest == 1 && classeBase == -1) {
                        negpos++;
                    }
                    if (classeTest == 0 && classeBase == -1) {
                        negneu++;
                    }
                    if (classeTest == -1 && classeBase == 1) {
                        posneg++;
                    }
                }

            }
            lanceRequete2.close();
            requete2.close();

        }
        requete1.close();
        lanceRequete1.close();
        int nbtot = pospos + neuneu + negneg + posneu + neupos + posneg + negpos + negneu + neuneg;
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
        
        System.out.format("Fscore pos  :\t %7.2f \t|%7.2f\t|%7.2f \n", (pospos / comPos), (neupos / comPos), (negpos / comPos));
        System.out.println("-------------------------------------------------------------------------------\n");
        System.out.format("Fscore neu : \t %7.2f \t|%7.2f\t|%7.2f \n", (posneu / comNeu), (neuneu / comNeu), (negneu / comNeu));
        System.out.println("-------------------------------------------------------------------------------\n");
        System.out.format("Fscore neg : \t %7.2f \t|%7.2f\t|%7.2f \n", (posneg / comNeg), (neuneg / comNeg), (negneg / comNeg));
        System.out.println("-------------------------------------------------------------------------------\n");

    }

    public static void main(String[] args) throws SQLException {

        Evaluer1 e;
        e = new Evaluer1();
        e.getRatio("PHRASE_NB", "PHRASE_HY_TEST");

    }
}
