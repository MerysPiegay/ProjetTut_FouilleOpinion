/*
voici la classe qui permet l'apprentissage
 */
package hybride;

import java.sql.*;

public class Learning {

    Connection co;

    Learning() throws SQLException {
        //on initialise la connection
        co = new Connection();
    }
//cette methode permet de recupérer le lemme d'un mot dans notre table lexique si c'est un nom, adjectif, verbe ou adverbe.
    public String lemme(String mot) throws SQLException {
        Statement lanceRequete1;
        lanceRequete1 = co.conn.createStatement();
        ResultSet requete1;
        requete1 = lanceRequete1.executeQuery("select * from LEXIQUE where MOT = '"
                + mot + "' and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = '"
                + mot + "' )and (CGRAM = 'NOM' "
                + "or CGRAM = 'VER' "
                + "or CGRAM = 'ADV' or CGRAM = 'ADJ')  ");
        String lemme = "";
        if (requete1.next()) {
            lemme = requete1.getString("LEMME");
        }
        lanceRequete1.close();
        requete1.close();
        return lemme;
    }
    /*
    voici la méthode d'apprentissage elle prend en argument le nom de la table de phrases découpées et 
    notés ainsi que la table ou sont stocké les mots et leurs occurrences dans chaques classes.
    */
    public void apprendre(String baseComNote, String baseOccMot) throws SQLException {
        Statement lanceRequete1;
        lanceRequete1 = co.conn.createStatement();
        ResultSet requete1;
        //on recupère toutes les lignes de notre table
        requete1 = lanceRequete1.executeQuery("select * from " + baseComNote);
        while (requete1.next()) {
            Phrase phrase;
            //pour chaque ligne de la table, on va recuperer la phrase et la note qui lui a été donnée
            phrase = new Phrase(requete1.getString("PHRASE"));
            int classe = requete1.getInt("CLASSE");
            //si une négation est détectée dans la phrase, on inverse cette note
            if (phrase.detectNegation()) {
                classe *= -1;
            }
            for (int i = 0; i < phrase.mots.size(); i++) {
                String mot = phrase.mots.get(i);
                //pour chaque mot de chaque phrase, on récupère son lemme
                String lemme = lemme(mot);
                if (!lemme.equals("") && lemme.length() > 1) {
                    //on passe le mot en minuscule
                    mot = lemme.toLowerCase();
                    System.out.println("apprentissage de : " + mot);
                    Statement lanceRequete2;
                    lanceRequete2 = co.conn.createStatement();
                    ResultSet requete2;
                    //on récupère ensuite, si elle existe, la ligne correspondante au lemme obtenu précédemment 
                    requete2 = lanceRequete2.executeQuery("select * from " + baseOccMot + " where LEMME='" + mot + "'");
                
                    String col = "";
                    int numcol = 0;
                    //Ce switch permet de préparer le bonne attribut à incrémenter en fonction de la classe de la phrase
                    switch (classe) {
                        case 1:
                            col = "OCCUR_POS";
                            numcol = 1;
                            break;
                        case 0:
                            col = "OCCUR_NEU";
                            numcol = 2;
                            break;
                        case -1:
                            col = "OCCUR_NEG";
                            numcol = 3;
                            break;
                    }
                    //Si le lemme existe deja dans notre table de mots, on va uniquement mettre à jour la bonne colonne
                    if (requete2.next()) {
                        Statement lanceRequete3;
                        lanceRequete3 = co.conn.createStatement();
                        lanceRequete3.executeUpdate("update " + baseOccMot + " set " + col + "=" + col + "+1 where LEMME = '" + mot + "'");
                        lanceRequete3.close();
                    //sinon, on va créer la ligne
                    } else {
                        Statement lanceRequete4;
                        lanceRequete4 = co.conn.createStatement();
                        ResultSet requete4 = lanceRequete4.executeQuery("select max(ID_LEMME) from " + baseOccMot);
                        int idmax = 0;
                        if (requete4.next()) {
                            idmax = requete4.getInt(1)+1;
                        }
                        lanceRequete4.close();
                        PreparedStatement lanceRequete3;
                        lanceRequete3 = co.conn.prepareStatement("insert into " + baseOccMot + " values( " + idmax + ",'" + mot + "',?,?,?)");
                        lanceRequete3.setInt(1, 0);
                        lanceRequete3.setInt(2, 0);
                        lanceRequete3.setInt(3, 0);
                        lanceRequete3.setInt(numcol, 1);
                        lanceRequete3.executeUpdate();
                        lanceRequete3.close();
                    }
                    lanceRequete2.close();
                }
            }
        }
        lanceRequete1.close();
    }

    public static void main(String[] args) throws SQLException {
        Learning l;
        l = new Learning();
        //l.apprendre("PHRASE_NB", "MOTS_HY");
        l.apprendre("PHRASE_NB", "MOTS_HY");
    }

}
