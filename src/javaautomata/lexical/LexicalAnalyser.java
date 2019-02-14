/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaautomata.lexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaautomata.lexical.exceptions.LexemeParsingException;

/**
 *
 * @author minhhoangdang
 */
public class LexicalAnalyser {
    
    private List<Lexeme> lexemes;
    private String line = "";
    private int success;
    
    /**
     * Constructeur
     */
    public LexicalAnalyser(){
        
    }
    
    public void readFile(String fileName){        
        File file = new File(fileName);
        if(file.exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                
                while( (line = br.readLine()) != null){
                    Lexeme lexeme = new LexemeBuilder(line).createLexeme();
                    lexemes.add(lexeme);
                    success++;
                }
                
            } catch (FileNotFoundException ex) {
                System.out.println("File not found! \n" + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Problem reading file");
            } catch (LexemeParsingException ex) {
                System.out.println(ex.getMessage() + "line["+ success +"]: " + line);
            }
        }
    }
    
    
}
