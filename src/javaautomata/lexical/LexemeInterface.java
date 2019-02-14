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
public interface LexemeInterface {

    /**
     * Evaluate the content of the lexeme
     * @throws LexemeParsingException 
     */
    public void evaluate() throws LexemeParsingException;
    public String getPattern();
}
