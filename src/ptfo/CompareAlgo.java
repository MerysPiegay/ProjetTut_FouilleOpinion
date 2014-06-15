/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author yougo
 */
public class CompareAlgo {

      Connection connexion;
      NaiveBayes nb;

      public CompareAlgo() throws SQLException {
            connexion = new Connection();
            
      }

      public void ameliorationPhrase() throws SQLException {
            PreparedStatement recupPhrase;
            String select = "select *  from PHRASE_NB";
            recupPhrase = connexion.conn.prepareStatement(select);
            ResultSet phraseAModif;
            phraseAModif = recupPhrase.executeQuery();
            while (phraseAModif.next()) {
                  int idPhrase = phraseAModif.getInt("ID_PHRASE");
                  Phrase phrase = new Phrase(phraseAModif.getString("PHRASE"));
                  int classe = phraseAModif.getInt("CLASSE");
                  int idComm = phraseAModif.getInt("ID_COMMENTAIRE");
                  PreparedStatement renvoiPhrase;
                  String insert = "insert into PHRASE_NB_CLEAN values(?,?,?,?)";
                  renvoiPhrase = connexion.conn.prepareStatement(insert);
                  renvoiPhrase.setInt(1, idPhrase);
                  renvoiPhrase.setString(2, phrase.phrase);
                  renvoiPhrase.setInt(3, classe);
                  renvoiPhrase.setInt(4, idComm);
                  renvoiPhrase.executeUpdate();
                  renvoiPhrase.close();
            }
            phraseAModif.close();
            recupPhrase.close();
      }

      public void compareAlgoAvecHumain() throws SQLException {
            float noteRefPositif = 0;
            float noteATestPositifPositif = 0;
            float noteATestNeutrePositif = 0;
            float noteATestNegatifPositif = 0;
            float noteRefNeutre = 0;
            float noteATestPositifNeutre = 0;
            float noteATestNeutreNeutre=0;
            float noteATestNegatifNeutre=0;
            float noteRefNegatif = 0;
            float noteATestPositifNegatif = 0;
            float noteATestNeutreNegatif = 0;
            float noteATestNegatifNegatif = 0;
            PreparedStatement dbHumaine;
            PreparedStatement dbATest;
            String select = "select * from PHRASE_NB";
            dbHumaine = connexion.conn.prepareStatement(select);
            ResultSet phraseRecup;
            phraseRecup = dbHumaine.executeQuery();
            String selectTest = "select * from PHRASE_NB_TEST where ID_PHRASE =?";
            dbATest = connexion.conn.prepareStatement(selectTest);
            ResultSet phraseTestRecup;
            while (phraseRecup.next()) {
                  System.out.println("Phrase référence : " + phraseRecup.getString("PHRASE") +"\n" +"Classe : " + phraseRecup.getInt("CLASSE") + "\n");
                  int id = phraseRecup.getInt("ID_PHRASE");
                  int classe = phraseRecup.getInt("CLASSE");
                  dbATest.setInt(1, id);
                  phraseTestRecup = dbATest.executeQuery();
                  phraseTestRecup.next();
                  System.out.println("Phrase à tester  : " + phraseTestRecup.getString("PHRASE") +"\n" +"Classe : " + phraseTestRecup.getInt("CLASSE") + "\n");
                  int classeATest = phraseTestRecup.getInt("CLASSE");
                  switch (classe) {
                        case 1:
                              noteRefPositif++;
                              if (classe == classeATest) {
                                    noteATestPositifPositif++;
                              }else {
                                    if (classeATest == 0) noteATestNeutrePositif ++;
                                    else noteATestNegatifPositif ++;
                              }
                              break;
                        case 0:
                              noteRefNeutre++;
                              if (classe == classeATest) {
                                    noteATestNeutreNeutre++;
                              }else  {
                                    if (classeATest ==1) noteATestPositifNeutre++;
                                    else noteATestNegatifNeutre ++;
                              }
                              break;
                        case -1:
                              noteRefNegatif++;
                              if (classe == classeATest) {
                                    noteATestNegatifNegatif++;
                              }else {
                                    if(classeATest ==1)noteATestPositifNegatif ++;
                                    else noteATestNeutreNegatif ++;
                              }
                              break;
                        default:
                              System.out.println("Problème ! ! ");
                              break;
                  }
                  phraseTestRecup.close();
            }
            System.out.println("\n\n\n\t\t\t\t\tResultat de l'algo");
            System.out.println("\t\t\t Positif\t|\tNeutre\t|\tNegatif\n");
            System.out.format("Fscore Positif  :\t %7.2f \t|%7.2f\t|%7.2f \n", (noteATestPositifPositif / noteRefPositif),(noteATestNeutrePositif / noteRefPositif),(noteATestNegatifPositif / noteRefPositif));
            System.out.println("-------------------------------------------------------------------------------\n");
            System.out.format("Fscore Neutre : \t %7.2f \t|%7.2f\t|%7.2f \n", (noteATestPositifNeutre / noteRefNeutre),(noteATestNeutreNeutre / noteRefNeutre),(noteATestNegatifNeutre / noteRefNeutre));
            System.out.println("-------------------------------------------------------------------------------\n");
            System.out.format("Fscore Negatif : \t %7.2f \t|%7.2f\t|%7.2f \n", (noteATestPositifNegatif / noteRefNegatif),(noteATestNeutreNegatif / noteRefNegatif),(noteATestNegatifNegatif / noteRefNegatif));
            System.out.println("-------------------------------------------------------------------------------\n");

            dbATest.close();
            phraseRecup.close();
            dbHumaine.close();

      }

      /**
       * @param args the command line arguments
       */
      public static void main(String[] args) throws SQLException {
            // TODO code application logic here
            CompareAlgo nettoyage;
            nettoyage = new CompareAlgo();
            //nettoyage.ameliorationPhrase();
            nettoyage.compareAlgoAvecHumain();
      }

}
