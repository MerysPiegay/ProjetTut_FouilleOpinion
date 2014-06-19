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
 * NaiveBayesLemme classe des opinions suivants leurs connotations.
 *
 * Cette classe a besoin d'une base d'apprentissage afin de produire des
 * resultats pertinents. Elle classe ensuite des phrase de commentaire dans
 * trois catégories : Positif/Neutre/Negatif
 *
 * @author Cyril
 */
public class NaiveBayesLemme {

    /**
     * commPositif, commNegatif, commTotal, commNeutre represente le nombre de
     * commentaire de chaque classe, ainsi que le total. Ces variables sont
     * incrémentées lors des apprentissages (premierApprentissageMots() et
     * miseAJourPhrase(ptfo.Phrase, int)). L'incrementation se fait grace a la
     * fonction miseAjourNbrComm()
     *
     * @see NaiveBayesLemme#miseAjourNbrComm(int)
     * @see NaiveBayesLemme#premierApprentissageMots()
     * @see NaiveBayesLemme#miseAJourPhrase(ptfo.Phrase, int)
     *
     *
     * occPositif, occTotal, occNegatif, occNeutre sont le nombre d'occurence
     * d'un mot dans les diverses classes Elle sont recuperées dans la table
     * MOTNBLM dans la fonction recupereNbrOccur (). Elles servent au calcule de
     * la note Naïve Bayesienne
     *
     * @see NaiveBayesLemme#recupereNbrOccur(java.lang.String)
     * @see NaiveBayesLemme#calculNoteNBNegatif(ptfo.Phrase)
     * @see NaiveBayesLemme#calculNoteNBNeutre(ptfo.Phrase)
     * @see NaiveBayesLemme#calculNoteNBPositif(ptfo.Phrase)
     */
    double commPositif, commNegatif, commTotal, commNeutre;
    double note;
    double occPositif, occTotal, occNegatif, occNeutre;
    Connection co1;

    /**
     * Initialisation de l'objet
     *
     * @throws SQLException si il y a un probleme avec la BDD
     */
    NaiveBayesLemme() throws SQLException {
        commPositif = 0;
        commNegatif = 0;
        commTotal = 0;
        commNeutre = 0;
        co1 = new Connection();
    }

    /**
     * Recupere les nombres d'occurence pour un mot
     *
     * @param s recupere le mot souhaité
     * @throws SQLException si il y a un probleme avec la BDD
     * @see NaiveBayesLemme#occNegatif
     * @see NaiveBayesLemme#occNeutre
     * @see NaiveBayesLemme#occPositif
     * @see NaiveBayesLemme#occTotal
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
            occPositif = (double) requeteOccur.getInt("OCCUR_POSITIF");
            occNegatif = (double) requeteOccur.getInt("OCCUR_NEGATIF");
            occNeutre = (double) requeteOccur.getInt("OCCUR_NEUTRE");
            occTotal = (double) requeteOccur.getInt("OCCUR_TOTAL");
        }
        requeteOccur.close();
        lanceRequeteOccur.close();
    }

    /**
     * Nourrit la table MOTNBLM a partir d'une phrase et da sa classe passé en
     * parametre. Elle tranforme les mots de la phrase en lemme avant de les
     * stocker dans la base. Elle est appelée dans premierApprentissageMots() et
     * a la fin de miseAJourPhrase(ptfo.Phrase, int).
     *
     * @see NaiveBayesLemme#premierApprentissageMots()
     * @see NaiveBayesLemme#miseAJourPhrase(ptfo.Phrase, int)
     * @param phraseNB
     * @param classePhrase classe de la phrase ( positif/neutre/negatif)
     * @throws SQLException si il y a un probleme avec la BDD
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
                System.out.println(s);
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
                                System.out.println("negatif");
                                break;
                            case 0:
                                ajoutMot.setInt(1, ID);
                                ajoutMot.setString(2, s);
                                ajoutMot.setInt(3, 0);
                                ajoutMot.setInt(4, 0);
                                ajoutMot.setInt(5, 1);
                                System.out.println("nzutre");
                                break;
                            case 1:
                                ajoutMot.setInt(1, ID);
                                ajoutMot.setString(2, s);
                                ajoutMot.setInt(3, 1);
                                ajoutMot.setInt(4, 0);
                                ajoutMot.setInt(5, 0);
                                System.out.println("positif");
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
                                    System.out.println("negatif");
                                    break;
                                case 0:
                                    insertMot.setInt(1, 0);
                                    insertMot.setInt(2, 1);
                                    insertMot.setInt(3, 0);
                                    insertMot.setString(4, s);
                                    System.out.println("neutre");
                                    break;
                                case 1:
                                    insertMot.setInt(1, 0);
                                    insertMot.setInt(2, 0);
                                    insertMot.setInt(3, 1);
                                    insertMot.setString(4, s);
                                    System.out.println("positif");
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
                                System.out.println("negatif");
                                break;
                            case 0:
                                ajoutMot.setInt(1, ID);
                                ajoutMot.setString(2, lemme);
                                ajoutMot.setInt(3, 0);
                                ajoutMot.setInt(4, 0);
                                ajoutMot.setInt(5, 1);
                                System.out.println("neutre");
                                break;
                            case 1:
                                ajoutMot.setInt(1, ID);
                                ajoutMot.setString(2, lemme);
                                ajoutMot.setInt(3, 1);
                                ajoutMot.setInt(4, 0);
                                ajoutMot.setInt(5, 0);
                                System.out.println("positif");
                                break;
                            default:
                                System.out.println("Problème dans le insert / switch de apprentissageMots() ");
                                break;
                        }
                        ajoutMot.executeUpdate();
                        lemmeDejaFait.mots.add(lemme);
                        ajoutMot.close();
                    } else { // si il est dans la liste
                        if (!lemmeDejaFait.mots.contains(lemme)) {
                            PreparedStatement insertMot;
                            String insert = "update MOTSNBLM set OCCUR_TOTAL = OCCUR_TOTAL+1, OCCUR_NEGATIF = OCCUR_NEGATIF+?,OCCUR_NEUTRE = OCCUR_NEUTRE +?, OCCUR_POSITIF =OCCUR_POSITIF+? where MOTS=?";
                            insertMot = co1.conn.prepareStatement(insert);
                            switch (classePhrase) {
                                case -1:
                                    insertMot.setInt(1, 1);
                                    insertMot.setInt(2, 0);
                                    insertMot.setInt(3, 0);
                                    insertMot.setString(4, lemme);
                                    System.out.println("negatif");
                                    break;
                                case 0:
                                    insertMot.setInt(1, 0);
                                    insertMot.setInt(2, 1);
                                    insertMot.setInt(3, 0);
                                    insertMot.setString(4, lemme);
                                    System.out.println("neutre");
                                    break;
                                case 1:
                                    insertMot.setInt(1, 0);
                                    insertMot.setInt(2, 0);
                                    insertMot.setInt(3, 1);
                                    insertMot.setString(4, lemme);
                                    System.out.println("positif");
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

                }
                lanceRequeteLemme.close();
                requeteLemme.close();
            }
        }
    }

    /**
     * Va permettre de noter la première fois tous les mots pour nourrir la
     * table MOTNBLM
     *
     * @throws SQLException si il y a un probleme avec la BDD
     */
    public void premierApprentissageMots() throws SQLException {
        PreparedStatement lanceRequetePhrase;
        String selectPhrase = "select * from RPHRASE"; //Table d'apprentissage
        lanceRequetePhrase = co1.conn.prepareStatement(selectPhrase);
        ResultSet requetePhrase;

        requetePhrase = lanceRequetePhrase.executeQuery();
        while (requetePhrase.next()) {
            Phrase phrase;
            phrase = new Phrase(requetePhrase.getString("PHRASE"));
            int classePhrase = requetePhrase.getInt("CLASSE");
            System.out.println(phrase);
            System.out.println(classePhrase);
            apprentissageMots(phrase, classePhrase);
            miseAjourNbrComm(classePhrase);
        }
        requetePhrase.close();
        lanceRequetePhrase.close();
    }

    /**
     * Va calculer la fameuse note NB positive pour miseAJourPhrase(ptfo.Phrase,
     * int)
     *
     * @param phrase
     * @return noteNB
     * @see NaiveBayesLemme#miseAJourPhrase(ptfo.Phrase, int)
     * @throws SQLException si il y a un probleme avec la BDD
     */
    public double calculNoteNBPositif(Phrase phrase) throws SQLException {
        double noteNB = 1;
        //boolean passage = false;
        for (String s : phrase.mots) {
            s = s.toLowerCase();
            recupereNbrOccur(s);
            if (occPositif != 0) {
                //passage = true;
                System.out.println(s);
                noteNB *= (occPositif / commPositif) * 10;
                System.out.println("nbr NB POSITIF Temp : " + noteNB);
            } else {
                noteNB *= (1 / commPositif) * 10;
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
     * Va calculer la note NB négative pour miseAJourPhrase(ptfo.Phrase, int)
     *
     * @param phrase
     * @return noteNB
     * @see NaiveBayesLemme#miseAJourPhrase(ptfo.Phrase, int)
     * @throws SQLException si il y a un probleme avec la BDD
     */
    public double calculNoteNBNegatif(Phrase phrase) throws SQLException {
        double noteNB = 1;
        //boolean passage = false;
        for (String s : phrase.mots) {
            s = s.toLowerCase();
            recupereNbrOccur(s);
            if (occNegatif != 0) {
                //         passage = true;

                noteNB *= (occNegatif / commNegatif) * 10;
                System.out.println("nbr NB NEGATIF Temp : " + noteNB);
            } else {
                noteNB *= (1 / commNegatif) * 10;
            }
            System.out.println(s);
            System.out.println(noteNB);
        }
        //if (passage) {
        noteNB *= commNegatif / commTotal;
        System.out.println("\t\tNombre NB NEGATIF : " + noteNB);
        // } else {
        //      noteNB = 0;
        // }
        return noteNB;
    }

    /**
     * Calcule la note NB du neutre pour miseAJourPhrase(ptfo.Phrase, int)
     *
     * @param phrase
     * @return noteNB
     * @see NaiveBayesLemme#miseAJourPhrase(ptfo.Phrase, int)
     * @throws SQLException si il y a un probleme avec la BDD
     */
    public double calculNoteNBNeutre(Phrase phrase) throws SQLException {
        double noteNB = 1;
        //boolean passage = false;
        for (String s : phrase.mots) {
            s = s.toLowerCase();
            recupereNbrOccur(s);
            if (occNeutre != 0) {
                //       passage = true;
                System.out.println(s);
                noteNB *= (occNeutre / commNeutre) * 10;
                System.out.println("nbr NB NEUTRE Temp : " + noteNB);
            } else {
                noteNB *= (1 / commNeutre) * 10;
            }
        }
        //if (passage) {
        noteNB *= commNeutre / commTotal;
        System.out.println("\t\tNombre NB NEUTRE : " + noteNB);
        //}else noteNB = 0;

        return noteNB;
    }

    /**
     * Prend en parametre une phrase pour la noter et la ranger dans la table
     * PHRASE_NBLM
     * La phrase est au préalable lemmatisé pour la notation.
     *
     * @param phrase
     * @param id
     * @see NaiveBayesLemme#calculNoteNBNegatif(ptfo.Phrase) 
     * @see NaiveBayesLemme#calculNoteNBNeutre(ptfo.Phrase) 
     * @see NaiveBayesLemme#calculNoteNBPositif(ptfo.Phrase) 
     * @throws SQLException si il y a un probleme avec la BDD
     */
    public void miseAJourPhrase(Phrase phrase, int id) throws SQLException { // a revoir 
      /*  -------------------
         |  LEMMATISATION /!\  |
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

        /*  --------------------------------------------------
         | Utilisation la phrase de lemme pour les noatations |
         -----------------------------------------------------
         */
        int classe;


        double noteNbPos = calculNoteNBPositif(phraseDeLemme);
        double noteNbNeg = calculNoteNBNegatif(phraseDeLemme);
        double noteNbNeutre = calculNoteNBNeutre(phraseDeLemme);

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
        maj.setDouble(4, noteNbPos);
        maj.setDouble(5, noteNbNeg);
        maj.executeUpdate();
        maj.close();
        System.out.println(phraseDeLemme.phrase);
        apprentissageMots(phraseDeLemme, classe);
        miseAjourNbrComm(classe);
    }

    /**
     * A chaque apprendtissage, met a jour le nombre de commentaire.
     * 
     * @see NaiveBayesLemme#premierApprentissageMots()
     * @see NaiveBayesLemme#miseAJourPhrase(ptfo.Phrase, int)
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
     * Va nourrir la table MOTNBLM dans un premier temps avec la fonction
     * premierApprentissageMots()
     * Puis va noter des commentaires grace a miseAJourPhrase.
     * 
     * @see NaiveBayesLemme#premierApprentissageMots()
     * @see NaiveBayesLemme#miseAJourPhrase(ptfo.Phrase, int)
     * @param args
     * @throws SQLException 
     * 
     */
    public static void main(String[] args) throws SQLException {

        NaiveBayesLemme nb;
        nb = new NaiveBayesLemme();
        nb.premierApprentissageMots();

        PreparedStatement lanceRequeteTest;
        String selectPhrase = "select * from PHRASE_NB"; //Table a noter
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
