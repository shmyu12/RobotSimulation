/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package study;

import Jama.Matrix;
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
        List<Test> list2 = new ArrayList<>(list);
        for(Test t: list) System.out.println(t.str);        
        //a = list.get(0);
        //if (a == list.get(0)) System.out.println("参照コピー");
        //a = list.get(1);
        //list.set(1, c);
        //a = c;
        a.str = "d";
        //a = b;
        //if (a == b) System.out.println("参照コピー");
        for(Test t: list2) System.out.println(t.str);
        
        double[] gene = new double[]{1., 2., 3.};
        System.out.println(Arrays.toString(gene));
        double[] clone = new double[3];
        System.arraycopy(gene, 0, clone, 0, 3);
        System.out.println(Arrays.toString(clone));
        
        Integer count = 0;
        System.out.println(count);
        a.func(count);
        System.out.println(count);
        
        double[] arr = new double[]{1., 2, 3., 4.};
        double[][] brr = new double[arr.length][1];
        for (int i=0; i<brr.length; i++) {
            brr[i][0] = arr[i];
        }
        Matrix marr = new Matrix(arr, arr.length);
        arr[0] = 5.;
        marr.print(2, 2);
        new Matrix(brr).print(2, 2);
        //Matrix m = new Matrix(arr);
        //m.print(2, 2);
        //Matrix n = new Matrix(m.getArray());
        //arr[2][2] = 5.5;
        //m.print(2, 2);
        //m.set(1, 1, 10.2);
        //m.timesEquals(10.);
        //System.out.println(arr[0][0]);
        
    }
        
}

class Test {
    public int num;
    public String str;
    //public Integer count;
    
    public Test(int a, String b) {
        num = a;
        str = b;
    }
    
    public void func(Integer count) {
        count++;
    }

}
