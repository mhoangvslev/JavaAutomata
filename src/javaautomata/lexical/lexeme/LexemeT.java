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
public class LexemeT extends Lexeme {

    public LexemeT(char symbol, String content) throws LexemeParsingException {
        super(symbol, content, "^[" + REGEX_NUM + "]+\\s"+REGEX_STR_DELIM+"{0,1}[" + REGEX_ALPHA + REGEX_META + "]"+REGEX_STR_DELIM+"{0,1}\\s[" + REGEX_NUM + "]+(\\s"+REGEX_STR_DELIM+"{0,1}[" + REGEX_ALPHA + REGEX_META + "]"+REGEX_STR_DELIM+"{0,1}){0,1}\\s*$");

    }
}
