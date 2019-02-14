/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.lexical;

import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 * Substrings read from .descr files
 *
 * @author minhhoangdang
 */
public class LexemeBuilder {

    private final char symbol;
    private final String content;

    public LexemeBuilder(String line) throws LexemeParsingException {
        symbol = line.charAt(0);
        evaluateLexemeClass(symbol); // will exit if fails
        content = line.substring(2);
    }

    /**
     * Check whether the symbol is valid
     *
     * @param symbol the first symbol extracted
     * @throws LexemeParsingException
     */
    private void evaluateLexemeClass(char symbol) throws LexemeParsingException {
        String ref = "CMVOEOFT";
        if (!ref.contains("" + symbol)) {
            throw new LexemeParsingException("Symbol invalide");
        }
    }

    /**
     * Generate subclass lexemes
     *
     * @return
     * @throws javaautomata.lexical.exceptions.LexemeParsingException
     */
    public Lexeme createLexeme() throws LexemeParsingException {
        switch (symbol) {
            case 'T':
                return new LexemeT(symbol, content);
            case 'C':
                return new LexemeC(symbol, content);
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        try {
            String testLine = "T 0 'p' 2 'p'";
            Lexeme testLex = new LexemeBuilder(testLine).createLexeme();
            System.out.println(testLex);
        } catch (LexemeParsingException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
