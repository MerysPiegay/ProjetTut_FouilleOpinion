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

      int commPosit, commNegat, commTot;
      float note;
      int occPosit, occTotal, occNeg;
      Connection co1;

      NaiveBayes() throws SQLException {
            commPosit = commNegat = commTot = 0;
            co1 = new Connection();
      }
      /**
       * Va calculer le nombre de commentaires positifs, négatifs et neutres
       * @throws SQLException 
       */
      public void recupereNbrComm() throws SQLException {
            Statement lanceRequete1;
            lanceRequete1 = co1.conn.createStatement();
            ResultSet requete1;

            // récupération des CT et CP.
            requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE where CLASSE >= 1");
            requete1.next();
            commPosit = requete1.getInt(0);
            requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE where CLASSE >= -1");
            requete1.next();
            commNegat = requete1.getInt(0);
            requete1 = lanceRequete1.executeQuery("select count(PHRASE) from PHRASE");
            requete1.next();
            commTot = requete1.getInt(0);
            requete1.close();
            lanceRequete1.close();
      }

      /**
       *  Va permettre de noter les mots si ils existent déjà dans
       * la table si ils ne sont pas présent dans la table ils sont rajoutés
       * dedans
       * @param phraseNB
       * @param classePhrase
       * @throws SQLException 
       */
      public void apprentissageMots(Phrase phraseNB, int classePhrase) throws SQLException {

                  for (String s : phraseNB.mots) {
                        Statement lanceRequete2, lanceRequete3, lanceRequete4;
                        lanceRequete2 = co1.conn.createStatement();
                        lanceRequete3 = co1.conn.createStatement();
                        lanceRequete4 = co1.conn.createStatement();
                        ResultSet requete2, requete3;
                        requete2 = lanceRequete2.executeQuery("select * from MOTSNB where MOTS = s ");
                        requete3 = lanceRequete3.executeQuery("select max(ID) from MOTSNB");
                        if (!requete2.next()) {

                              switch (classePhrase) {
                                    case -1:
                                          lanceRequete4.executeUpdate("insert into MOTSNB values (" + (requete3.getInt(0) + 1) + "," + s + ",1,0,1,0 ");
                                          break;
                                    case 0:
                                          lanceRequete4.executeUpdate("insert into MOTSNB values (" + (requete3.getInt(0) + 1) + "," + s + ",1,0,0,1 ");
                                          break;
                                    case 1:
                                          lanceRequete4.executeUpdate("insert into MOTSNB values (" + (requete3.getInt(0) + 1) + "," + s + ",1,1,0,0 ");
                                          break;
                                    default:
                                          System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                          break;
                              }
                        } else {
                              switch (classePhrase) {
                                    case -1:
                                          lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+1 ");
                                          break;
                                    case 0:
                                          lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEUTRE = OCCUR_NEUTRE+1 ");
                                          break;
                                    case 1:
                                          lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_POSITIF =  OCCUR_POSITIF+1 ");
                                          break;
                                    default:
                                          System.out.println("Problème dans le update / switch de apprentissageMots() ");
                                          break;
                              }
                        }
                        requete2.close();
                        lanceRequete2.close();
                        requete3.close();
                        lanceRequete3.close();
                        lanceRequete4.close();
                  }
            }
      
      /**
       * Va permettre de noter la première fois tous les mots
       * @throws SQLException 
       */
      public void premierApprentissageMots() throws SQLException{
            Statement lanceRequetePhrase;
            lanceRequetePhrase = co1.conn.createStatement();
            ResultSet requetePhrase;
            requetePhrase = lanceRequetePhrase.executeQuery("select * from PHRASE");
            while(requetePhrase.next()){
                  Phrase phrase;
                  phrase = new Phrase (requetePhrase.getString("PHRASE"));
                  int classePhrase = requetePhrase.getInt("CLASSE");
                  apprentissageMots(phrase, classePhrase);
            }
            requetePhrase.close();
            lanceRequetePhrase.close();
      }

      /**
       * Récupère le nombre d'occurence pour chaque mot de la phrase et en fait
       * l'addition
       *
       * @param phraseNote
       * @throws SQLException
       */
       public void recupereNbrOccur(Phrase phraseNote) throws SQLException {
            Statement lanceRequeteOccur;
            lanceRequeteOccur = co1.conn.createStatement();
            ResultSet requeteOccur;
            occPosit = 0;
            occNeg = 0;
            occTotal = 0;

            for (String s : phraseNote.mots) {
                  requeteOccur = lanceRequeteOccur.executeQuery("select * from MOTSNB where MOTS=" + s);
                  requeteOccur.next();
                  occPosit += requeteOccur.getInt("OCCUR_POSITIF");
                  occNeg += requeteOccur.getInt("OCCUR_NEGATIF");
                  occTotal += requeteOccur.getInt("OCCUR_TOTAL");
                  requeteOccur.close();
            }
            lanceRequeteOccur.close();
      }
      
      /**
       * Va calculer la fameuse note NB positive
       * @param phrase
       * @return
       * @throws SQLException 
       */
      public float calculNoteNBPositif(Phrase phrase) throws SQLException{
            float noteNB;
            recupereNbrOccur(phrase);
            recupereNbrComm();
            noteNB = (occPosit/occTotal)*(commPosit/commTot);
            return noteNB;
      }
      
      /**
       * Va calculer la version NB négative
       * @param phrase
       * @return
       * @throws SQLException 
       */
       public float calculNoteNBNegatif(Phrase phrase) throws SQLException{ 
            float noteNB;
            recupereNbrOccur(phrase);
            recupereNbrComm();
            noteNB = (occNeg/occTotal)*(commNegat/commTot);
            return noteNB;
      }
      /**
       * Mise à jou de toute une phrase avec mise à jour des mots dedans et update de la table phrase.
       * @param phrase
       * @throws SQLException 
       */
      public void miseAJourPhrase(Phrase phrase) throws SQLException{
            int classe;
            Statement lanceRequeteMaJ, lanceRequeteID;
            lanceRequeteMaJ = co1.conn.createStatement();
            lanceRequeteID = co1.conn.createStatement();
            ResultSet requeteID;
            requeteID = lanceRequeteID.executeQuery("select max(ID_PHRASE) from PHRASE_TEST");
            requeteID.next();
            classe = calculNoteNBPositif(phrase)>=0.5 ?1:0;
            if (classe ==0 && calculNoteNBNegatif(phrase)>=0.5) classe = -1;
            
            lanceRequeteMaJ.executeUpdate("insert into PHRASE_TEST values ("+(requeteID.getInt(0)+1)+","+phrase+","+classe+",?,?");
           // penser à remplacer les deux ? mais pour le moment pas besoin
            requeteID.close();
            lanceRequeteID.close();
            lanceRequeteMaJ.close();
            
            apprentissageMots(phrase, classe);
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

      }

}
