/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.lexical;

import javaautomata.lexical.lexeme.Lexeme;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javaautomata.lexical.exceptions.LexemeParsingException;
import javaautomata.lexical.lexeme.*;

/**
 *
 * @author minhhoangdang
 */
public class LexicalAnalyser {

    private final Map<Character, List<Lexeme>> composition;
    private final Map<String, Object> metadata;
    private String line = "";
    private int success;

    /**
     * Constructeur
     */
    public LexicalAnalyser() {
        this.composition = new HashMap<>();
        this.metadata = new HashMap<>();
        this.success = 1;
    }

    /**
     * Lire les fichiers et check la syntaxe
     *
     * @param fileName
     */
    public void readFile(String fileName) {
        this.success = 1;
        File file = new File(fileName);
        if (file.exists()) {
            System.out.println("Examining file " + file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                ArrayList<Lexeme> lexemes = new ArrayList<>();
                Lexeme lexeme = null;
                while ((line = br.readLine()) != null) {
                    lexeme = new LexemeBuilder(line).createLexeme();
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
                checkSemantic();

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
    }

    /**
     *
     * @throws LexemeParsingException
     */
    public void checkSemantic() throws LexemeParsingException, NumberFormatException {
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
            throw new LexemeParsingException("Semantic error (V): Pas de vocabulaire d'entrée");
        }

        if (!this.composition.containsKey('E')) {
            throw new LexemeParsingException("Semantic error (E): Le nombre d'état n'est pas précisé");
        }

        if (!this.composition.containsKey('F')) {
            throw new LexemeParsingException("Semantic error: Pas d'états acceptants");
        }
        
        if (!this.composition.containsKey('T')) {
            throw new LexemeParsingException("Semantic error: Pas de transition!");
        }

        /*Summarise*/
        this.metadata.put("meta", this.getData('M', 0).get(0));
        this.metadata.put("vocab_entree", this.getData('V', 0).get(0));
        this.metadata.put("vocab_sortie", this.getData('O', 0).get(0));
        this.metadata.put("nb_etats", Integer.parseInt(this.getData('E', 0).get(0)) + 1);
        this.metadata.put("etats_init", this.getData('I', 0).toString());
        this.metadata.put("etats_acceptants", this.getData('F', 0).toString());

        System.out.println("\t>> Summarise: " + this.metadata);

        /* Coherence check in T lexemes*/
        for (Lexeme lex : this.composition.get('T')) {

            /* Harmoniser à quatre attributs */
            if (lex.getContent().size() == 3) {
                lex.getContent().add("" + this.metadata.get("meta"));
            }

            for (int i = 0; i < lex.getContent().size(); i++) {
                String token = lex.getContent().get(i);

                // Pour les attributs 1 et 3, vérifier si l'ID est entre [0, N-1].
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
                    if (!vocabSortie.contains(token)) {
                        throw new LexemeParsingException("L'ID de l'état '" + token + "' doit être dans '" + vocabSortie + "'");
                    }
                }
            }
        }
        
        /* Si on arrive ici, alles gut! */
        System.out.println("\t> SEMANTIC OK!");
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

    public static void main(String[] args) throws IOException {
        File[] dirs = {new File("Exemples_Test_Moteur"), new File("Exemples_Test_Determinisation")};
        Map<String, LexicalAnalyser> moteurs = new HashMap<>();

        for (File dir : dirs) {
            System.out.println("\n>>> " + dir.getAbsolutePath());
            for (final File file : dir.listFiles()) {
                LexicalAnalyser analyser = new LexicalAnalyser();
                moteurs.put(file.getName(), analyser);
                analyser.readFile(file.getAbsolutePath());
            }
        }
    }

}
