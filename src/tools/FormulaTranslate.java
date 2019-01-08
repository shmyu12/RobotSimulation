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
        //translateDic.put("[t]", "");
        //translateDic.put("[", "(");
        //translateDic.put("]", ")");
        translateDic.put(" ", "*");
        translateDic.put("Cos", "cos");
        translateDic.put("Sin", "sin");
        //translateDic.put("l1", "l[0]");
        //translateDic.put("l2", "l[1]");
        //translateDic.put("l3", "l[2]");
        //translateDic.put("th1", "th[0]");
        //translateDic.put("th2", "th[1]");
        //translateDic.put("th3", "th[2]");
        //translateDic.put("lext1", "lext[0]");
        //translateDic.put("lext2", "lext[1]");
        //translateDic.put("lext3", "lext[2]");
        //translateDic.put("c1", "counterWeight[0]");
        //translateDic.put("c2", "counterWeight[1]");
        //translateDic.put("c3", "counterWeight[2]");
        //translateDic.put("m1", "mass[0]");
        //translateDic.put("m2", "mass[1]");
        //translateDic.put("m3", "mass[2]");
        
        translateDic.put("[1]", "[0]");
        translateDic.put("[2]", "[1]");
        translateDic.put("[3]", "[2]");
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
        
        str = str.replaceAll("\\[t\\]", "");    //[t]消去
        str = str.replaceAll("\\[([0-9])+\\]", "_$1_"); //配列[]退避
        str = str.replaceAll("(Cos|Sin)\\[([^\\[\\]]+)\\]\\^([0-9]+)", "pow($1($2),$3)");   //第一次pow変換
        str = str.replaceAll("([\\+\\-\\(\\)/ ^.])([^\\+\\-\\(\\)/ ]+)\\^([0-9])", "$1pow($2,$3)"); //第二次pow変換
        str = str.replaceAll("([^_[0-9]])([0-9]+)([^_[0-9]])", "$1$2.$3");  //第一次小数表現
        str = str.replaceAll("([^_[0-9]])([0-9]+)([^\\.])", "$1$2.$3"); //第二次小数表現
        str = str.replaceAll("_([0-9]+)_", "[$1]"); //退避[]戻し
        
        for (String key : translateDic.keySet()) {
            str = str.replace(key, translateDic.get(key));
        }
        
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
