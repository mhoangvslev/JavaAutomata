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
public class LexemeT extends Lexeme {

    public LexemeT(char symbol, String content) throws LexemeParsingException {
        super(symbol, content, "^[0-9]+\\s\'[A-Za-z#]\'\\s[0-9]+\\s\'[A-Za-z#]\'$");
        
    }    
    
}
