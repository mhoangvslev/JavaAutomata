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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaautomata.lexical.lexeme.Lexeme;

/**
 * Node in a graph
 *
 * @author minhhoangdang
 */
class Node {

    private final String nodeName;
    private final boolean isInit;
    private final boolean isFinal;
    private Node parentNode;
    private final Map<Node, String[]> children;

    public Node(String nodeName, Node parentNode, boolean isInit, boolean isFinal) {
        this.nodeName = nodeName;
        this.parentNode = parentNode;
        this.children = new HashMap<>();
        this.isInit = isInit;
        this.isFinal = isFinal;
    }

    public void addChild(String input, String output, Node child) {
        String[] cond = new String[2];
        cond[0] = input;
        cond[1] = output;
        this.children.put(child, cond);
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

    public String getTransitionInput(Node child) {
        return this.children.get(child)[0];
    }

    public String getTransitionOutput(Node child) {
        return this.children.get(child)[1];
    }

    public Collection<Node> getChildByInput(String input) {
        Collection<Node> result = new ArrayList<>();
        for (Node node : this.children.keySet()) {
            if (this.children.get(node)[0].contains(input)) {
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
        this.tree = new HashMap<>();
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
            return result;
        }

        for (Node child : node.getChildByInput("" + letter)) {
            String input = node.getTransitionInput(child);
            String output = node.getTransitionOutput(child);
            if (output.contains("#")) {
                log(">> CurrentState: " + node.getNodeName() + ", Input: " + input + ", [Transition found]");
            } else {
                log(">> CurrentState: " + node.getNodeName() + ", Input: " + input + ", Output: " + output + ", [Transition found]");
                result += output;
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

    public Map<String, Node> getTree() {
        return tree;
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
