/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.analyser;

import javaautomata.lexical.lexeme.*;
import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 * Substrings read from .descr files
 *
 * @author minhhoangdang
 */
public class LexicalAnalyser {

    private final char symbol;
    private final String content;

    /**
     * Constructor
     * @param line the line fed from file reading process
     * @throws LexemeParsingException
     */
    public LexicalAnalyser(String line) throws LexemeParsingException {
        symbol = line.charAt(0);
        evaluateLexemeClass(symbol); // will exit if fails
        content = line.substring(2);
    }

    /**
     * Check whether the symbol is valid
     * @param symbol the first symbol extracted
     * @throws LexemeParsingException
     */
    private void evaluateLexemeClass(char symbol) throws LexemeParsingException {
        String ref = "CMVOEIFT";
        if (!ref.contains("" + symbol)) {
            throw new LexemeParsingException("Symbol invalide!" + symbol);
        }
    }

    /**
     * Generate subclass lexemes
     * @return a lexeme
     * @throws javaautomata.lexical.exceptions.LexemeParsingException
     */
    public Lexeme createLexeme() throws LexemeParsingException {
        switch (symbol) {
            case 'C':
                return new LexemeC(symbol, content);
            case 'M':
                return new LexemeM(symbol, content);
            case 'V':
                return new LexemeV(symbol, content);
            case 'O':
                return new LexemeO(symbol, content);
            case 'E':
                return new LexemeE(symbol, content);
            case 'I':
                return new LexemeI(symbol, content);
            case 'F':
                return new LexemeF(symbol, content);
            case 'T':
                return new LexemeT(symbol, content);

            default:
                return null;
        }
    }

    public static void main(String[] args) {
        try {
            String testLine = "T 0 'p' 2 'p'";
            Lexeme testLex = new LexicalAnalyser(testLine).createLexeme();
            System.out.println(testLex);
        } catch (LexemeParsingException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
