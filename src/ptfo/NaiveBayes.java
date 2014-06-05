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
                        lanceRequete4 = co1.conn.createStatement();
                        ResultSet requete2, requete3;
                        requete2 = lanceRequete2.executeQuery("select * from MOTSNB where MOTS = '" + s + "'");
                        requete3 = lanceRequete3.executeQuery("select max(ID) from MOTSNB");
                        requete3.next();
                        if (!requete2.next()) {

                              switch (classePhrase) {
                                    case -1:
                                          lanceRequete4.executeUpdate("insert into MOTSNB values (" + (requete3.getInt(1) + 1) + ",'" + s + "',1,0,1,0 )");
                                          break;
                                    case 0:
                                          lanceRequete4.executeUpdate("insert into MOTSNB values (" + (requete3.getInt(1) + 1) + ",'" + s + "',1,0,0,1 )");
                                          break;
                                    case 1:
                                          lanceRequete4.executeUpdate("insert into MOTSNB values (" + (requete3.getInt(1) + 1) + ",'" + s + "',1,1,0,0 )");
                                          break;
                                    default:
                                          System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                          break;
                              }
                              motsDejaFait.mots.add(s);
                              System.out.println(s);
                        } else {
                              if (!motsDejaFait.mots.contains(s)) {
                                    switch (classePhrase) {
                                          case -1:
                                                lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+1 where MOTS='" + s + "' ");
                                                break;
                                          case 0:
                                                lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEUTRE = OCCUR_NEUTRE+1 where MOTS='" + s + "' ");
                                                break;
                                          case 1:
                                                lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_POSITIF =  OCCUR_POSITIF+1 where MOTS='" + s + "' ");
                                                break;
                                          default:
                                                System.out.println("Problème dans le update / switch de apprentissageMots() ");
                                                break;
                                    }
                                    System.out.println(s);
                              }
                        }
                        requete2.close();
                        lanceRequete2.close();
                        requete3.close();
                        lanceRequete3.close();
                        lanceRequete4.close();
                  }
            }
      }

      public void apprentissageMotsTest(Phrase phraseNB, int classePhrase) throws SQLException {

            for (String s : phraseNB.mots) {
                  Phrase motsDejaFait;
                  motsDejaFait = new Phrase("");
                  if (!s.equals("") && !motsDejaFait.mots.contains(s)) {
                        s = s.toLowerCase();
                        Statement lanceRequete2, lanceRequete3, lanceRequete4;
                        lanceRequete2 = co1.conn.createStatement();
                        lanceRequete3 = co1.conn.createStatement();
                        lanceRequete4 = co1.conn.createStatement();
                        ResultSet requete2, requete3;
                        requete2 = lanceRequete2.executeQuery("select * from MOTSNB_TEST where MOTS = '" + s + "'");
                        requete3 = lanceRequete3.executeQuery("select max(ID) from MOTSNB_TEST");
                        requete3.next();
                        if (!requete2.next()) {
                              switch (classePhrase) {
                                    case -1:
                                          lanceRequete4.executeUpdate("insert into MOTSNB_TEST values (" + (requete3.getInt(1) + 1) + ",'" + s + "',1,0,1,0 )");
                                          break;
                                    case 0:
                                          lanceRequete4.executeUpdate("insert into MOTSNB_TEST values (" + (requete3.getInt(1) + 1) + ",'" + s + "',1,0,0,1 )");
                                          break;
                                    case 1:
                                          lanceRequete4.executeUpdate("insert into MOTSNB_TEST values (" + (requete3.getInt(1) + 1) + ",'" + s + "',1,1,0,0 )");
                                          break;
                                    default:
                                          System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                          break;
                              }
                              motsDejaFait.mots.add(s);
                              System.out.println(s);
                        } else {
                              if (!motsDejaFait.mots.contains(s)) {
                                    switch (classePhrase) {
                                          case -1:
                                                lanceRequete4.executeUpdate("update MOTSNB_TEST set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+1 where MOTS='" + s + "' ");
                                                break;
                                          case 0:
                                                lanceRequete4.executeUpdate("update MOTSNB_TEST set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEUTRE = OCCUR_NEUTRE+1 where MOTS='" + s + "' ");
                                                break;
                                          case 1:
                                                lanceRequete4.executeUpdate("update MOTSNB_TEST set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_POSITIF =  OCCUR_POSITIF+1 where MOTS='" + s + "' ");
                                                break;
                                          default:
                                                System.out.println("Problème dans le update / switch de apprentissageMots() ");
                                                break;
                                    }
                              }
                              motsDejaFait.mots.add(s);
                              System.out.println(s);
                        }
                        requete2.close();
                        lanceRequete2.close();
                        requete3.close();
                        lanceRequete3.close();
                        lanceRequete4.close();
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
                  apprentissageMots(phrase, classePhrase);
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
                        System.out.println("nbr O POSITIF : " + occPosit);
                        noteNB *= (occPosit /commPosit);
                        System.out.println("nbr NB Temp : " + noteNB);
                  }
            } 
            noteNB *=commPosit/commTot;
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
                        System.out.println("nbr O NEGATIF : " + occNeg);
                        noteNB *= (occNeg / commNegat);
                        System.out.println("nbr NB Temp : " + noteNB);
                  }
            }
            noteNB *= commNegat/commTot;
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
            Statement lanceRequeteMaJ, lanceRequeteID;
            lanceRequeteMaJ = co1.conn.createStatement();
            lanceRequeteID = co1.conn.createStatement();
            ResultSet requeteID;
            requeteID = lanceRequeteID.executeQuery("select max(ID_PHRASE) from PHRASE_TEST");
            requeteID.next();
            int ID = requeteID.getInt(1) + 1;
            classe = calculNoteNBPositif(phrase) >= 0.5 ? 1 : 0;
            float notenb = calculNoteNBPositif(phrase);
            if (classe == 0 && calculNoteNBNegatif(phrase) >= 0.5) {
                  classe = -1;
                  notenb = calculNoteNBNegatif(phrase);
            }

            lanceRequeteMaJ.executeUpdate("insert into PHRASE_TEST values (" + ID + ",'" + phrase.phrase + "'," + classe + ",0," + notenb + ")");
            // penser à remplacer les deux ? mais pour le moment pas besoin
            requeteID.close();
            lanceRequeteID.close();
            lanceRequeteMaJ.close();

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

      /*
       public Phrase  recupPhrasePourTest() throws SQLException{
       Statement lanceRequeteTest;
       lanceRequeteTest = co1.conn.createStatement();
       ResultSet requeteTest;
       requeteTest = lanceRequeteTest.executeQuery("select PHRASE from PHRASE_BACKUP");
       int i=0;
       while (requeteTest.next() && i <= 10){
       Phrase p;
       p =new Phrase (requeteTest.getString("PHRASE"));
       i ++;
       return p;
       }
       }*/
      /**
       * @param args the command line arguments
       */
      public static void main(String[] args) throws SQLException {
            NaiveBayes nb;
            nb = new NaiveBayes();
             //nb.premierApprentissageMots();

           /*Statement lanceRequeteTest;
            lanceRequeteTest = nb.co1.conn.createStatement();
            ResultSet requeteTest;
            requeteTest = lanceRequeteTest.executeQuery("select * from PHRASE");
            int i = 0;
            while (requeteTest.next() && i <= 5) {*/
                  
                  Phrase phraseDeTest;
                 // phraseDeTest = new Phrase(requeteTest.getString("PHRASE"));
                  //nb.miseAJourPhrase(phraseDeTest);
                  //i++;
                  
                  phraseDeTest = new Phrase("Ce pneu est super dangereux");
                  System.out.println(phraseDeTest);
                  float NBPos = nb.calculNoteNBPositif(phraseDeTest);
                  
                  float NBNeg = nb.calculNoteNBNegatif(phraseDeTest);
                   System.out.println("\t\tNombre NB positif : " + NBPos );
                  System.out.println("\t\tNombre NB negatif : " + NBNeg);
                  System.out.println (NBPos>NBNeg?"\n\n\t\t\t\t CLASSE POSITIVE":"\t\t\t\t CLASSE NEGATIVE\n\n");
           // }

      }

}
