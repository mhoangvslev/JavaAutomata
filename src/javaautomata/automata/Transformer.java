/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javaautomata.lexical.exceptions.LexemeParsingException;

class Transition {

    private final String depart;
    private final String input;
    private final String output;
    private final String arrivee;

    public Transition(String depart, String input, String output, String arrivee) {
        this.depart = depart;
        this.input = input;
        this.output = output;
        this.arrivee = arrivee;
    }

    public String toString() {
        return String.join(" ", Arrays.asList(depart, input, arrivee, output));
    }
}

/**
 *
 * @author minhhoangdang
 */
public class Transformer {

    /**
     * Deterministic Finite Automata to Non-Deterministic
     * @param inAutomata
     * @return
     * @throws LexemeParsingException
     * @throws CloneNotSupportedException 
     */
    public static Automata DFA2NDFA(Automata inAutomata) throws LexemeParsingException, CloneNotSupportedException {
        
        Automata outAutomata = (Automata) inAutomata.clone();
        System.out.println(outAutomata);
        
        String oldName = (String) outAutomata.getMetadata().get("fileName");
        outAutomata.getMetadata().put("fileName", oldName.substring(0, oldName.indexOf(".")-1) + ".dfa.descr");
        
        List<Node> P = closure(inAutomata.getGraph().getTree().values());
        List<Node> L = new ArrayList<>();
        List<Transition> D = new ArrayList<>();

        while (P.size() > 0) {
            Node T = P.remove(0);
            if (!L.contains(T)) {
                L.add(T);

                for (Transition d : D) {
                    inAutomata.addToComposition('T', d.toString());
                }

                String vocab_entree = (String) inAutomata.getMetadata().get("vocab_entree");
                for (Character a : vocab_entree.toCharArray()) {
                    List<Node> temp = new ArrayList<>();
                    temp.add(T);
                    List<Node> U = closure(transiter(temp, "" + a));
                    for (Node u : U) {
                        for (Node uChild : u.getChildren()) {
                            D.add(new Transition(
                                    u.getNodeName(),
                                    u.getTransitionInput(uChild),
                                    u.getTransitionOutput(uChild),
                                    uChild.getNodeName()
                            ));
                        }
                    }
                    P.addAll(U);
                }
            }
        }
        return outAutomata;
    }

    /**
     * 
     * @param T
     * @return 
     */
    private static List<Node> closure(Collection<Node> T) {
        List<Node> p = new ArrayList<>(T);
        List<Node> F = new ArrayList<>();

        while (p.size() > 0) {
            Node t = p.remove(0);
            if (!F.contains(t)) {
                F.add(t);
                for (Node u : t.getChildren()) {
                    p.add(u);
                }
            }
        }

        return F;
    }

    /**
     * 
     * @param T
     * @param input
     * @return 
     */
    private static List<Node> transiter(Collection<Node> T, String input) {
        List<Node> F = new ArrayList<>();
        for (Node t : T) {
            for (Node u : t.getChildByInput(input)) {
                if (!F.contains(u)) {
                    F.add(u);
                }
            }
        }

        return F;
    }
}
