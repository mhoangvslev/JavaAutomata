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

    /**
     * REGEX component representing "not word" (\W)
     */
    public final static String REGEX_META = "(\\W)";

    /**
     * REGEX component representing "alphanumeric" (\w)
     */
    public final static String REGEX_ALPHA = "\\w";

    /**
     * REGEX component representing digit
     */
    public final static String REGEX_NUM = "0-9";

    /**
     * REGEX component representing quotation marks
     */
    public final static String REGEX_STR_DELIM = "[\'\"]";

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
     * Returns the content of a lexeme
     *
     * @return List of tokens
     */
    public List<String> getContent() {
        return content;
    }

    /**
     *
     * @return the regex pattern used for syntatic analysis
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Tokenise the contents
     *
     * @param str
     * @return List of tokens
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
     * Return the lexeme class
     *
     * @return the class of the lexeme
     */
    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol + " " + content;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Lexeme) {
            Lexeme l = (Lexeme) o;
            return l.toString().equals(o.toString());
        }
        return false;
    }

}
