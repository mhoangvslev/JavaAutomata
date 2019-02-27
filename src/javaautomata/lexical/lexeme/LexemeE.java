/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.lexical.lexeme;

import java.util.ArrayList;
import java.util.List;
import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 *
 * @author minhhoangdang
 */
public class LexemeE extends Lexeme {

    public LexemeE(char symbol, String content) throws LexemeParsingException {
        super(symbol, content, "[" + REGEX_NUM + "]+");
    }

    @Override
    public List<String> getTokenisedContent(String str) {
        List<String> result = new ArrayList<>();
        result.add(str);
        return result;
    }
}
