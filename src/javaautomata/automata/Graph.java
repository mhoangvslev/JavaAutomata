/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.automata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaautomata.lexical.lexeme.Lexeme;

class Label implements Comparable {

    private final String input;
    private final String output;

    public Label(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return input + "/" + output;
    }

    @Override
    public int compareTo(Object t) {
        return this.toString().compareTo(t.toString());
    }

}

/**
 * Node in a graph
 *
 * @author minhhoangdang
 */
class Node implements Comparable {

    private final String nodeName;
    private final boolean isInit;
    private final boolean isFinal;
    private Node parentNode;
    private final Map<Node, TreeSet<Label>> children;

    public Node(String nodeName, Node parentNode, boolean isInit, boolean isFinal) {
        this.nodeName = nodeName;
        this.parentNode = parentNode;
        this.children = new LinkedHashMap<>();
        this.isInit = isInit;
        this.isFinal = isFinal;
    }

    public void addChild(String input, String output, Node child) {
        TreeSet<Label> temp = this.children.get(child);
        temp = temp == null ? new TreeSet<>() : temp;

        Label cond = new Label(input, output);
        temp.add(cond);

        this.children.put(child, temp);
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setParentNode(Node parent) {
        this.parentNode = parent;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public Collection<String> getTransitionInput(Node child) {
        TreeSet<String> res = new TreeSet<>();
        for (Label label : this.children.get(child)) {
            res.add(label.getInput());
        }
        return res;
    }

    public TreeSet<String> getTransitionOutput(Node child) {
        TreeSet<String> res = new TreeSet<>();
        for (Label label : this.children.get(child)) {
            res.add(label.getOutput());
        }
        return res;
    }

    public Collection<Node> getChildByInput(String input) {
        TreeSet<Node> result = new TreeSet<>();
        for (Node node : this.children.keySet()) {
            if (this.getTransitionInput(node).contains(input)) {
                result.add(node);
            }
        }
        return result;
    }

    public Collection<Node> getChildren() {
        return this.children.keySet();
    }

    public boolean isInit() {
        return isInit;
    }

    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!Objects.equals(this.nodeName, other.nodeName)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object t) {
        Node n = (Node) t;
        return n.getNodeName().compareTo(nodeName);
    }

}

/**
 *
 * @author minhhoangdang
 */
public class Graph {

    private final Map<String, Node> tree;
    private final Automata automata;
    private String log;

    /**
     *
     * @param automata
     */
    Graph(Automata automata) {
        this.automata = automata;
        this.tree = new LinkedHashMap<>();
        this.log = "";
    }

    /**
     *
     */
    public void buildGraph() {
        List<String> inits = (List<String>) automata.getMetadata().get("etats_init");
        List<String> finals = (List<String>) automata.getMetadata().get("etats_acceptants");

        for (Lexeme transition : automata.getComposition().get('T')) {

            String name = transition.getContent().get(0);
            String input = transition.getContent().get(1);
            String output = transition.getContent().get(3);
            String childName = transition.getContent().get(2);

            /* Setup current node */
            Node current = this.tree.get(name);
            if (current == null) {
                current = new Node(name, null, inits.contains(name), finals.contains(name));
                this.tree.put(current.getNodeName(), current);
            }

            /* Setup child node */
            Node child = this.tree.get(childName);
            if (child == null) {
                child = new Node(childName, current, inits.contains(childName), finals.contains(childName));
                this.tree.put(child.getNodeName(), child);
            } else {
                child.setParentNode(current);
            }

            current.addChild(input, output, child);

        }

        log("Graph path is...");
        //displayGraph(this.tree.get("0"), "-");
    }

    /**
     *
     * @param script
     */
    public void translateFromScript(String script) {
        try {
            BufferedReader br = new BufferedReader(new StringReader(script));
            String line;
            while ((line = br.readLine()) != null) {

                /* Until the end marker */
                if (line.contains("###") || line.equals("")) {
                    break;
                }

                /* Do the work*/
                interprete(line);

            }

            log("-- End of process --");
        } catch (IOException ex) {
            log("Error translating from script! Check syntax!");
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param line
     */
    private void interprete(String line) {
        log("Processing sequence '" + line + "':");

        /* Find possible translations from each entry points */
        for (Node node : this.tree.values()) {
            if (node.isInit()) {
                log("> Reading from " + node.getNodeName());
                StringCharacterIterator itr = new StringCharacterIterator(line);
                log("> Output is '" + readFrom(node, itr, itr.first()) + "'");
            }
        }
        log("-- End of line --");
    }

    /**
     *
     * @param node
     * @param itr
     * @param letter
     * @return
     */
    private String readFrom(Node node, StringCharacterIterator itr, char letter) {

        String result = "";

        log(">> Reading " + letter + " ...");
        if (letter == CharacterIterator.DONE) {
            log(">> CurrentState: " + node.getNodeName() + ", End of sequence");
            if (node.isFinal()) {
                log(">> Input is final!");
            } else {
                log(">> Input is not final!");
            }

            return result;
        }

        if (node.getChildren().isEmpty()) {
            log(">> CurrentState: " + node.getNodeName() + ", No next transition found");
            if (node.isFinal()) {
                log(">> Input is final!");
            } else {
                log(">> Input is not final!");
            }

            return result;
        }

        for (Node child : node.getChildByInput("" + letter)) {

            for (String output: node.getTransitionOutput(child)) {
                if (output.contains("#")) {
                    log(">> CurrentState: " + node.getNodeName() + ", Input: " + letter + ", [Transition found]");
                } else {
                    log(">> CurrentState: " + node.getNodeName() + ", Input: " + letter + ", Output: " + output + ", [Transition found]");
                    result += output;
                }
            }

            result += readFrom(child, itr, itr.next());

        }

        return result;
    }

    private void log(String msg) {
        this.log += msg + "\r\n";
    }

    public String getLog() {
        return log;
    }

    public void clearLog() {
        this.log = "";
    }

    public Collection<Node> getEntries() {
        List<Node> res = new ArrayList<>();
        for (Node n : this.tree.values()) {
            if (n.isInit()) {
                res.add(n);
            }
        }
        return res;
    }

    /**
     *
     * @param node
     * @param lvl
     */
    public void displayGraph(Node node, String lvl) {
        log(lvl + " " + node.getNodeName());
        lvl += lvl;
        for (Node child : node.getChildren()) {
            displayGraph(child, lvl);
        }
    }

}
