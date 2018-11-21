/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotsimulation;

import Jama.Matrix;

/**
 *
 * @author raven
 */
abstract public class Robot {
    
    protected final double[] th;    //関節角度変数[rad]
    protected final double[] l;     //リンク長[m]
    double rho;     //リンク線密度[kg/m]
    double r;       //リンク断面の半径(円筒近似)[m]
    double me;      //エンドエフェクタ質量[kg]
    final int dof;  //自由度
    
    public Robot(int dof) {
        this.dof = dof;
        th = new double[dof];
        l = new double[dof];
    }
    
    public final void setLength(double[] l) {
        System.arraycopy(l, 0, this.l, 0, dof);
    }
    
    public final void setTheta(double[] th) {
        System.arraycopy(l, 0, this.l, 0, dof);
    }
    
    public final void setDensity(double rho) {
        this.rho = rho;
    }
    
    public final void setRadius(double r) {
        this.r = r;
    }
    
    public final void setMassOfEndeffector(double me) {
        this.me = me;
    }
    
    public final double[] getLength() {
        double[] ret = new double[l.length];
        System.arraycopy(l, 0, ret, 0, l.length);
        return ret;
    }
    
    public final double[] getTheta() {
        double[] ret = new double[th.length];
        System.arraycopy(th, 0, ret, 0, th.length);
        return ret;
    }
    
    public final double getDensity() {
        return rho;
    }
    
    public final double getRadius() {
        return r;
    }
    
    public final double getMassOfEndeffector() {
        return me;
    }
    
    public final int getDof() {
        return dof;
    }

    
    abstract Matrix jacobian(double[] th);
    abstract double[] kinematics(double[] th);
    abstract double[] invKinematics(double[] r, double[] th, double precision);
}
