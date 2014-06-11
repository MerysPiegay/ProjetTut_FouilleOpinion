/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
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
            PreparedStatement lanceRequete1;
            String selectCount = "select count(PHRASE) from PHRASE_NB where CLASSE >=?";
            String selectCountTotal = "select count(PHRASE) from PHRASE_NB";
            lanceRequete1 = co1.conn.prepareStatement(selectCount);
            ResultSet requete1;

            // récupération des CT et CP.
            lanceRequete1.setInt(1, 1);
            requete1 = lanceRequete1.executeQuery();
            requete1.next();
            commPosit = requete1.getInt(1);
            lanceRequete1.setInt(1, -1);
            requete1 = lanceRequete1.executeQuery();
            requete1.next();
            commNegat = requete1.getInt(1);
            lanceRequete1 = co1.conn.prepareStatement(selectCountTotal);
            requete1 = lanceRequete1.executeQuery();
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
            PreparedStatement lanceRequeteOccur;
            String selectMot = "select * from MOTSNB where MOTS=?";
            lanceRequeteOccur = co1.conn.prepareStatement(selectMot);
            lanceRequeteOccur.setString(1, s);
            ResultSet requeteOccur;
            occPosit = 0;
            occNeg = 0;
            occTotal = 0;

            requeteOccur = lanceRequeteOccur.executeQuery();
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
                        System.out.println(s);
                        PreparedStatement lanceRequeteAll;
                        PreparedStatement lanceRequeteID;
                        String selectAll = "select * from MOTSNB where MOTS =?";
                        lanceRequeteAll = co1.conn.prepareStatement(selectAll);
                        lanceRequeteAll.setString(1, s);
                        String selectID = "select max(ID) from MOTSNB";
                        lanceRequeteID = co1.conn.prepareStatement(selectID);
                        ResultSet requeteAll, requeteID;
                        requeteID = lanceRequeteID.executeQuery();
                        requeteID.next();
                        int ID = requeteID.getInt(1) + 1;
                        requeteAll = lanceRequeteAll.executeQuery();
                        if (!requeteAll.next()) {
                              PreparedStatement ajoutMot;
                              String ajout = "insert into MOTSNB values (?,?,1,?,?,? )";
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
                                          ajoutMot.setInt(1, ID);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 0);
                                          ajoutMot.setInt(4, 0);
                                          ajoutMot.setInt(5, 1);
                                          break;
                                    case 1:
                                          ajoutMot.setInt(1, ID);
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
                              ajoutMot.close();
                        } else {
                              if (!motsDejaFait.mots.contains(s)) {
                                    PreparedStatement insertMot;
                                    String insert = "update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+?,OCCUR_NEUTRE = OCCUR_NEUTRE +?, OCCUR_POSITIF =OCCUR_POSITIF+? where MOTS=?";
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
                        }
                        requeteAll.close();
                        lanceRequeteAll.close();
                        requeteID.close();
                        lanceRequeteID.close();
                  }
            }
      }

      public void apprentissageMotsTest(Phrase phraseNB, int classePhrase) throws SQLException {

            for (String s : phraseNB.mots) {
                  Phrase motsDejaFait;
                  motsDejaFait = new Phrase("");
                  if (!s.equals("") && !motsDejaFait.mots.contains(s)) {
                        s = s.toLowerCase();
                        System.out.println(s);
                        PreparedStatement lanceRequeteAll;
                        PreparedStatement lanceRequeteID;
                        String selectAll = "select * from MOTSNB_TEST where MOTS =?";
                        lanceRequeteAll = co1.conn.prepareStatement(selectAll);
                        lanceRequeteAll.setString(1, s);
                        String selectID = "select max(ID) from MOTSNB_TEST";
                        lanceRequeteID = co1.conn.prepareStatement(selectID);
                        ResultSet requeteAll, requeteID;
                        requeteID = lanceRequeteID.executeQuery();
                        requeteID.next();
                        int ID = requeteID.getInt(1) + 1;
                        requeteAll = lanceRequeteAll.executeQuery();
                        if (!requeteAll.next()) {
                              PreparedStatement ajoutMot;
                              String ajout = "insert into MOTSNB_TEST values (?,?,1,?,?,? )";
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
                                          ajoutMot.setInt(1, ID);
                                          ajoutMot.setString(2, s);
                                          ajoutMot.setInt(3, 0);
                                          ajoutMot.setInt(4, 0);
                                          ajoutMot.setInt(5, 1);
                                          break;
                                    case 1:
                                          ajoutMot.setInt(1, ID);
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
            PreparedStatement lanceRequetePhrase;
            String selectPhrase = "select * from PHRASE_NB";
            lanceRequetePhrase = co1.conn.prepareStatement(selectPhrase);
            ResultSet requetePhrase;
            requetePhrase = lanceRequetePhrase.executeQuery();
            while (requetePhrase.next()) {
                  Phrase phrase;
                  phrase = new Phrase(requetePhrase.getString("PHRASE"));
                  int classePhrase = requetePhrase.getInt("CLASSE");
                  apprentissageMots(phrase, classePhrase);
                  //apprentissageMotsTest(phrase, classePhrase);
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
            PreparedStatement lanceRequeteID;
            String selectMaxID = "select max(ID_PHRASE) from PHRASE_NB_TEST";
            lanceRequeteID = co1.conn.prepareStatement(selectMaxID);
            ResultSet requeteID;
            requeteID = lanceRequeteID.executeQuery();
            requeteID.next();
            int ID = requeteID.getInt(1) + 1;
            float noteNbPos = calculNoteNBPositif(phrase);
            float noteNbNeg = calculNoteNBNegatif(phrase);
            if (noteNbPos != noteNbNeg) {
                  classe = noteNbPos > noteNbNeg ? 1 : -1;
            } else {
                  classe = 0;
            }
            System.out.println(noteNbPos > noteNbNeg ? "\n\n\t\t\t\t CLASSE POSITIVE" : "\t\t\t\t CLASSE NEGATIVE\n\n");
            PreparedStatement maj;
            String insertPhraseTest = "insert into PHRASE_NB_TEST values (?,?,?,0,?,?)";
            maj = co1.conn.prepareStatement(insertPhraseTest);
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
       * @throws java.sql.SQLException
       */
      public static void main(String[] args) throws SQLException {
            NaiveBayes nb;
            nb = new NaiveBayes();
            //nb.premierApprentissageMots();

            PreparedStatement lanceRequeteTest;
            String selectPhraseBackup = "select * from PHRASE_NB_BACKUP";
            lanceRequeteTest = nb.co1.conn.prepareStatement(selectPhraseBackup);
            ResultSet requeteTest;
            requeteTest = lanceRequeteTest.executeQuery();
            int i = 0;
            while (requeteTest.next() && i <= 5) {
                  Phrase phraseDeTest;
                  phraseDeTest = new Phrase(requeteTest.getString("PHRASE"));
                  System.out.println(phraseDeTest);
                  nb.miseAJourPhrase(phraseDeTest);
                  i++;
                  //phraseDeTest = new Phrase("Ce pneu est super dangereux");
            }
      }

}
