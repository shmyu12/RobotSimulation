/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Char Aznable
 */
public class Study {
    
    public static void main(String args[]) {
        
        String a = "a";
        String b = "b";
        
        List<String> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        
        for(String str: list) System.out.println(str);
        
        a = list.get(0);
        a = "adsf";
        System.out.println(a);
        for(String str: list) System.out.println(str);
        
        double[] gene = new double[]{1., 2., 3.};
        System.out.println(Arrays.toString(gene));
        double[] clone = new double[3];
        System.arraycopy(gene, 0, clone, 0, 3);
        System.out.println(Arrays.toString(clone));
        
    }
        
}
