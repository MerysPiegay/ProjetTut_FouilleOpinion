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

      float commPositif, commNegatif, commTotal, commNeutre;  // variable nous permettant de garde le nombre de commentaires de chaque type
      float occPositif, occTotal, occNegatif, occNeutre; // nombre d'apparition d'un mot pour chaque classe et sur tout une table
      Connection co1;

      NaiveBayes() throws SQLException {
            commPositif = commNegatif = commTotal = commNeutre = 0;
            co1 = new Connection();
      }

      /**
       * Récupère les occurences d'un mot passé en entrée (s)
       *
       * @param s
       * @throws SQLException
       */
      public void recupereNbrOccur(String s) throws SQLException {
            PreparedStatement lanceRequeteOccur;
            String selectMot = "select * from MOTSNB where MOTS=?";
            lanceRequeteOccur = co1.conn.prepareStatement(selectMot);
            lanceRequeteOccur.setString(1, s);
            ResultSet requeteOccur;
            occPositif = 0;
            occNegatif = 0;
            occNeutre = 0;
            occTotal = 0;

            requeteOccur = lanceRequeteOccur.executeQuery();
            if (requeteOccur.next()) {
                  occPositif = (float) requeteOccur.getInt("OCCUR_POSITIF");
                  occNegatif = (float) requeteOccur.getInt("OCCUR_NEGATIF");
                  occNeutre = (float) requeteOccur.getInt("OCCUR_NEUTRE");
                  occTotal = (float) requeteOccur.getInt("OCCUR_TOTAL");
            }
            requeteOccur.close();
            lanceRequeteOccur.close();
      }

      /**
       * Va permettre de noter les mots d'une phrase passé en entrée avec sa
       * classe si ils existent déjà dans la table si ils ne sont pas présent
       * dans la table ils sont rajoutés dedans
       *
       * Va nous servir pour le premier apprentissage et pour la mise à jour une
       * fois qu'on a classifié une phrase.
       *
       * MOTSNB : Table d'enregistrement des mots appris
       *
       * @param phraseNB
       * @param classePhrase
       * @throws SQLException
       */
      public void apprentissageMots(Phrase phraseNB, int classePhrase) throws SQLException {

            System.out.println(phraseNB);
            System.out.println("nbr comm positifs :" + commPositif);
            System.out.println("nbr comm neutre :" + commNeutre);
            System.out.println("nbr comm negatif :" + commNegatif);
            System.out.println("nbr comm total :" + commTotal);
            for (String s : phraseNB.mots) {
                  Phrase motsDejaFait; // On ne compte qu'une fois un mot par phrase donc cette phrase est là pour récupérer les mots notés et être sûr que si on le recroise il ne soit pas pris en compte une deuxième fois
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

      /**
       * Va permettre de noter la première fois tous les mots de toute une table
       * d'apprentissage. A faire tourner au début de chaque test ATTENTION :
       * RPHRASE est la table d'apprentissage, la changer si on veut changer de
       * table
       *
       * @throws SQLException
       */
      public void premierApprentissageMots() throws SQLException {
            PreparedStatement lanceRequetePhrase;
            String selectPhrase = "select * from RPHRASE";  // Table d'apprentissage
            lanceRequetePhrase = co1.conn.prepareStatement(selectPhrase);
            ResultSet requetePhrase;
            requetePhrase = lanceRequetePhrase.executeQuery();
            while (requetePhrase.next()) {
                  Phrase phrase;
                  phrase = new Phrase(requetePhrase.getString("PHRASE"));
                  int classePhrase = requetePhrase.getInt("CLASSE");
                  apprentissageMots(phrase, classePhrase);
                  miseAjourNbrComm(classePhrase);
            }
            requetePhrase.close();
            lanceRequetePhrase.close();
      }

      /**
       * Va calculer la fameuse note NB positive pour toute une phrase
       *
       * @param phrase
       * @return
       * @throws SQLException
       */
      public float calculNoteNBPositif(Phrase phrase) throws SQLException {
            float noteNB = 1;
            //boolean passage = false;
            for (String s : phrase.mots) {
                  s = s.toLowerCase();
                  recupereNbrOccur(s);
                  if (occPositif != 0) {
                        //passage = true;
                        System.out.println(s);
                        noteNB *= (occPositif / commPositif);
                        System.out.println("nbr NB POSITIF Temp : " + noteNB);
                  } else {
                        noteNB *= (1 / commPositif);
                  }
            }
            //if (passage) {
            noteNB *= commPositif / commTotal;
            System.out.println("\t\tNombre NB POSITIF : " + noteNB);
            // } else {
            //    noteNB = 0;
            // }
            return noteNB;
      }

      /**
       * Pareil que la précedente mais version NB négative
       *
       * @param phrase
       * @return
       * @throws SQLException
       */
      public float calculNoteNBNegatif(Phrase phrase) throws SQLException {
            float noteNB = 1;
            for (String s : phrase.mots) {
                  s = s.toLowerCase();
                  recupereNbrOccur(s);
                  if (occNegatif != 0) {
                        System.out.println(s);
                        noteNB *= (occNegatif / commNegatif);
                        System.out.println("nbr NB NEGATIF Temp : " + noteNB);
                  } else {
                        noteNB *= (1 / commNegatif);
                  }
            }
            noteNB *= commNegatif / commTotal;
            System.out.println("\t\tNombre NB NEGATIF : " + noteNB);
            return noteNB;
      }

      /**
       * Et la version pour le neutre !
       *
       * @param phrase
       * @return
       * @throws SQLException
       */
      public float calculNoteNBNeutre(Phrase phrase) throws SQLException {
            float noteNB = 1;
            for (String s : phrase.mots) {
                  s = s.toLowerCase();
                  recupereNbrOccur(s);
                  if (occNeutre != 0) {
                        System.out.println(s);
                        noteNB *= (occNeutre / commNeutre);
                        System.out.println("nbr NB NEUTRE Temp : " + noteNB);
                  } else {
                        noteNB *= (1 / commNeutre);
                  }
            }
            noteNB *= commNeutre / commTotal;
            System.out.println("\t\tNombre NB NEUTRE : " + noteNB);
            return noteNB;
      }

      /**
       * Mise à jou de toute une phrase avec mise à jour des mots dedans et
       * update de la table phrase. On passe en entrée l'ID de la phrase pour
       * être sûr que dans les test de comparaisons on ai les même ID pour les
       * phrase classifiées par l'algo et les phrase classifiées à la main
       *
       * @param phrase
       * @param id
       * @throws SQLException
       */
      public void miseAJourPhrase(Phrase phrase, int id) throws SQLException {
            int classe;
            float noteNbPos = calculNoteNBPositif(phrase);
            float noteNbNeg = calculNoteNBNegatif(phrase);
            float noteNbNeutre = calculNoteNBNeutre(phrase);
            if (noteNbPos > noteNbNeg) {
                  if (noteNbPos > noteNbNeutre) {
                        classe = 1;
                        System.out.println("\n\n\t\t\t\t CLASSE POSITIVE\n\n");
                  } else {
                        classe = 0;
                        System.out.println("\n\n\t\t\t\t CLASSE NEUTRE\n\n");
                  }
            } else {
                  if (noteNbNeg > noteNbNeutre) {
                        classe = -1;
                        System.out.println("\n\n\t\t\t\t CLASSE NEGATIVE\n\n");
                  } else {
                        classe = 0;
                        System.out.println("\n\n\t\t\t\t CLASSE NEUTRE\n\n");
                  }
            }
            PreparedStatement maj;
            String insertPhraseTest = "insert into PHRASE_NB_TEST values (?,?,?,0,?,?)";
            maj = co1.conn.prepareStatement(insertPhraseTest);
            maj.setInt(1, id);
            maj.setString(2, phrase.phrase);
            maj.setInt(3, classe);
            maj.setFloat(4, noteNbPos);
            maj.setFloat(5, noteNbNeg);
            maj.executeUpdate();
            maj.close();
            apprentissageMots(phrase, classe);
            miseAjourNbrComm(classe);
      }

      /**
       * Pour avoir le nombre de commentaires positifs neutres et négatifs On
       * met juste en entrée la classe et il incrémente les var globales
       *
       * @param classe
       */
      public void miseAjourNbrComm(int classe) {
            if (classe == 1) {
                  commPositif++;
            }
            if (classe == 0) {
                  commNeutre++;
            }
            if (classe == -1) {
                  commNegatif++;
            }
            commTotal++;
      }

      /**
       * @param args the command line arguments
       * @throws java.sql.SQLException
       */
      public static void main(String[] args) throws SQLException {
            NaiveBayes nb;
            nb = new NaiveBayes();
            nb.premierApprentissageMots();

            PreparedStatement lanceRequeteTest;
            String selectPhrase = "select * from RPHRASE"; // ATTENTION cette fois table de test ! ! 
            lanceRequeteTest = nb.co1.conn.prepareStatement(selectPhrase);
            ResultSet requeteTest;
            requeteTest = lanceRequeteTest.executeQuery();
            int i = 0;
            while (requeteTest.next()) {
                  Phrase phraseDeTest;
                  phraseDeTest = new Phrase(requeteTest.getString("PHRASE"));
                  int idPhrase = requeteTest.getInt("ID_PHRASE");
                  System.out.println(phraseDeTest);
                  nb.miseAJourPhrase(phraseDeTest, idPhrase);
            }
            requeteTest.close();
            lanceRequeteTest.close();
      }
}
