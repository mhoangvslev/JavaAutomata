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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author minhhoangdang
 */
public class LexicalAnalyser {
    
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
                
            } catch (FileNotFoundException ex) {
                System.out.println("File not found! \n" + ex.getMessage());
            }
        }
    }
    
    
}
