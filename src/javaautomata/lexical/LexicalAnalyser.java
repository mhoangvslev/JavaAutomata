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
import java.util.List;
import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 *
 * @author minhhoangdang
 */
public class LexicalAnalyser {

    private List<Lexeme> lexemes;
    private String line = "";
    private int success;

    /**
     * Constructeur
     */
    public LexicalAnalyser() {
        this.lexemes = new ArrayList<>();
        this.success = 1;
    }

    public void readFile(String fileName) {
        this.success = 1;
        File file = new File(fileName);
        if (file.exists()) {
            System.out.println("Examining file " + file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                while ((line = br.readLine()) != null) {
                    Lexeme lexeme = new LexemeBuilder(line).createLexeme();
                    lexemes.add(lexeme);
                    success++;
                }

            } catch (FileNotFoundException ex) {
                System.out.println("File not found! \n" + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Problem reading file");
            } catch (LexemeParsingException ex) {
                System.out.println(ex.getMessage() + " line[" + success + "]: " + line);
            }
        }
    }

    public static void main(String[] args) {
        File[] dirs = {new File("Exemples_Test_Moteur"), new File("Exemples_Test_Determinisation")};
        LexicalAnalyser analyser = new LexicalAnalyser();

        for (File dir : dirs) {
            System.out.println("\n>>> " + dir.getAbsolutePath());
            for (final File file : dir.listFiles()) {
                analyser.readFile(file.getAbsolutePath());
            }
        }
    }

}
