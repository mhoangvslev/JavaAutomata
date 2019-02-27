/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.lexical.lexeme;

import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 *
 * @author minhhoangdang
 */
public abstract class Lexeme implements LexemeInterface {

    final char symbol;
    final String content;
    final String pattern;
    
    final static String REGEX_META = "(\\w\\S)";
    final static String REGEX_ALPHA = "\\w";
    final static String REGEX_NUM = "0-9";
    final static String REGEX_STR_DELIM = "[\'\"]";
        
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
        evaluate();
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
            throw new LexemeParsingException("Syntax error {pattern = '"+ pattern +"', expr = '"+ content +"' !");
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
