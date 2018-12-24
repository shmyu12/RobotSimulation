/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import static java.lang.Math.abs;

/**
 *
 * @author Char Aznable
 */
public class MyMath {
    
    public static double rangeRandom(double min, double max) {
        
        return min + abs(max-min)*MT19937.nextDouble();
    }
    
    public static int rangeRandom(int min, int max) {
        max++;
        return (int)(abs(max-min)*MT19937.nextDouble()) + min;
    }
    
    public static double[][] transTwoDimArray(double[] a) {
        double[][] b = new double[a.length][1];
        for (int i=0; i<a.length; i++) {
            b[i][0] = a[i];
        }
        return b;
    }
    
    public static void main(String[] args) {
        
        int[] a = new int[6];
        
        for(int i=0; i< 600; i++){
            int tmp = rangeRandom(1, 6);
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
