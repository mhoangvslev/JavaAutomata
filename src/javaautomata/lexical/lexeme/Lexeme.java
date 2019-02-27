/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.lexical.lexeme;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 *
 * @author minhhoangdang
 */
public abstract class Lexeme {

    final char symbol;
    final List<String> content;
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
        this.content = getTokenisedContent(cleanContent(content));
        this.pattern = "";
        evaluate(content);
    }

    /**
     *
     * @param symbol
     * @param content
     * @param pattern
     * @throws LexemeParsingException
     */
    public Lexeme(char symbol, String content, String pattern) throws LexemeParsingException {
        this.symbol = symbol;
        this.content = getTokenisedContent(cleanContent(content));
        this.pattern = pattern;
        evaluate(content);
    }

    /**
     *
     * @param str
     * @return
     */
    private String cleanContent(String str) {
        return str.replaceAll(REGEX_STR_DELIM, "");
    }

    /**
     *
     * @param str
     * @throws LexemeParsingException
     */
    private void evaluate(String str) throws LexemeParsingException {
        if (!str.matches(pattern)) {
            throw new LexemeParsingException("Syntax error {pattern = '" + pattern + "', expr = '" + content + "' !");
        }
    }

    /**
     *
     * @return
     */
    public List<String> getContent() {
        return content;
    }

    /**
     *
     * @return
     */
    public String getPattern() {
        return pattern;
    }

    /**
     *
     * @return
     */
    public List<String> getTokenisedContent(String str) {
        List<String> result = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(str);
        while (st.hasMoreTokens()) {
            String temp = st.nextToken();
            result.add(temp);
        }

        return result;
    }

    /**
     *
     * @return
     */
    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return this.getClass().toString() + ": " + symbol + content + " eval using " + pattern;
    }
}
