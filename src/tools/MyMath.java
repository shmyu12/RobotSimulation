/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import static java.lang.Math.abs;
import static java.lang.Math.random;

/**
 *
 * @author Char Aznable
 */
public class MyMath {
    
    public static double RangeRandom(double min, double max) {
        
        return min + abs(max-min)*random();
    }
    
    public static int RangeRandom(int min, int max) {
        max++;
        return (int)(abs(max-min)*random()) + min;
    }
    
    public static void main(String[] args) {
        
        int[] a = new int[6];
        
        for(int i=0; i< 600; i++){
            int tmp = RangeRandom(1, 6);
            for(int j=0; j<6; j++) {
                if (tmp==j+1) a[j]++;
            }
        }
        
        
        for(int hoge: a) {
            System.out.println(hoge);
        }
        //System.out.println(RangeRandom(1, 6));
    }
}
