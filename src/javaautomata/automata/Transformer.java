/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import javaautomata.lexical.exceptions.LexemeParsingException;

class SuperState {

    private static int id = 0;
    private String name;
    private boolean isFinal;
    private final Collection<Node> nodes;

    public SuperState(Collection<Node> nodes) {
        id++;
        this.name = "" + id;
        this.nodes = nodes;
        isFinal = false;

        for (Node n : nodes) {
            if (n.isFinal()) {
                isFinal = true;
                break;
            }
        }
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SuperState) {
            SuperState ss = (SuperState) o;

            List<Node> a, b;
            a = new ArrayList<>(nodes);
            b = new ArrayList<>(ss.getNodes());
            Collections.sort(a);
            Collections.sort(b);

            return nodes.equals(ss.getNodes());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.nodes);
        return hash;
    }

    @Override
    public String toString() {
        return "SuperState {ID: " + id + ", nodes: " + this.nodes + "}";
    }

}

class Transition {

    private final SuperState depart;
    private final String input;
    private final String output;
    private final SuperState arrivee;
    private boolean hasFinal;

    public Transition(SuperState depart, char input, String output, SuperState arrivee) {
        this.depart = depart;
        this.input = "" + input;
        this.output = output;
        this.arrivee = arrivee;
    }

    public boolean hasFinal() {
        return hasFinal;
    }

    public String toContent() {
        return String.join(" ", Arrays.asList(depart.getName(), input, arrivee.getName(), output));
    }
}

/**
 *
 * @author minhhoangdang
 */
public class Transformer {

    private static String meta = "#";

    /**
     * Deterministic Finite Automata to Non-Deterministic
     *
     * @param inAutomata
     * @return
     * @throws LexemeParsingException
     * @throws CloneNotSupportedException
     */
    public static Automata DFA2NDFA(Automata inAutomata) throws LexemeParsingException, CloneNotSupportedException {

        Automata outAutomata = new Automata();
        outAutomata.getComposition().putAll(inAutomata.getComposition());
        outAutomata.getComposition().remove('T');
        outAutomata.getComposition().remove('I');
        outAutomata.getComposition().remove('F');
        outAutomata.getComposition().remove('E');

        outAutomata.getMetadata().put("filePath", inAutomata.getMetadata().get("filePath"));
        outAutomata.getMetadata().put("fileName", inAutomata.getMetadata().get("fileName"));
        outAutomata.getMetadata().put("meta", inAutomata.getMetadata().get("meta"));
        meta = (String) outAutomata.getMetadata().get("meta");

        String oldName = (String) outAutomata.getMetadata().get("fileName");
        System.out.println(oldName);
        outAutomata.getMetadata().put("fileName", oldName.substring(0, oldName.lastIndexOf(".")) + ".dfa.descr");

        /* Transformation starts */
        SuperState I = new SuperState(inAutomata.getGraph().getEntries());
        Stack<SuperState> P = new Stack();
        System.out.println(I);
        P.push(closure(I));

        List<SuperState> L = new ArrayList<>();
        List<Transition> D = new ArrayList<>();

        List<String> etats_acceptants = new ArrayList<>();

        int id = 0;
        while (!P.isEmpty()) {
            SuperState T = P.pop();
            System.out.println(T);

            if (!L.contains(T)) {
                L.add(T);
                T.setName("" + id);
                String vocab_entree = (String) inAutomata.getMetadata().get("vocab_entree");
                for (Character a : vocab_entree.toCharArray()) {
                    SuperState U = closure(transiter(T, a));
                    id++;
                    U.setName("" + id);

                    D.add(new Transition(T, a, meta, U));
                    P.push(U);
                }
            }
        }

        System.out.println(L.size());
        for (SuperState ss : L) {
            //ss.setName("" + L.indexOf(ss));
            if (ss.isFinal() && !etats_acceptants.contains(ss.getName())) {
                etats_acceptants.add(ss.getName());
            }
        }

        outAutomata.addToComposition('I', L.get(0).getName());
        outAutomata.addToComposition('F', String.join(" ", etats_acceptants));
        outAutomata.addToComposition('E', "" + (id + 1));
        for (Transition d : D) {
            System.out.println("Adding T " + d.toContent());
            outAutomata.addToComposition('T', d.toContent());
        }

        System.out.println(outAutomata);
        return outAutomata;
    }

    /**
     *
     * @param T
     * @return
     */
    private static SuperState closure(SuperState T) {
        Stack<Node> p = new Stack();
        p.addAll(T.getNodes());
        List<Node> F = new ArrayList<>();

        while (!p.isEmpty()) {
            Node t = p.pop();
            if (!F.contains(t)) {
                F.add(t);
                for (Node u : t.getChildByInput(meta)) {
                    p.push(u);
                }
            }
        }

        return new SuperState(F);
    }

    /**
     *
     * @param T
     * @param input
     * @return
     */
    private static SuperState transiter(SuperState T, char input) {
        List<Node> F = new ArrayList<>();
        for (Node t : T.getNodes()) {
            for (Node u : t.getChildByInput("" + input)) {
                if (!F.contains(u)) {
                    F.add(u);
                }
            }
        }

        return new SuperState(F);
    }
}
