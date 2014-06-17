/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ptfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement; //necessaire aux interactions avec la BDD

/**
 *
 * @author Cyril
 */
public class NaiveBayesLemme {

    float commPositif, commNegatif, commTotal, commNeutre;
    float note;
    float occPositif, occTotal, occNegatif, occNeutre;
    Connection co1;

    NaiveBayesLemme() throws SQLException {
        commPositif = 504;
                commNegatif = 361;
                commTotal = 1266;
                commNeutre = 277;
        co1 = new Connection();
    }

    /* public void recupereNbrComm() throws SQLException { //creer la TABLE PHRASE_NBKM
        
     PreparedStatement lanceRequete1;
     PreparedStatement lanceRequeteTotal;
     String selectCount;
     selectCount = "select distinct count  (PHRASE) from PHRASE_NB where CLASSE =?";
     String selectCountTotal = "select distinct count  (PHRASE) from PHRASE_NB";
     lanceRequete1 = co1.conn.prepareStatement(selectCount);
     lanceRequeteTotal = co1.conn.prepareStatement(selectCountTotal);
     ResultSet requete1;
     ResultSet requeteTotal;
        
     PreparedStatement lanceRequeteNew;
     PreparedStatement lanceRequeteTotalNew;
     String selectCountNew = "select count(PHRASE) from PHRASE_NB where CLASSE >=?";
     String selectCountTotalNew = "select count(PHRASE) from PHRASE_NB";
     lanceRequeteNew = co1.conn.prepareStatement(selectCountNew);
     lanceRequeteTotalNew = co1.conn.prepareStatement(selectCountTotalNew);
     ResultSet requeteNew;
     ResultSet requeteTotalNew;

     //récupération des CT et CP.
     lanceRequete1.setInt(1, 1);
     requete1 = lanceRequete1.executeQuery();
     requete1.next();
        
     lanceRequeteNew.setInt(1, 1);
     requeteNew = lanceRequeteNew.executeQuery();
     requeteNew.next();
        
     commPositif = requete1.getInt(1) + requeteNew.getInt(1);
     requete1.close(); 
     System.out.println(commPositif +'\n');
     requeteNew.close();
        
        
        
        
        
     lanceRequete1.setInt(1, -1);
     requete1 = lanceRequete1.executeQuery();
     requete1.next();        
        
     lanceRequeteNew.setInt(1, -1);
     requeteNew = lanceRequeteNew.executeQuery();
     requeteNew.next();
        
     commNegatif = requete1.getInt(1) /*+ requeteNew.getInt(1);
     requete1.close();
     requeteNew.close();
        
        
     lanceRequete1.setInt(1, 0);
     requete1 = lanceRequete1.executeQuery();
     requete1.next();
     lanceRequeteNew.setInt(1, 0);
     requeteNew = lanceRequeteNew.executeQuery();
     requeteNew.next();
     commNeutre = requete1.getInt(1) + requeteNew.getInt(1);       
     requete1.close();
     requeteNew.close();
        
        
     requeteTotal = lanceRequeteTotal.executeQuery();
     requeteTotal.next();
     requeteTotalNew = lanceRequeteTotalNew.executeQuery();
     requeteTotalNew.next();
     commTotal = requeteTotal.getInt(1) + requeteTotalNew.getInt(1);
     requeteTotal.close();
     lanceRequete1.close();
     lanceRequeteTotal.close();
     requeteTotalNew.close();
     lanceRequeteNew.close();
     lanceRequeteTotalNew.close();
     }*/
    /**
     * Récupère le nombre d'occurence pour chaque mot de la phrase et en fait
     * l'addition
     *
     * @param s
     * @throws SQLException
     */
    public void recupereNbrOccur(String s) throws SQLException {
        PreparedStatement lanceRequeteOccur;
        String selectMot = "select * from MOTSNBLM where MOTS=?";
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
     * Récupère le nombre d'occurence pour chaque mot de la phrase et en fait
     * l'addition
     *
     * @param phraseNB
     * @param classePhrase
     * @throws SQLException
     */
    public void apprentissageMots(Phrase phraseNB, int classePhrase) throws SQLException {

        for (String s : phraseNB.mots) {
            if (!s.equals("")) {
                PreparedStatement lanceRequeteID; //recuperation de la position de dernier element de MOTNBLM pour inserer les nouveau lemme
                String selectID = "select max(ID) from MOTSNBLM";
                lanceRequeteID = co1.conn.prepareStatement(selectID);
                ResultSet requeteID;
                requeteID = lanceRequeteID.executeQuery();
                requeteID.next();
                int ID = requeteID.getInt(1) + 1;

                Phrase lemmeDejaFait;
                s = s.toLowerCase();
                lemmeDejaFait = new Phrase("");

                String lemme = ""; //Recuperation du lemme
                PreparedStatement lanceRequeteLemme;
                String selectLemme = "select LEMME from LEXIQUE where MOT = ? and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = ?)";
                lanceRequeteLemme = co1.conn.prepareStatement(selectLemme);
                lanceRequeteLemme.setString(1, s);
                lanceRequeteLemme.setString(2, s);
                ResultSet requeteLemme;
                requeteLemme = lanceRequeteLemme.executeQuery();
                if (!requeteLemme.next()) { //si le lemme du mot n'existe pas
                    System.out.println(s);
                    PreparedStatement lanceRequeteAll;

                    String selectAll = "select * from MOTSNBLM where MOTS =?";
                    lanceRequeteAll = co1.conn.prepareStatement(selectAll);
                    lanceRequeteAll.setString(1, s);
                    ResultSet requeteAll;
                    requeteAll = lanceRequeteAll.executeQuery();
                    if (!requeteAll.next()) {//si le mot n'est pas dans la base MOTNBLM
                        PreparedStatement ajoutMot;
                        String ajout = "insert into MOTSNBLM values (?,?,1,?,?,? )";
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
                        lemmeDejaFait.mots.add(s);
                        ajoutMot.close();
                    } else { // si il est dans la liste
                        if (!lemmeDejaFait.mots.contains(s)) {
                            PreparedStatement insertMot;
                            String insert = "update MOTSNBLM set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+?,OCCUR_NEUTRE = OCCUR_NEUTRE +?, OCCUR_POSITIF =OCCUR_POSITIF+? where MOTS=?";
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

                        lemmeDejaFait.mots.add(s);
                    }
                    requeteAll.close();
                    lanceRequeteAll.close();
                    requeteID.close();
                    lanceRequeteID.close();
                } else {//si le lemme est dans la base lexique
                    lemme = requeteLemme.getNString(1);
                    System.out.println(lemme);

                    //
                    PreparedStatement lanceRequeteAll;
                    String selectAll = "select * from MOTSNBLM where MOTS =?";
                    lanceRequeteAll = co1.conn.prepareStatement(selectAll);
                    lanceRequeteAll.setString(1, lemme);
                    ResultSet requeteAll;
                    requeteAll = lanceRequeteAll.executeQuery();
                    if (!requeteAll.next()) {//si le lemme n'est pas dans la base MOTNBLM
                        PreparedStatement ajoutMot;
                        String ajout = "insert into MOTSNBLM values (?,?,1,?,?,? )";
                        ajoutMot = co1.conn.prepareStatement(ajout);
                        switch (classePhrase) {
                            case -1:
                                ajoutMot.setInt(1, ID);
                                ajoutMot.setString(2, lemme);
                                ajoutMot.setInt(3, 0);
                                ajoutMot.setInt(4, 1);
                                ajoutMot.setInt(5, 0);
                                break;
                            case 0:
                                ajoutMot.setInt(1, ID);
                                ajoutMot.setString(2, lemme);
                                ajoutMot.setInt(3, 0);
                                ajoutMot.setInt(4, 0);
                                ajoutMot.setInt(5, 1);
                                break;
                            case 1:
                                ajoutMot.setInt(1, ID);
                                ajoutMot.setString(2, lemme);
                                ajoutMot.setInt(3, 1);
                                ajoutMot.setInt(4, 0);
                                ajoutMot.setInt(5, 0);
                                break;
                            default:
                                System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                break;
                        }
                        ajoutMot.executeUpdate();
                        lemmeDejaFait.mots.add(lemme);
                        ajoutMot.close();
                    } else { // si il est dans la liste
                        if (!lemmeDejaFait.mots.contains(lemme)) {//bisar
                            PreparedStatement insertMot;
                            String insert = "update MOTSNBLM set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+?,OCCUR_NEUTRE = OCCUR_NEUTRE +?, OCCUR_POSITIF =OCCUR_POSITIF+? where MOTS=?";
                            insertMot = co1.conn.prepareStatement(insert);
                            switch (classePhrase) {
                                case -1:
                                    insertMot.setInt(1, 1);
                                    insertMot.setInt(2, 0);
                                    insertMot.setInt(3, 0);
                                    insertMot.setString(4, lemme);
                                    break;
                                case 0:
                                    insertMot.setInt(1, 0);
                                    insertMot.setInt(2, 1);
                                    insertMot.setInt(3, 0);
                                    insertMot.setString(4, lemme);
                                    break;
                                case 1:
                                    insertMot.setInt(1, 0);
                                    insertMot.setInt(2, 0);
                                    insertMot.setInt(3, 1);
                                    insertMot.setString(4, lemme);
                                    break;
                                default:
                                    System.out.println("Problème dans le update / switch de apprentissageMots() ");
                                    break;
                            }
                            insertMot.executeUpdate();
                            insertMot.close();

                        }

                        lemmeDejaFait.mots.add(lemme); // pas sur que ca soir utile
                    }
                    requeteAll.close();
                    lanceRequeteAll.close();
                    requeteID.close();
                    lanceRequeteID.close();

                    //
                }
                lanceRequeteLemme.close();
                requeteLemme.close();
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
        String selectPhrase = "select * from PHRASE_DEMO";
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
     * Va calculer la fameuse note NB positive
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
       * Va calculer la version NB négative
       *
       * @param phrase
       * @return
       * @throws SQLException
       */
      public float calculNoteNBNegatif(Phrase phrase) throws SQLException {
            float noteNB = 1;
            //boolean passage = false;
            for (String s : phrase.mots) {
                  s = s.toLowerCase();
                  recupereNbrOccur(s);
                  if (occNegatif != 0) {
                        //         passage = true;
                        System.out.println(s);
                        noteNB *= (occNegatif / commNegatif);
                        System.out.println("nbr NB NEGATIF Temp : " + noteNB);
                  } else {
                        noteNB *= (1 / commNegatif);
                  }
            }
            //if (passage) {
            noteNB *= commNegatif / commTotal;
            System.out.println("\t\tNombre NB NEGATIF : " + noteNB);
           // } else {
            //      noteNB = 0;
            // }
            return noteNB;
      }

      public float calculNoteNBNeutre(Phrase phrase) throws SQLException {
            float noteNB = 1;
            //boolean passage = false;
            for (String s : phrase.mots) {
                  s = s.toLowerCase();
                  recupereNbrOccur(s);
                  if (occNeutre != 0) {
                        //       passage = true;
                        System.out.println(s);
                        noteNB *= (occNeutre / commNeutre);
                        System.out.println("nbr NB NEUTRE Temp : " + noteNB);
                  } else {
                        noteNB *= (1 / commNeutre);
                  }
            }
            //if (passage) {
            noteNB *= commNeutre / commTotal;
            System.out.println("\t\tNombre NB NEUTRE : " + noteNB);
            //}else noteNB = 0;

            return noteNB;
      }

    /**
     * Mise à jou de toute une phrase avec mise à jour des mots dedans et update
     * de la table phrase.
     *
     * @param phrase
     * @param id
     * @throws SQLException
     */
    public void miseAJourPhrase(Phrase phrase, int id) throws SQLException { // a revoir 
      /*  -------------------
         |  A CONSERVER /!\  |
         -------------------
         */
        Phrase phraseDeLemme;
        phraseDeLemme = new Phrase("");
        for (String mot : phrase.mots) {
            mot = mot.toLowerCase();
            PreparedStatement lanceRequeteLemme;
            String selectLemme = "select LEMME from LEXIQUE where MOT = ? and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = ?)";
            lanceRequeteLemme = co1.conn.prepareStatement(selectLemme);
            lanceRequeteLemme.setString(1, mot);
            lanceRequeteLemme.setString(2, mot);
            ResultSet requeteLemme;
            requeteLemme = lanceRequeteLemme.executeQuery();
            if (!requeteLemme.next()) { // si le mot n'a pas de lemme
                phraseDeLemme.mots.add(mot); //on ajoute a la phrase le mot
            } else { //sinon on ajoute le lemme 
                String lemme;
                lemme = requeteLemme.getNString(1);
                phraseDeLemme.mots.add(lemme);
            }
            lanceRequeteLemme.close();
            requeteLemme.close();

        }

        /*  ----------------------------------------------
         | Utiliser la phrase de lemme pour les noatations |
         -------------------------------------------------
         */
        int classe;

        //PreparedStatement lanceRequeteID;
        //String selectMaxID = "select max(ID_PHRASE) from PHRASE_NB_TEST";
        //lanceRequeteID = co1.conn.prepareStatement(selectMaxID);
        //ResultSet requeteID;
        //requeteID = lanceRequeteID.executeQuery();
        //requeteID.next();
        //int ID = requeteID.getInt(1) + 1;
        float noteNbPos = calculNoteNBPositif(phraseDeLemme);
        float noteNbNeg = calculNoteNBNegatif(phraseDeLemme);
        float noteNbNeutre = calculNoteNBNeutre(phraseDeLemme);
        /*if (noteNbPos == noteNbNeg){
         if (noteNbPos == noteNbNeutre){
         classe = 9;
         System.out.println("\n\n\t\t\t\t TOTALEMENT INDETERMINE \n\n\n");
         }
         }*/
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
        String insertPhraseTest = "insert into PHRASE_NBLM values (?,?,?,0,?,?)";
        maj = co1.conn.prepareStatement(insertPhraseTest);
        maj.setInt(1, id);
        maj.setString(2, phrase.phrase);
        maj.setInt(3, classe);
        maj.setFloat(4, noteNbPos);
        maj.setFloat(5, noteNbNeg);
        maj.executeUpdate();
        maj.close();
        System.out.println(phraseDeLemme.phrase);
        apprentissageMots(phraseDeLemme, classe);
        miseAjourNbrComm(classe);
    }

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
     * Va noter tout un commentaire rentré en argument
     *
     * @param comment
     * @throws SQLException
     */
    /*
     public void notationCommentaire(Commentaire comment) throws SQLException {
     note = 0;
     Statement lanceRequetePhrase;
     lanceRequetePhrase = co1.conn.createStatement();

     for (String s : comment.phrases) {
     Phrase phrase = new Phrase(s);
     miseAJourPhrase(phrase);
     }

     lanceRequetePhrase.close();
     }*/
    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    // fin insertion brut
    public static void main(String[] args) throws SQLException {

        NaiveBayesLemme nb;
        nb = new NaiveBayesLemme();
        nb.premierApprentissageMots();

        PreparedStatement lanceRequeteTest;
        String selectPhrase = "select * from PHRASE_NB";
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
            //phraseDeTest = new Phrase("Ce pneu nul ");
            //int idPhrase = 2;

        }
        requeteTest.close();
        lanceRequeteTest.close();

        /*      ****   Z0N3 51    ****
                
         NaiveBayesLemme nbln;
         nbln = new NaiveBayesLemme();
         Phrase lemmeDejaFait;
         lemmeDejaFait = new Phrase("");
         String lemme = "";
         PreparedStatement lanceRequeteLemme;
         String selectLemme = "select LEMME from LEXIQUE where MOT = ? and FREQLEMFILM = (select Max(FREQLEMFILM) from LEXIQUE where MOT = ?)";
         lanceRequeteLemme = nbln.co1.conn.prepareStatement(selectLemme);
         lanceRequeteLemme.setString(1, "non");
         lanceRequeteLemme.setString(2, "non");
         ResultSet requeteLemme;
         requeteLemme = lanceRequeteLemme.executeQuery();
         if(!requeteLemme.next()){
         System.out.print("le lemme n'existe pas");
         }
         else{
         lemme = requeteLemme.getNString(1);
         System.out.println(lemme);
         }
        
         requeteLemme.close();
         lanceRequeteLemme.close();
         
         nbln.recupereNbrComm();
         System.out.println(nbln.commPositif +'\n');
         System.out.println(nbln.commNegatif +'\n');
         System.out.println(nbln.commTotal +'\n');
         System.out.println(nbln.commNeutre );
         
         */
    }
}
