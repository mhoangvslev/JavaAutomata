/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.analyser;

import javaautomata.lexical.lexeme.Lexeme;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javaautomata.automata.Automata;
import javaautomata.lexical.exceptions.LexemeParsingException;
import javaautomata.lexical.lexeme.*;

/**
 *
 * @author minhhoangdang
 */
public class SyntaxAnalyser {

    private final Map<Character, List<Lexeme>> composition;
    private final Map<String, Object> metadata;

    /**
     * Constructeur
     *
     * @param automate l'automate à analyser
     */
    public SyntaxAnalyser(Automata automate) {
        this.composition = automate.getComposition();
        this.metadata = automate.getMetadata();
    }

    /**
     * Vérifier si le tout a du sens
     *
     * @throws LexemeParsingException
     */
    public void checkSyntax() throws LexemeParsingException, NumberFormatException {
        /* Add defaults */
        if (!this.composition.containsKey('M')) {
            System.out.println("\t>> Méta-caractère n'étant pas précisé, interpretation avec # par défaut");
            List<Lexeme> temp = new ArrayList<>();
            temp.add(new LexemeM('M', "#"));
            this.composition.put('M', temp);
        }

        if (!this.composition.containsKey('I')) {
            System.out.println("\t>> L'état initial n'étant pas précisé, utiliser 0 par défaut");
            List<Lexeme> temp = new ArrayList<>();
            temp.add(new LexemeI('I', "0"));
            this.composition.put('I', temp);
        }

        /* Errors */
        if (!this.composition.containsKey('V')) {
            throw new LexemeParsingException("Syntax error (V): Pas de vocabulaire d'entrée");
        }

        if (!this.composition.containsKey('E')) {
            throw new LexemeParsingException("Syntax error (E): Le nombre d'état n'est pas précisé");
        }

        if (!this.composition.containsKey('F')) {
            throw new LexemeParsingException("Syntax error: Pas d'états acceptants");
        }

        if (!this.composition.containsKey('T')) {
            throw new LexemeParsingException("Syntax error: Pas de transition!");
        }

        System.out.println("\t>>" + this.composition);

        /*Summarise*/
        this.metadata.put("meta", this.getData('M', 0).get(0));
        this.metadata.put("vocab_entree", this.getData('V', 0).get(0));
        this.metadata.put("vocab_sortie", this.getData('O', 0).get(0));
        this.metadata.put("nb_etats", Integer.parseInt(this.getData('E', 0).get(0)) + 1);
        this.metadata.put("etats_init", this.getData('I', 0));
        this.metadata.put("etats_acceptants", this.getData('F', 0));

        System.out.println("\t>> Summarise: " + this.metadata);

        List<String> listUsedEtats = new ArrayList<>();
        List<String> listUsedInputs = new ArrayList<>();

        /* Coherence check in T lexemes*/
        for (Lexeme lex : this.composition.get('T')) {

            /* Harmoniser à quatre attributs */
            if (lex.getContent().size() == 3) {
                lex.getContent().add("" + this.metadata.get("meta"));
            }

            /* meta */
            if (!listUsedEtats.contains(lex.getContent().get(0))) {
                listUsedEtats.add(lex.getContent().get(0));
            }
            
            if (!listUsedInputs.contains(lex.getContent().get(1))) {
                listUsedInputs.add(lex.getContent().get(1));
            }

            /* Autres */
            for (int i = 0; i < lex.getContent().size(); i++) {
                String token = lex.getContent().get(i);

                /* Vérifier si l'ID est entre [0, N-1]. */
                if (i % 2 == 0) {
                    int num = Integer.parseInt(token);
                    int nbEtats = (Integer) this.metadata.get("nb_etats");
                    if (num > nbEtats || num < 0) {
                        throw new LexemeParsingException("L'ID de l'état " + num + " doit être entre [" + 0 + ", " + (nbEtats - 1) + "] ");
                    }
                } /* Vérifier si ID est dans le vocab d'entréé */ else if (i == 1) {
                    String vocabEntree = (String) this.metadata.get("vocab_entree");
                    if (!vocabEntree.contains(token)) {
                        throw new LexemeParsingException("L'ID de l'état '" + token + "' doit être dans '" + vocabEntree + "'");
                    }
                } /* Vérifier si ID est dans le vocab de sortie */ else {
                    String vocabSortie = (String) this.metadata.get("vocab_sortie") + this.metadata.get("meta");
                    if (!(vocabSortie + this.metadata.get("meta")).contains(token)) {
                        throw new LexemeParsingException("L'ID de l'état '" + token + "' doit être dans '" + vocabSortie + "'");
                    }
                }
            }
        }

        /* Si on arrive ici, alles gut! */
        
        this.metadata.put("usedInputs", listUsedInputs);
        this.metadata.put("usedStates", listUsedEtats);
        System.out.println("\t> SYNTAX OK!");
    }

    /**
     *
     * @param key
     * @param itemIndex
     * @return
     */
    private List<String> getData(char key, int itemIndex) {
        List<String> result = new ArrayList<>();
        try {
            if (this.composition.get(key) != null) {
                if (this.composition.get(key).size() < 1) {
                    result.add("");
                } else {
                    result = this.composition.get(key).get(itemIndex).getContent();
                }
            } else {
                result.add("");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage() + " Key: " + key + " Content: " + this.composition.get(key));
        }
        return result;
    }

}
