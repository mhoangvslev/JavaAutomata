/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.lexical;

import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 *
 * @author minhhoangdang
 */
public abstract class Lexeme implements LexemeInterface {

    final char symbol;
    final String content;
    final String pattern;
        
    /**
     * Constructor
     *
     * @param symbol
     * @param content
     * @throws javaautomata.lexical.exceptions.LexemeParsingException
     */
    public Lexeme(char symbol, String content) throws LexemeParsingException {
        this.symbol = symbol;
        this.content = content;
        this.pattern = "";
        this.evaluate();
    }
    
    public Lexeme(char symbol, String content, String pattern) throws LexemeParsingException {
        this.symbol = symbol;
        this.content = content;
        this.pattern = pattern;
        this.evaluate();
    }
    
    @Override
    public void evaluate() throws LexemeParsingException{
        //System.out.println("Evaluating using " + pattern);
        if(!content.matches(pattern))
            throw new LexemeParsingException("Syntax error!");
    }
    
    @Override
    public String getPattern(){
        return pattern;
    }

    @Override
    public String toString() {
        return this.getClass().toString() + ": " + symbol + content + " eval using " + pattern;
    }
}
