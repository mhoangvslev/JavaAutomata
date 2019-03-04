/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.analyser;

import java.util.List;
import java.util.Map;
import javaautomata.automata.Automata;
import javaautomata.lexical.lexeme.Lexeme;

/**
 *
 * @author minhhoangdang
 */
public class SemanticAnalyser {

    private final Map<Character, List<Lexeme>> composition;
    private final Map<String, Object> metadata;

    /**
     * Constructeur
     *
     * @param automate l'automate à analyser
     */
    public SemanticAnalyser(Automata automate) {
        this.composition = automate.getComposition();
        this.metadata = automate.getMetadata();
    }

    /**
     * Dire si AEF est deterministe
     */
    public void checkDeterministic() {
        boolean result = (this.composition.get('I').get(0).getContent().size() == 1);
        String sourceState = "", input = "";
        
        for (Lexeme lex : this.composition.get('T')) {
            String currentSource = lex.getContent().get(0);
            String currentInput = lex.getContent().get(1);
            result = result && (!currentSource.equals(sourceState) || !currentInput.equals(input));
            if (!result) {
                break;
            }
            sourceState = currentSource;
            input = currentInput;
        }

        this.metadata.put("estDeterministe", result);
        if (!result) {
            checkComplete();
        } else {
            this.metadata.put("estComplet", true);
        }

    }

    /**
     * Dire si AEF est complet Déf: ∀e ∈ E, a ∈ V, ∃e' : (e, a, e', o) ∈ ∆ Rejet
     * si: ∃e ∈ E, a ∈ V, ∀e' : (e, a, e', o) ~∈ ∆
     */
    public void checkComplete() {
        boolean result = true;

        int nbEtats = (int) this.metadata.get("nb_etats");
        List<String> inStates = (List<String>) this.metadata.get("usedInStates");
        List<String> outStates = (List<String>) this.metadata.get("usedInStates");

        for (int i = 0; i < nbEtats && result; i++) {
            if (!inStates.contains("" + i)) {
                for (int j = 0; i < nbEtats && result; j++) {
                    if (outStates.contains("" + j)) {
                        result = false;
                    }
                }
            }
        }

        String vocab_entree = (String) this.metadata.get("vocab_entree");
        List<String> inputs = (List<String>) this.metadata.get("usedInputs");

        for (String input : inputs) {
            if (!vocab_entree.contains(input)) {
                result = false;
                break;
            }
        }

        this.metadata.put("estComplet", result);
    }
}
