/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrix;

import Jama.Matrix;

/**
 *
 * @author Char Aznable
 */
public class MatrixTest {
    
    public static void main(String args[]) {
        
        Matrix a = new Matrix(new double[]{1., 1., 1.}, 3);
        a.print(1,0);
        System.out.println(a.norm2());
        System.out.println(a.get(0, 0));
    }
}
