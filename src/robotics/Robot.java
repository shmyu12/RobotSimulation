/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotics;

import Jama.Matrix;

/**
 *
 * @author raven
 */
abstract public class Robot {
    
    final double[] jointAngle;    //関節角度変数[rad]
    final double[] linkLength;     //リンク長[m]
    //double density;     //リンク線密度[kg/m]
    //double radius;       //リンク断面の半径(円筒近似)[m]
    //double me;      //エンドエフェクタ質量[kg]
    final int dof;  //自由度
    final double g = 9.80665;
    
    public Robot(int dof) {
        this.dof = dof;
        jointAngle = new double[dof];
        linkLength = new double[dof];
    }
    
    public final void setLength(double[] linkLength) {
        System.arraycopy(linkLength, 0, this.linkLength, 0, dof);
    }
    
    public final void setAngle(double[] jointAngle) {
        System.arraycopy(jointAngle, 0, this.jointAngle, 0, dof);
    }
    
    public final double[] getLength() {
        double[] ret = linkLength.clone();
        return ret;
    }
    
    public final double[] getAngle() {
        double[] ret = jointAngle.clone();
        return ret;
    }
    
    public final int getDof() {
        return dof;
    }

    
    abstract Matrix jacobian();
    abstract double[] kinematics();
    abstract double[] invKinematics(double[] r, double precision);
}
