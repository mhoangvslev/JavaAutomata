/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.automaton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaautomata.analyser.LexicalAnalyser;
import javaautomata.analyser.SemanticAnalyser;
import javaautomata.analyser.SyntaxAnalyser;
import javaautomata.lexical.exceptions.LexemeParsingException;
import javaautomata.lexical.lexeme.Lexeme;

/**
 *
 * @author minhhoangdang
 */
public class Automaton {

    private final Map<Character, List<Lexeme>> composition;
    private final Map<String, Object> metadata;
    private final Graph graph;

    /**
     * Build an automate from scratch
     */
    public Automaton() {
        this.composition = new LinkedHashMap<>();
        this.metadata = new LinkedHashMap<>();
        this.graph = new Graph(this);
    }

    /**
     * Build an automate from descrFile
     *
     * @param fileName
     */
    public Automaton(String fileName) {
        this.composition = new LinkedHashMap<>();
        this.metadata = new LinkedHashMap<>();
        this.graph = new Graph(this);
        this.fromDescr(fileName);
    }

    /**
     * Import description file
     *
     * @param fileName
     */
    public void fromDescr(String fileName) {
        int success = 1;
        String line = "";

        File file = new File(fileName);

        if (!file.exists()) {
            return;
        }

        System.out.println("Examining file " + file);
        this.metadata.put("fileName", file.getName());
        this.metadata.put("filePath", file.getParent() + '/');

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            ArrayList<Lexeme> lexemes = new ArrayList<>();
            Lexeme lexeme = null;
            while ((line = br.readLine()) != null) {
                lexeme = new LexicalAnalyser(line).createLexeme();
                lexemes.add(lexeme);

                if (lexeme.getSymbol() == 'T') {
                    continue;
                } else {
                    this.composition.put(lexeme.getSymbol(), (List) lexemes.clone());
                    lexemes = new ArrayList<>();
                }
                success++;
            }

            this.composition.put(lexeme.getSymbol(), (List) lexemes.clone());

            System.out.println("\t> SYNTAX OK!");

        } catch (FileNotFoundException ex) {
            System.out.println("File not found! \n" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Problem reading file");
        } catch (LexemeParsingException ex) {
            System.out.println(ex.getMessage() + " line[" + success + "]: " + line);
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Convert to Graphviz .dot file
     * <a href="https://martin-thoma.com/how-to-draw-a-finite-state-machine/">Source</a>
     *
     * @throws java.io.IOException
     */
    public void toDot(String arg) throws IOException {

        String dir = (arg.equals("default")) ? (String) this.metadata.get("filePath") : arg;

        String fileName = (String) this.metadata.get("fileName");
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String dotFile = dir + "/" + fileName + ".dot";
        System.out.println(dir);

        /* Initialisation */
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(dotFile))) {
            /* Initialisation */
            fw.write("digraph G {\r\n" + "\trankdir=LR\r\n");

            /* Les formes pour les etats acceptants */
            List<String> finalStates = (List<String>) this.metadata.get("etats_acceptants");
            for (String state : finalStates) {
                fw.write("\tnode [shape = doublecircle]; " + state + ";\r\n");
            }

            fw.write("\tnode [shape = point ]; qi\r\n"
                    + "\tnode [shape = circle];\r\n");

            /* Les formes pour les états inits */
            List<String> initStates = (List<String>) this.metadata.get("etats_init");
            for (String state : initStates) {
                fw.write("\tqi -> " + state + ";\r\n");
            }

            /* Ecrire les transitions */
            for (Lexeme lex : this.composition.get('T')) {
                String e = lex.getContent().get(0);
                String eprime = lex.getContent().get(2);
                String a = lex.getContent().get(1);
                String o = lex.getContent().get(3);

                fw.write("\t" + e + " -> " + eprime + " [ label=\"" + a + "/" + o + "\" ];\r\n");
            }
            fw.write("}\r\n");

            /* Finalisation */
            fw.flush();
            fw.close();
        }
    }

    /**
     * Convert to PNG
     *
     * @param arg
     * @throws IOException
     */
    public void toPng(String arg) throws IOException {

        /*to dot first*/
        toDot(arg);

        String dir = (arg.equals("default")) ? (String) this.metadata.get("filePath") : arg;
        String fileName = (String) this.metadata.get("fileName");
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String pngFile = dir + "/" + fileName + ".png";

        Process p;
        p = new ProcessBuilder("dot", "-Tpng", dir + "/" + fileName + ".dot", "-o", pngFile).start();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // read the output from the command
        String s = null;
        System.out.println(">> Converting to png \n");
        while ((s = in.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println(">> Error of the command (if any):\n");
        while ((s = err.readLine()) != null) {
            System.out.println(s);
        }

        p.destroy();
    }

    /**
     * Export to description file
     *
     * @param arg
     * @throws IOException
     */
    public void toDescr(String arg) throws IOException {

        String dir = (arg.equals("default")) ? (String) this.metadata.get("filePath") : arg;

        String fileName = (String) this.metadata.get("fileName");
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String descrFile = dir + "/" + fileName + ".descr";

        try (BufferedWriter fw = new BufferedWriter(new FileWriter(descrFile))) {
            String ref = "CMVOEIFT";
            for (char symbol : ref.toCharArray()) {
                for (Lexeme lex : this.composition.get(symbol)) {
                    String content = String.join(" ", lex.getContent());
                    fw.write(symbol + " " + content);
                }
            }

            fw.flush();
            fw.close();
        }

    }

    public void addToComposition(Character key, String input) throws LexemeParsingException {

        if (key != 'T' && this.composition.get(key) != null) {
            throw new LexemeParsingException("Syntax error: On ne peut pas avoir plusieur item de clé " + key);
        }

        //System.out.println("Adding " + key + " " + input);
        Lexeme value = new LexicalAnalyser(key + " " + input).createLexeme();

        List<Lexeme> temp = this.composition.get(key);
        temp = (temp == null) ? new ArrayList<>() : temp;

        if (!temp.contains(key)) {
            temp.add(value);
            this.composition.put(key, temp);
        }
    }

    /**
     * Get the composition of an automaton
     *
     * @return the dictionary containing all the normalised lexeme.
     */
    public Map<Character, List<Lexeme>> getComposition() {
        return composition;
    }

    /**
     * The metadata of the given automaton, retrievable using the following keys
     * meta, vocab_entree, vocab_sortie, nb_etats, etats_init, etats_acceptants,
     * estDeterministe
     *
     * @return the dictionary containing all the infos
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Graph getGraph() {
        return graph;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Automaton res = new Automaton();
        try {
            res.getComposition().putAll(this.composition);
            res.getMetadata().putAll(this.metadata);

            SyntaxAnalyser sa = new SyntaxAnalyser(res);
            sa.checkSyntax();

            SemanticAnalyser sea = new SemanticAnalyser(res);
            sea.checkDeterministic();

            res.getGraph().buildGraph();

        } catch (LexemeParsingException ex) {
            Logger.getLogger(Automaton.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Automaton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;

    }

    @Override
    public String toString() {
        return "INFO AUTOMATE: \n"
                + "\t>> Composition: " + this.composition
                + "\t>> Metadata: " + this.metadata;
    }

}
