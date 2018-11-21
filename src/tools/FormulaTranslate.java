/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author raven
 */
public class FormulaTranslate {
    
    String formula;
    Map<String, String> translateDic = new LinkedHashMap<>();

    public FormulaTranslate() {
        translateDic.put("[t]", "");
        translateDic.put("[", "(");
        translateDic.put("]", ")");
        translateDic.put(" ", "*");
        translateDic.put("Cos", "cos");
        translateDic.put("Sin", "sin");
        translateDic.put("l1", "l[0]");
        translateDic.put("l2", "l[1]");
        translateDic.put("l3", "l[2]");
        translateDic.put("th1", "th[0]");
        translateDic.put("th2", "th[1]");
        translateDic.put("th3", "th[2]");
        //translateDic.put("[1]", "[0]");
        //translateDic.put("[2]", "[1]");
        //translateDic.put("[3]", "[2]");
    }
    
    void inputFormula() {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
 
        System.out.println("---Input Mathematica Formula---");
 
        try {
            formula = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void translate() {
        String str = formula;
        
        //str = str.replaceAll("([a-z]+)([0-9]+)", "$1%%$2%%]");
        
        for (String key : translateDic.keySet()) {
            str = str.replace(key, translateDic.get(key));
        }
        
        //str = str.replaceAll("%%([0-9]+)%%", "[$1]");
        
        System.out.println("---Translated Result---\r\n"+str);
        
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(str);
        clipboard.setContents(selection, null);
    }
    
    
    public static void main(String args[]) {

        FormulaTranslate formulaTest = new FormulaTranslate();
        formulaTest.inputFormula();
        formulaTest.translate();
    }
}
