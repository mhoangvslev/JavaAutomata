/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.analyser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
     * Dire si AEF est complet.
     * Si il y a autant d'état entrant que d'état sortant
     */
    public void checkComplete() {
        Collection<String> inStates = (Collection<String>) this.metadata.get("usedInStates");
        Collection<String> outStates = (Collection<String>) this.metadata.get("usedOutStates");
        List<String> a, b;
        a = new ArrayList<>(inStates);
        b = new ArrayList<>(outStates);
        
        Collections.sort(a);
        Collections.sort(b);

        this.metadata.put("estComplet", a.equals(b));
    }
}
