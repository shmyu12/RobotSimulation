/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import java.util.function.Function;

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

    public static double newton(double y, Function<Double, Double> f, Function<Double, Double> df) {
        final double precision = 1e-9;
        int max = 10000;
        double z;
        do {
            z = f.apply(y)/df.apply(y);
            if(abs(z) > 1.) {
                y = y - z/abs(z);
            } else {
                y = y - z;
            }

            max--;
            System.out.println(max);
        } while(abs(f.apply(y))>precision && max>0);
        System.out.println(f.apply(y));
        return y;
    }

    public static double newton(double y, Function<Double, Double> f) {
        final double precision = 1e-9;
        int max = 10000;
        double z;
        do {
            z = f.apply(y);
            if(abs(z) > 1.) {
                y = y - z/abs(z);
            } else {
                y = y - z;
            }

            max--;
            System.out.println(max);
        } while(abs(z)>precision && max>0);
        
        return y;
    }
    
    public static double f(double y) {
        return y*y-3.;
    }
    
    public static double df(double y) {
        return 2.*y;
    }
    
    public static double myExp(double x) {
        double x1 = x%1;
        double x2 = x-x1;
        return exp(x1)*exp(x2);
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
        
        double y;
        y = newton(1., MyMath::f, MyMath::df);
        System.out.println(y);
        
        System.out.println(myExp(34.61687714866581*0.1085)*1069.7);
        System.out.println(exp(34.61687714866581*0.1085)*1069.7);
    }
}
