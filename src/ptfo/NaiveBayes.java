/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

/**
 *
 * @author yougo
 */
public class NaiveBayes {

      float commPosit, commNegat, commTot;
      float note;
      float occPosit, occTotal, occNeg;
      Connection co1;

      NaiveBayes() throws SQLException {
            commPosit = commNegat = commTot = 0;
            co1 = new Connection();
      }

      /**
       * Va calculer le nombre de commentaires positifs, négatifs et neutres
       *
       * @throws SQLException
       */
      public void recupereNbrComm() throws SQLException {
            Statement lanceRequete1;
            lanceRequete1 = co1.conn.createStatement();
            ResultSet requete1;

            // récupération des CT et CP.
            requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE_BACKUP where CLASSE >= 1");
            requete1.next();
            commPosit = requete1.getInt(1);
            requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE_BACKUP where CLASSE >= -1");
            requete1.next();
            commNegat = requete1.getInt(1);
            requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE_BACKUP");
            requete1.next();
            commTot = requete1.getInt(1);
            requete1.close();
            lanceRequete1.close();
      }

      /**
       * Récupère le nombre d'occurence pour chaque mot de la phrase et en fait
       * l'addition
       *
       * @param phraseNote
       * @throws SQLException
       */
      public void recupereNbrOccur(String s) throws SQLException {
            Statement lanceRequeteOccur;
            lanceRequeteOccur = co1.conn.createStatement();
            ResultSet requeteOccur;
            occPosit = 0;
            occNeg = 0;
            occTotal = 0;

            requeteOccur = lanceRequeteOccur.executeQuery("select * from MOTSNB where MOTS='" + s + "'");
            if (requeteOccur.next()) {
                  occPosit = (float) requeteOccur.getInt("OCCUR_POSITIF");
                  occNeg = (float) requeteOccur.getInt("OCCUR_NEGATIF");
                  occTotal = (float) requeteOccur.getInt("OCCUR_TOTAL");
            }
            requeteOccur.close();

            lanceRequeteOccur.close();
      }

      /**
       * Va permettre de noter les mots si ils existent déjà dans la table si
       * ils ne sont pas présent dans la table ils sont rajoutés dedans
       *
       * @param phraseNB
       * @param classePhrase
       * @throws SQLException
       */
      public void apprentissageMots(Phrase phraseNB, int classePhrase) throws SQLException {

            for (String s : phraseNB.mots) {
                  Phrase motsDejaFait;
                  motsDejaFait = new Phrase("");
                  if (!s.equals("") && !motsDejaFait.mots.contains(s)) {
                        s = s.toLowerCase();
                        Statement lanceRequete2, lanceRequete3, lanceRequete4;
                        lanceRequete2 = co1.conn.createStatement();
                        lanceRequete3 = co1.conn.createStatement();
                        ResultSet requete2, requete3;
                        requete2 = lanceRequete2.executeQuery("select * from MOTSNB where MOTS = '" + s + "'");
                        requete3 = lanceRequete3.executeQuery("select max(ID) from MOTSNB");
                        requete3.next();
                        PreparedStatement ajoutMot;
                        ajoutMot = co1.conn.prepareStatement("insert into MOTSNB values (?,?,1,?,?,? )");
                        PreparedStatement insertMot;
                        insertMot = co1.conn.prepareStatement("update MOTSNB_TEST set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+1 where MOTS=?");
                        if (!requete2.next()) {
                              switch (classePhrase) {
                                    case -1:
                                          ajoutMot.setInt(1, requete3.getInt(1) + 1);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 0);
                                          ajoutMot.setInt(4, 1);
                                          ajoutMot.setInt(5, 0);
                                          ajoutMot.executeUpdate();
                                          break;
                                    case 0:
                                          ajoutMot.setInt(1, requete3.getInt(1) + 1);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 0);
                                          ajoutMot.setInt(4, 0);
                                          ajoutMot.setInt(5, 1);
                                          ajoutMot.executeUpdate();
                                          break;
                                    case 1:
                                          ajoutMot.setInt(1, requete3.getInt(1) + 1);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 1);
                                          ajoutMot.setInt(4, 0);
                                          ajoutMot.setInt(5, 0);
                                          ajoutMot.executeUpdate();
                                          break;
                                    default:
                                          System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                          break;
                              }
                              motsDejaFait.mots.add(s);
                              System.out.println(s);
                              ajoutMot.close();
                        } else {
                              if (!motsDejaFait.mots.contains(s)) {
                                    switch (classePhrase) {
                                          case -1:
                                                insertMot.setString(1, s);
                                                insertMot.executeUpdate();
                                                break;
                                          case 0:
                                                insertMot.setString(1, s);
                                                insertMot.executeUpdate();
                                                break;
                                          case 1:
                                                insertMot.setString(1, s);
                                                insertMot.executeUpdate();
                                                break;
                                          default:
                                                System.out.println("Problème dans le update / switch de apprentissageMots() ");
                                                break;
                                    }
                              }
                              motsDejaFait.mots.add(s);
                              System.out.println(s);
                              insertMot.close();
                        }
                        requete2.close();
                        lanceRequete2.close();
                        requete3.close();
                        lanceRequete3.close();
                  }
            }
      }

      public void apprentissageMotsTest(Phrase phraseNB, int classePhrase) throws SQLException {

            for (String s : phraseNB.mots) {
                  Phrase motsDejaFait;
                  motsDejaFait = new Phrase("");
                  if (!s.equals("") && !motsDejaFait.mots.contains(s)) {
                        s = s.toLowerCase();
                        PreparedStatement lanceRequeteAll = null;
                        PreparedStatement lanceRequeteID = null;
                        String selectAll = "select * from MOTSNB_TEST where MOTS =?";
                        lanceRequeteAll = co1.conn.prepareStatement(selectAll);
                        lanceRequeteAll.setString(1, s);
                        String selectID = "select max(ID) from MOTSNB_TEST";
                        lanceRequeteID = co1.conn.prepareStatement(selectID);
                        ResultSet requeteAll, requeteID;
                        requeteID = lanceRequeteID.executeQuery();
                        requeteID.next();
                        int ID = requeteID.getInt(1)+1;
                        requeteAll = lanceRequeteAll.executeQuery();
                        if (!requeteAll.next()) {
                              PreparedStatement ajoutMot;
                              String ajout;
                              ajout = "insert into MOTSNB_TEST values (?,?,1,?,?,? )";
                              ajoutMot = co1.conn.prepareStatement(ajout);
                              switch (classePhrase) {
                                    case -1:
                                          ajoutMot.setInt(1, ID);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 0);
                                          ajoutMot.setInt(4, 1);
                                          ajoutMot.setInt(5, 0);
                                          break;
                                    case 0:
                                          ajoutMot.setInt(1, ID + 1);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 0);
                                          ajoutMot.setInt(4, 0);
                                          ajoutMot.setInt(5, 1);
                                          break;
                                    case 1:
                                          ajoutMot.setInt(1, ID + 1);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 1);
                                          ajoutMot.setInt(4, 0);
                                          ajoutMot.setInt(5, 0);
                                          break;
                                    default:
                                          System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                          break;
                              }
                              ajoutMot.executeUpdate();
                              motsDejaFait.mots.add(s);
                              System.out.println(s);
                              ajoutMot.close();
                        } else {
                              if (!motsDejaFait.mots.contains(s)) {
                                    PreparedStatement insertMot;
                                    String insert = "update MOTSNB_TEST set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+?,OCCUR_NEUTRE = OCCUR_NEUTRE +?, OCCUR_POSITIF =OCCUR_POSITIF+? where MOTS=?";
                                    insertMot = co1.conn.prepareStatement(insert);
                                    switch (classePhrase) {
                                          case -1:
                                                insertMot.setInt(1, 1);
                                                insertMot.setInt(2, 0);
                                                insertMot.setInt(3, 0);
                                                insertMot.setString(4, s);
                                                break;
                                          case 0:
                                                insertMot.setInt(1, 0);
                                                insertMot.setInt(2, 1);
                                                insertMot.setInt(3, 0);
                                                insertMot.setString(4, s);
                                                break;
                                          case 1:
                                                insertMot.setInt(1, 0);
                                                insertMot.setInt(2, 0);
                                                insertMot.setInt(3, 1);
                                                insertMot.setString(4, s);
                                                break;
                                          default:
                                                System.out.println("Problème dans le update / switch de apprentissageMots() ");
                                                break;
                                    }
                                    insertMot.executeUpdate();
                                    insertMot.close();
                              }
                              motsDejaFait.mots.add(s);
                              System.out.println(s);
                        }
                        requeteAll.close();
                        lanceRequeteAll.close();
                        requeteID.close();
                        lanceRequeteID.close();
                  }
            }
      }

      /**
       * Va permettre de noter la première fois tous les mots
       *
       * @throws SQLException
       */
      public void premierApprentissageMots() throws SQLException {
            Statement lanceRequetePhrase;
            lanceRequetePhrase = co1.conn.createStatement();
            ResultSet requetePhrase;
            requetePhrase = lanceRequetePhrase.executeQuery("select * from PHRASE_BACKUP");
            while (requetePhrase.next()) {
                  Phrase phrase;
                  phrase = new Phrase(requetePhrase.getString("PHRASE"));
                  int classePhrase = requetePhrase.getInt("CLASSE");
                  //apprentissageMots(phrase, classePhrase);
                  apprentissageMotsTest(phrase, classePhrase);
            }
            requetePhrase.close();
            lanceRequetePhrase.close();
      }

      /**
       * Va calculer la fameuse note NB positive
       *
       * @param phrase
       * @return
       * @throws SQLException
       */
      public float calculNoteNBPositif(Phrase phrase) throws SQLException {
            float noteNB = 1;
            recupereNbrComm();

            for (String s : phrase.mots) {
                  s = s.toLowerCase();
                  recupereNbrOccur(s);
                  if (occPosit > 0) {
                        System.out.println(s);
                        noteNB *= (occPosit / commPosit);
                        System.out.println("nbr NB POSITIF Temp : " + noteNB);
                  }
            }
            noteNB *= commPosit / commTot;
            System.out.println("\t\tNombre NB POSITIF : " + noteNB);
            return noteNB;

      }

      /**
       * Va calculer la version NB négative
       *
       * @param phrase
       * @return
       * @throws SQLException
       */
      public float calculNoteNBNegatif(Phrase phrase) throws SQLException {
            float noteNB = 1;
            recupereNbrComm();
            for (String s : phrase.mots) {
                  s = s.toLowerCase();
                  recupereNbrOccur(s);
                  if (occNeg > 0) {
                        System.out.println(s);
                        noteNB *= (occNeg / commNegat);
                        System.out.println("nbr NB NEGATIF Temp : " + noteNB);
                  }
            }
            noteNB *= commNegat / commTot;
            System.out.println("\t\tNombre NB NEGATIF : " + noteNB);
            return noteNB;
      }

      /**
       * Mise à jou de toute une phrase avec mise à jour des mots dedans et
       * update de la table phrase.
       *
       * @param phrase
       * @throws SQLException
       */
      public void miseAJourPhrase(Phrase phrase) throws SQLException {
            int classe;
            Statement lanceRequeteID;
            lanceRequeteID = co1.conn.createStatement();

            ResultSet requeteID;
            requeteID = lanceRequeteID.executeQuery("select max(ID_PHRASE) from PHRASE_TEST");
            requeteID.next();
            int ID = requeteID.getInt(1) + 1;
            //classe = calculNoteNBPositif(phrase) >= 0.5 ? 1 : 0;
            float noteNbPos = calculNoteNBPositif(phrase);
            float noteNbNeg = calculNoteNBNegatif(phrase);
            if (noteNbPos != noteNbNeg) {
                  if (noteNbPos > noteNbNeg) {
                        classe = 1;
                  } else {
                        classe = -1;
                  }
            } else {
                  classe = 0;
            }
            System.out.println(noteNbPos > noteNbNeg ? "\n\n\t\t\t\t CLASSE POSITIVE" : "\t\t\t\t CLASSE NEGATIVE\n\n");
            PreparedStatement maj;
            maj = co1.conn.prepareStatement("insert into PHRASE_TEST values (?,?,?,0,?,?)");
            maj.setInt(1, ID);
            maj.setString(2, phrase.phrase);
            maj.setInt(3, classe);
            maj.setFloat(4, noteNbPos);
            maj.setFloat(5, noteNbNeg);
            maj.executeUpdate();
            maj.close();
            requeteID.close();
            lanceRequeteID.close();
            apprentissageMotsTest(phrase, classe);
      }

      /**
       * Va noter tout un commentaire rentré en argument
       *
       * @param comment
       * @throws SQLException
       */
      public void notationCommentaire(Commentaire comment) throws SQLException {
            /*note = 0;
             Statement lanceRequetePhrase;
             lanceRequetePhrase = co1.conn.createStatement();*/

            for (String s : comment.phrases) {
                  Phrase phrase = new Phrase(s);
                  miseAJourPhrase(phrase);
            }

            /*lanceRequetePhrase.close();*/
      }

      /**
       * @param args the command line arguments
       */
      public static void main(String[] args) throws SQLException {
            NaiveBayes nb;
            nb = new NaiveBayes();
            nb.premierApprentissageMots();
            /*
             Statement lanceRequeteTest;
             lanceRequeteTest = nb.co1.conn.createStatement();
             ResultSet requeteTest;
             requeteTest = lanceRequeteTest.executeQuery("select * from PHRASE_BACKUP");
             int i = 0;
             while (requeteTest.next() && i <= 5) {
             Phrase phraseDeTest;
             phraseDeTest = new Phrase(requeteTest.getString("PHRASE"));
             nb.miseAJourPhrase(phraseDeTest);
             i++;
             //phraseDeTest = new Phrase("Ce pneu est super dangereux");
             System.out.println(phraseDeTest);
             }*/

      }

}
