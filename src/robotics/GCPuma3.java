/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotics;

import Jama.Matrix;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sin;
import static java.lang.Math.pow;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import static tools.MyMath.newton;
/**
 *
 * @author raven
 */
public class GCPuma3 extends Robot{

    double[] extendedLinkLength;
    double[] armWeight;
    double[] counterWeight;
    double[] sectionalArea;
    double[] polMomentOfArea;
    double[] halfWidth;
    double[] density;
    double massOfEndEffector;
    double force=20.0;
    final double allowableStress = 20.5; //205/10[N/mm4]
    
    final double aRho = 1.88780e-4;
    final double aI = 1.06971e3;
    final double aA = 7.51336e1;
    final double bRho = 5.41167e-2;
    final double bI = 1.08514e-1;
    final double bA = 5.19035e-2;
            
    private Count count;
            
    public GCPuma3(int dof) {
        super(3);
        this.extendedLinkLength = new double[3];
        this.armWeight = new double[3];
        this.counterWeight = new double[3];
        this.sectionalArea = new double[3];
        this.polMomentOfArea = new double[3];
        this.halfWidth = new double[3];
        this.density = new double[3];
        count = new Count(10);
    }
    
    public GCPuma3() {
        this(3);
    }
    
    public void optimizeMaterial() {
        halfWidth[2] = newton(50., this::fl3, this::dfl3);
        calcCounterWeight(2);
        calcArmWeight(2);
        halfWidth[1] = newton(50., this::fl2, this::dfl2);
        calcCounterWeight(1);
        calcArmWeight(1);
        halfWidth[0] = halfWidth[1];
        for (int i=0; i<3; i++) {
            sectionalArea[i] = calcSectionalArea(halfWidth[i]);
            polMomentOfArea[i] = calcPolMomentOfArea(halfWidth[i]);
            density[i] = calcLinDensity(halfWidth[i]);
        }
    }
    
    public double fl2(double y) {
        double sigma = allowableStress;
        double I = calcSecMomentOfArea(y);
        double rho = calcLinDensity(y);
        double[] l = linkLength;
        double[] m = armWeight;
        double me = massOfEndEffector;
        return y*((me+m[2])*g*l[1]+force*(l[2]+l[1])+g*l[1]*l[1]*rho/2.)/(2.*I)-sigma;
    }
    
    public double dfl2(double y) {
        double I = calcSecMomentOfArea(y);
        double rho = calcLinDensity(y);
        double[] l = linkLength;
        double[] m = armWeight;
        double me = massOfEndEffector;
        return (((me+m[2])*g*l[1]+force*(l[2]+l[1]))*(1.-bI*y)+g*l[1]*l[1]*(1.+(bRho-bI)*y)*rho/2.)/(2.*I);
    }
    
    public double fl3(double y) {
        double sigma = allowableStress;
        double I = calcSecMomentOfArea(y);
        double rho = calcLinDensity(y);
        double[] l = linkLength;
        double me = massOfEndEffector;
        return y*l[2]*(me*g+force+g*l[2]*rho/2.)/(2.*I)-sigma;
    }
    
    public double dfl3(double y) {
        double I = calcSecMomentOfArea(y);
        double rho = calcLinDensity(y);
        double[] l = linkLength;
        double me = massOfEndEffector;
        return l[2]*((me*g+force)*(1.-bI*y)+g*l[2]*(1.+(bRho-bI)*y)*rho/2.)/(2.*I);
    }
    
    public double z(double y) {
        double sigma = allowableStress;
        double I = calcSecMomentOfArea(y);
        double rho = calcLinDensity(y);
        double[] l = linkLength;
        double me = massOfEndEffector;
        System.out.println(((me*g+force)*l[2]*(1.-bI*y)+g*l[2]*l[2]*(1.+(bRho-bI)*y)*rho/2)
                /(y*((me*g+force)*l[2]+g*l[2]*l[2]*rho/2)-2*I*sigma));
        return ((me*g+force)*l[2]*(1.-bI*y)+g*l[2]*l[2]*(1.+(bRho-bI)*y)*rho/2)
                /(y*((me*g+force)*l[2]+g*l[2]*l[2]*rho/2)-2*I*sigma);
    }
    
    public void setExtendedLength(double[] lext) {
        System.arraycopy(lext, 0, this.extendedLinkLength, 0, dof);
    }

    @Override
    public final double[] kinematics() {
        double[] th = jointAngle;
        double[] l = linkLength;
        double[] re = new double[]
            {cos(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),sin(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),l[0]+l[1]*cos(th[1])+l[2]*cos(th[1]+th[2])};
        
        return re;
    }

    private double[] loopInvKinematics(double[] r, final double precision) {
        double[] th = jointAngle;
        
        final Matrix rVec = new Matrix(r, r.length);
        Matrix xVec = new Matrix(kinematics(), r.length);
        Matrix thVec = new Matrix(th, th.length);
        Matrix dthVec;
        Matrix errVec;
        Matrix j;
        
        errVec = rVec.minus(xVec);
        //int count = 0;
        while(errVec.norm2() > precision) {
            //c.println();
            //System.out.println(errVec.norm2());
            j = jacobian();
            dthVec = j.inverse().times(errVec);
            if(dthVec.norm1() >= PI/2) {
                th = 
                    loopInvKinematics(
                        xVec.plusEquals(rVec).timesEquals(0.5).getColumnPackedCopy(),
                        precision);
            } else {
                thVec.plusEquals(dthVec);
                th = thVec.getColumnPackedCopy();
            }
            this.setAngle(th);
            
            if (!count.countUp()) {
                thVec.timesEquals(0);
                //System.out.println("未到達");
                return thVec.getColumnPackedCopy();
            }
            xVec = new Matrix(kinematics(), r.length);
            errVec = rVec.minus(xVec);
        }
        return th;
    }
    
    @Override
    public final boolean invKinematics(double[] r, final double precision) {
        double[] th = loopInvKinematics(r, precision);
        count.reset();
        setAngle(th);
        return !(jointAngle[0]==0. && jointAngle[1]==0. && jointAngle[2]==0.);
    }

    @Override
    public final Matrix jacobian() {
        double[] th = jointAngle;
        double[] l = linkLength;
        Matrix j;
        
        double[][] arrayj = new double[][]
            {{
                -sin(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),
                cos(th[0])*(l[1]*cos(th[1])+l[2]*cos(th[1]+th[2])),
                l[2]*cos(th[0])*cos(th[1]+th[2])
            },{
                cos(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),
                (l[1]*cos(th[1])+l[2]*cos(th[1]+th[2]))*sin(th[0]),
                l[2]*cos(th[1]+th[2])*sin(th[0])
            },{
                0,
                -l[1]*sin(th[1])-l[2]*sin(th[1]+th[2]),
                -l[2]*sin(th[1]+th[2])
            }};
        
        return j = new Matrix(arrayj);
    }
    
    public final Matrix inertiaMatrix() {
        double[] l = linkLength;
        double[] lext = extendedLinkLength;
        double[] rho = density;
        double[] Ip = polMomentOfArea;
        double[] A = sectionalArea;
        double[] m = armWeight;
        double[] c = counterWeight;
        double[] th = jointAngle;
        double me = massOfEndEffector;
        
    
        double[][] arraym = new double[][]
            {{
                (Ip[0]*l[0]*rho[0])/A[0]+(pow(cos(th[1]),2.)*Ip[1]*(l[1]+lext[1])*rho[1])/A[1]+(pow(cos(th[1]+th[2]),2.)*Ip[2]*(l[2]+lext[2])*rho[2])/A[2]+(c[1]*pow(lext[1],2.)+pow(l[1],2.)*(me+m[2])+(Ip[1]*(l[1]+lext[1])*rho[1])/(2.*A[1])+1./3.*(pow(l[1],3.)+pow(lext[1],3.))*rho[1])*pow(sin(th[1]),2.)+(me*pow(l[2],2.)+c[2]*pow(lext[2],2.)+(Ip[2]*(l[2]+lext[2])*rho[2])/(2.*A[2])+1./3.*(pow(l[2],3.)+pow(lext[2],3.))*rho[2])*pow(sin(th[1]+th[2]),2.),
                0,
                0
            },{
                0,
                me*pow(l[2],2.)+c[1]*pow(lext[1],2.)+c[2]*pow(lext[2],2.)+pow(l[1],2.)*(me+m[2])+(Ip[1]*(l[1]+lext[1])*rho[1])/(2.*A[1])+1./3.*(pow(l[1],3.)+pow(lext[1],3.))*rho[1]+(Ip[2]*(l[2]+lext[2])*rho[2])/(2.*A[2])+1./3.*(pow(l[2],3.)+pow(lext[2],3.))*rho[2],
                0
            },{
                0,
                0,
                me*pow(l[2],2.)+c[2]*pow(lext[2],2.)+(Ip[2]*(l[2]+lext[2])*rho[2])/(2.*A[2])+1./3.*(pow(l[2],3.)+pow(lext[2],3.))*rho[2]
            }};
        return new Matrix(arraym);
    }
    
    public final Matrix invInertiaMatrix() {
        double[] l = linkLength;
        double[] lext = extendedLinkLength;
        double[] rho = density;
        double[] Ip = polMomentOfArea;
        double[] A = sectionalArea;
        double[] m = armWeight;
        double[] c = counterWeight;
        double[] th = jointAngle;
        double me = massOfEndEffector;
        
        double[][] iM = new double[][]
            {{
                1/((Ip[0]*l[0]*rho[0])/A[0]+(pow(cos(th[1]),2.)*Ip[1]*(l[1]+lext[1])*rho[1])/A[1]+(pow(cos(th[1]+th[2]),2.)*Ip[2]*(l[2]+lext[2])*rho[2])/A[2]+(c[1]*pow(lext[1],2.)+pow(l[1],2.)*(me+m[2])+(Ip[1]*(l[1]+lext[1])*rho[1])/(2.*A[1])+1./3.*(pow(l[1],3.)+pow(lext[1],3.))*rho[1])*pow(sin(th[1]),2.)+(me*pow(l[2],2.)+c[2]*pow(lext[2],2.)+(Ip[2]*(l[2]+lext[2])*rho[2])/(2.*A[2])+1./3.*(pow(l[2],3.)+pow(lext[2],3.))*rho[2])*pow(sin(th[1]+th[2]),2.)),
                0,
                0
            },{
                0,
                1/(me*pow(l[2],2.)+c[1]*pow(lext[1],2.)+c[2]*pow(lext[2],2.)+pow(l[1],2.)*(me+m[2])+(Ip[1]*(l[1]+lext[1])*rho[1])/(2.*A[1])+1./3.*(pow(l[1],3.)+pow(lext[1],3.))*rho[1]+(Ip[2]*(l[2]+lext[2])*rho[2])/(2.*A[2])+1./3.*(pow(l[2],3.)+pow(lext[2],3.))*rho[2]),
                0
            },{
                0,
                0,
                1/(me*pow(l[2],2.)+c[2]*pow(lext[2],2.)+(Ip[2]*(l[2]+lext[2])*rho[2])/(2.*A[2])+1./3.*(pow(l[2],3.)+pow(lext[2],3.))*rho[2])
            }};
        
        return new Matrix(iM);
    }
    
    public final double dynamicManipulabillity() {
        Matrix m = invInertiaMatrix();
        Matrix j = jacobian();
        
        double[] s = j.times(m).svd().getSingularValues();
        double dm = 1.;
        for (double p : s) {
            dm *= p;
        }
        return dm;
    }
    
    public final double calcLinDensity(double y) {
        return aRho*exp(bRho*y);
    }
    
    public final double calcSectionalArea(double y) {
        return aA*exp(bA*y);
    }
    
    public final double calcSecMomentOfArea(double y) {
        return aI*exp(bI*y);
    }
    
    public final double calcPolMomentOfArea(double y) {
        return 2.*calcSecMomentOfArea(y);
    }
    
    public final void calcArmWeight(int index) {
        if (index > this.dof-1 || index < 0) {
            System.out.print("Index error");
            return;
        }
        double[] l = linkLength;
        double[] lext = extendedLinkLength;
        armWeight[index] = counterWeight[index] + calcLinDensity(halfWidth[index])*(l[index]+lext[index]);
    }
    
    public final void calcCounterWeight(int index) {
        if (index > this.dof-1 || index < 0) {
            System.out.print("Index error");
            return;
        }
        double[] l = linkLength;
        double[] lext = extendedLinkLength;
        double[] m = armWeight;
        double loadWeight = massOfEndEffector;
        for (int i=index+1; i<dof; i++) {
            loadWeight += m[i];
        }
        counterWeight[index] = loadWeight*l[index]/lext[index] + calcLinDensity(halfWidth[index])*(l[index]*l[index]-lext[index]*lext[index])/(2.*lext[index]);
    }
    
    public void setMassOfEndEffector(double me) {
        massOfEndEffector = me;
    }
    
    public static void main(String[] args) {
        GCPuma3 robot = new GCPuma3();
        
        double[] th = new double[] {0., PI/4, PI/4};
        double[] l = new double[] {1000., 1000., 1000.};
        double[] lext = new double[] {0, 500., 500.};
        
        robot.setLength(l);
        robot.setExtendedLength(lext);
        robot.setMassOfEndEffector(3.);
        robot.setAngle(th);

        robot.optimizeMaterial();
        for (double y : robot.halfWidth) {
            System.out.println("halfWidth:"+y);
        }
        for (double y : robot.armWeight) {
            System.out.println("mass:"+y);
        }
        for (double y : robot.counterWeight) {
            System.out.println("conterWeigth:"+y);
        }
        for (double y : robot.linkLength) {
            System.out.println("length:"+y);
        }
        for (double y : robot.extendedLinkLength) {
            System.out.println("extended length:"+y);
        }
        //System.out.println(robot.fl3(100.));
        //System.out.println(robot.dfl3(100.));
        
        robot.invKinematics(new double[]{1400., 100., 300.}, 0.01);
        NumberFormat nf = new DecimalFormat("#0.0##E00");
        new Matrix(robot.jointAngle, 3).print(9, 3);
        double[] x = robot.kinematics();
        new Matrix(x, 3).print(9, 3);
        robot.inertiaMatrix().print(nf, 12);
        robot.inertiaMatrix().inverse().print(nf, 12);
        robot.invInertiaMatrix().print(nf, 12);
        
        System.out.println(robot.dynamicManipulabillity());
    }
}
