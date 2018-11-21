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
        
        Test a = new Test(1, "a");
        Test b = new Test(2, "b");
        Test c = new Test(3, "c");
        
        List<Test> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        for(Test t: list) System.out.println(t.str);
        //a = list.get(0);
        if (a == list.get(0)) System.out.println("参照コピー");
        //a = list.get(1);
        list.set(1, c);
        a = c;
        a.str = "d";
        //a = b;
        if (a == b) System.out.println("参照コピー");
        for(Test t: list) System.out.println(t.str);
        
        double[] gene = new double[]{1., 2., 3.};
        System.out.println(Arrays.toString(gene));
        double[] clone = new double[3];
        System.arraycopy(gene, 0, clone, 0, 3);
        System.out.println(Arrays.toString(clone));
        
    }
        
}

class Test {
    public int num;
    public String str;
    
    public Test(int a, String b) {
        num = a;
        str = b;
    }

}
