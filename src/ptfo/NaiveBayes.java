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
      int note;
      int occPosit, occTotal, occNeg;
      Connection co1;
      
      NaiveBayes() throws SQLException {
               commPosit = commNegat = commTot = 0;
               co1 = new Connection();

      }

     void recupereNbrComm() throws SQLException {
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
     
     void apprentissageMots() throws SQLException{
          
            Statement lanceRequete1;
            lanceRequete1 = co1.conn.createStatement();
            ResultSet requete1;
            Phrase phraseNB;
            int classePhrase;
           
           requete1 = lanceRequete1.executeQuery("select *  from PHRASE ");
          while( requete1.next()){
                  phraseNB = new Phrase(requete1.getString("PHRASE"));
                  classePhrase =requete1.getInt("CLASSE");


                  for (String s : phraseNB.mots){
                        Statement lanceRequete2, lanceRequete3, lanceRequete4;
                        lanceRequete2=co1.conn.createStatement();
                        lanceRequete3=co1.conn.createStatement();
                        lanceRequete4=co1.conn.createStatement();
                        ResultSet requete2, requete3;
                        requete2=lanceRequete2.executeQuery("select * from MOTSNB where MOTS = s ");
                        requete3=lanceRequete3.executeQuery("select max(ID) from MOTSNB");
                        if (!requete2.next()){

                               switch (classePhrase) {
                                     case -1:  lanceRequete4.executeUpdate("insert into MOTSNB values (" +(requete3.getInt(1)+1) +"," + s + ",1,0,1,0 ");
                                            break;
                                     case 0:  lanceRequete4.executeUpdate("insert into MOTSNB values (" +(requete3.getInt(1)+1) +"," + s + ",1,0,0,1 ");
                                           break;
                                     case 1:  lanceRequete4.executeUpdate("insert into MOTSNB values (" +(requete3.getInt(1)+1) +"," + s + ",1,1,0,0 ");
                                           break;
                                     default: System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                           break;
                                }   
                        }else{
                              switch(classePhrase){
                                    case -1: lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+1 ");
                                          break;
                                    case 0: lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEUTRE = OCCUR_NEUTRE+1 ");
                                          break;
                                   case 1: lanceRequete4.executeUpdate("update MOTSNB set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_POSITIF =  OCCUR_POSITIF+1 ");
                                         break;
                                   default: System.out.println("Problème dans le update / switch de apprentissageMots() ");
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
     }
    
     // FONCTION EN CHANTIER
      void recupereNbrOccur(Phrase phraseNote) throws SQLException{
            Statement lanceRequeteOccur;
            lanceRequeteOccur = co1.conn.createStatement();
            ResultSet requeteOccur;
            occPosit =0;
            occNeg =0;
            occTotal =0;
            
            for (String s : phraseNote.mots){
                  requeteOccur = lanceRequeteOccur.executeQuery("select * from MOTSNB where MOTS="+s);
                  requeteOccur.next();
                  occPosit += requeteOccur.getInt("OCCUR_POSITIF");
                  occNeg += requeteOccur.getInt("OCCUR_NEGATIF");
                  occTotal += requeteOccur.getInt("OCCUR_TOTAL");
                  requeteOccur.close();
            }
            lanceRequeteOccur.close();
      }
      
        void notationCommentaire(){
              
              Commentaire comment;
              comment = new Commentaire(); 
              
              for (String s : phraseDecoupe){
                    
              }
        }

      /**
       * @param args the command line arguments
       */
      public static void main(String[] args) throws SQLException {
            NaiveBayes nb;
            nb = new NaiveBayes();
            nb.recupereNbrComm();
      }

}
