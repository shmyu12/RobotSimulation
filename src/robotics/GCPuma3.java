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
import static tools.MyMath.newton;
/**
 *
 * @author raven
 */
public class GCPuma3 extends Robot{

    public double[] lext;
    public double[] distance;
    public double[] mass;
    public double[] counterWeight;
    public double[] width;
    public double force=20.0;
    public final double sigma = 20.5; //205/10[N/mm4]
    
    final double aRho = 0.1888e-3;
    final double aI = 1069.7;
    final double aA = 75.134;
    final double bRho = 0.0541;
    final double bI = 0.1085;
    final double bA = 0.0519;
            
            
    public GCPuma3(int dof) {
        super(3);
        this.lext = new double[3];
        this.counterWeight = new double[3];
        this.mass = new double[3];
        this.distance = new double[3];
        this.width = new double[3];
    }
    
    public GCPuma3() {
        this(3);
    }
    
    public void optimizeMaterial() {
        width[2] = newton(50., this::fl3, this::dfl3);
        calcCounterWeight(2);
        calcMass(2);
        width[1] = newton(50., this::fl2, this::dfl2);
        calcCounterWeight(1);
        calcMass(1);
        width[0] = width[1];
    }
    
    public double fl2(double y) {
        double I = calcSecMomentOfArea(y);
        double density = calcLinDensity(y);
        return y*((me+mass[2])*g*l[1]+force*(l[2]+l[1])+g*l[1]*l[1]*density/2.)/(2.*I)-sigma;
    }
    
    public double dfl2(double y) {
        double I = calcSecMomentOfArea(y);
        double density = calcLinDensity(y);
        return (((me+mass[2])*g*l[1]+force*(l[2]+l[1]))*(1.-bI*y)+g*l[1]*l[1]*(1.+(bRho-bI)*y)*density/2.)/(2.*I);
    }
    
    public double fl3(double y) {
        double I = calcSecMomentOfArea(y);
        double density = calcLinDensity(y);
        return y*l[2]*(me*g+force+g*l[2]*density/2.)/(2.*I)-sigma;
    }
    
    public double dfl3(double y) {
        double I = calcSecMomentOfArea(y);
        double density = calcLinDensity(y);
        return l[2]*((me*g+force)*(1.-bI*y)+g*l[2]*(1.+(bRho-bI)*y)*density/2.)/(2.*I);
    }
    
    public double z(double y) {
        double I = calcSecMomentOfArea(y);
        double density = calcLinDensity(y);
        System.out.println(((me*g+force)*l[2]*(1.-bI*y)+g*l[2]*l[2]*(1.+(bRho-bI)*y)*density/2)
                /(y*((me*g+force)*l[2]+g*l[2]*l[2]*density/2)-2*I*sigma));
        return ((me*g+force)*l[2]*(1.-bI*y)+g*l[2]*l[2]*(1.+(bRho-bI)*y)*density/2)
                /(y*((me*g+force)*l[2]+g*l[2]*l[2]*density/2)-2*I*sigma);
    }
    
    public void setExtendedLength(double[] lext) {
        System.arraycopy(lext, 0, this.lext, 0, dof);
    }

    @Override
    public final double[] kinematics(double[] th) {
        //this.setTheta(th);
        double[] re = new double[]
            {cos(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),sin(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),l[0]+l[1]*cos(th[1])+l[2]*cos(th[1]+th[2])};
        
        return re;
    }

    @Override
    public final double[] invKinematics(double[] r, double[] th, final double precision, Count c) {
        final Matrix rVec = new Matrix(r, r.length);
        Matrix xVec = new Matrix(kinematics(th), r.length);
        Matrix thVec = new Matrix(th, th.length);
        Matrix dthVec;
        Matrix errVec;
        Matrix j;
        
        errVec = rVec.minus(xVec);
        //int count = 0;
        while(errVec.norm2() > precision) {
            //System.out.println(errVec.norm2());
            j = jacobian(th);
            dthVec = j.inverse().times(errVec);
            if(dthVec.norm1() >= PI/2) {
                th = 
                    invKinematics(
                        xVec.plusEquals(rVec).timesEquals(0.5).getColumnPackedCopy(),
                        th,
                        precision,
                        c);
            } else {
                thVec.plusEquals(dthVec);
                th = thVec.getColumnPackedCopy();
            }
            
            if (!c.countUp()) {
                thVec.timesEquals(0);
                //System.out.println("未到達");
                return thVec.getColumnPackedCopy();
            }
            
            xVec = new Matrix(kinematics(th), r.length);
            errVec = rVec.minus(xVec);
        }
        
        //c.num = 0; 
        //System.out.println("Count:");
        return th;
    }

    @Override
    public final Matrix jacobian(double[] th) {
        Matrix j;
        //this.setTheta(th);
        
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
    
    public final Matrix inertiaMatrix(double[] th) {
        //this.setTheta(th);
        double Ip1 = this.calcPolMomentOfArea(width[0]);
        double Ip2 = this.calcPolMomentOfArea(width[1]);
        double Ip3 = this.calcPolMomentOfArea(width[2]);
        double rho1 = this.calcLinDensity(width[0]);
        double rho2 = this.calcLinDensity(width[1]);
        double rho3 = this.calcLinDensity(width[2]);
        double A1 = this.calcSectionalArea(width[0]);
        double A2 = this.calcSectionalArea(width[1]);
        double A3 = this.calcSectionalArea(width[2]);
        //(Ip[0]*l[0]*rho[0])/A[0]+(cos(th[1])*(cos(th[1])*Ip[1]*(l[1]+lext[1])*rho[1])/A[1]+(cos(th[1]+th[2])*(cos(th[1]+th[2])*Ip[2]*(l[2]+lext[2])*rho[2])/A[2]+(counterWeight[1]*lext[1]*lext[1]+l[1]*l[1]*(me+mass[2])+(Ip[1]*(l[1]+lext[1])*rho[1])/(2*A[1])+1/3*(l[1]*l[1]*l[1]+lext[1]*lext[1]*lext[1])*rho[1])*sin(th[1])*sin(th[1])+(me*l[2]*l[2]+counterWeight[2]*lext[2]*lext[2]+(Ip[2]*(l[2]+lext[2])*rho[2])/(2*A[2])+1/3*(l[2]*l[2]*l[2]+lext[2]*lext[2]*lext[2])*rho[2])*sin(th[1]+th[2])*sin(th[1]+th[2]);
        double[][] arraym = new double[][]
            {{
                Ip1*l[0]*rho1+Ip2*(l[1]+lext[1])*cos(th[1])*cos(th[1])+Ip3*(l[2]+lext[2])*cos(th[1]+th[2])*cos(th[1]+th[2])+(counterWeight[1]*lext[1]*lext[1]+l[1]*l[1]*(mass[2]+me)+(Ip2*(l[1]+lext[1])*rho3)/(2.*A3)+1./3.*(l[1]*l[1]*l[1]+lext[1]*lext[1]*lext[1])*rho3)*sin(th[1])*sin(th[1])+(counterWeight[2]*lext[2]*lext[2]+1./2.*Ip3*(l[2]+lext[2])+l[2]*l[2]*me+1./3.*(l[2]*l[2]*l[2]+lext[2]*lext[2]*lext[2])*rho3)*sin(th[1]+th[2])*sin(th[1]+th[2]),
                0,
                0
            },{
                0,
                counterWeight[1]*lext[1]*lext[1]+counterWeight[2]*lext[2]*lext[2]+l[2]*l[2]*me+l[1]*l[1]*(mass[2]+me)+(Ip2*(l[1]+lext[1])*rho2)/A2+1./3.*(l[1]*l[1]*l[1]+lext[1]*lext[1]*lext[1])*rho2+(Ip3*(l[2]+lext[2])*rho3)/A3+1./3.*(l[2]*l[2]*l[2]+lext[2]*lext[2]*lext[2])*rho3,
                0
            },{
                0,
                0,
                counterWeight[2]*lext[2]*lext[2]+l[2]*l[2]*me+(Ip3*(l[2]+lext[2])*rho3)/A3+1./3.*(l[2]*l[2]*l[2]+lext[2]*lext[2]*lext[2])*rho3
            }};
        return new Matrix(arraym);
    }
    
    public final Matrix invInertiaMatrix(double th[]) {
        
        double[][] iM = new double[][]
            {{
                12./(3.*(2.*l[0]+l[1]+l[2])*r*r*rho+3.*l[1]*r*r*rho*cos(th[1])*cos(th[1])+3.*l[2]*r*2.*rho*cos(th[1]+th[2])*cos(th[1]+th[2])+4.*(l[1]*l[1]*(3.*me+(l[1]+3.*l[2])*rho)*sin(th[1])*sin(th[1])+3.*l[1]*l[2]*(2.*me+l[2]*rho)*sin(th[1])*sin(th[1]+th[2])+l[2]*l[2]*(3.*me+l[2]*rho)*sin(th[1]+th[2])*sin(th[1]+th[2]))),
                0,
                0},
            {
                0,
                (12.*(4.*me+l[2]*rho))/(l[1]*(4.*l[1]*l[1]*rho*(4.*me+l[2]*rho)+3.*r*r*rho*(4.*me+l[2]*rho)+6.*l[1]*(4.*me*me+6.*l[2]*me*rho+l[2]*l[2]*rho*rho)-6.*l[1]*(2.*me+l[2]*rho)*(2.*me+l[2]*rho)*cos(2.*th[2]))),
                -(12.*(l[2]*(4.*me+l[2]*rho)+2.*l[1]*(2.*me+l[2]*rho)*cos(th[2])))/(l[1]*l[2]*(4.*l[1]*l[1]*rho*(4.*me+l[2]*rho)+3.*r*r*rho*(4.*me+l[2]*rho)+6.*l[1]*(4.*me*me+6.*l[2]*me*rho+l[2]*l[2]*rho*rho)-6.*l[1]*(2.*me+l[2]*rho)*(2.*me+l[2]*rho)*cos(2.*th[2])))},
            {
                0,
                -(12.*(l[2]*(4.*me+l[2]*rho)+2.*l[1]*(2.*me+l[2]*rho)*cos(th[2])))/(l[1]*l[2]*(4.*l[1]*l[1]*rho*(4.*me+l[2]*rho)+3.*r*r*rho*(4.*me+l[2]*rho)+6.*l[1]*(4.*me*me+6.*l[2]*me*rho+l[2]*l[2]*rho*rho)-6.*l[1]*(2.*me+l[2]*rho)*(2.*me+l[2]*rho)*cos(2.*th[2]))),
                (4.*(4.*l[1]*l[1]*l[1]*rho+3.*l[1]*r*r*rho+12.*l[1]*l[1]*(me+l[2]*rho)+3.*l[2]*l[2]*(4.*me+l[2]*rho)+12.*l[1]*l[2]*(2.*me+l[2]*rho)*cos(th[2])))/(l[1]*l[2]*l[2]*(4.*l[1]*l[1]*rho*(4.*me+l[2]*rho)+3.*r*r*rho*(4.*me+l[2]*rho)+6.*l[1]*(4.*me*me+6.*l[2]*me*rho+l[2]*l[2]*rho*rho)-6.*l[1]*(2.*me+l[2]*rho)*(2.*me+l[2]*rho)*cos(2.*th[2])))
            }};
        
        return new Matrix(iM);
    }
    
    public final double dynamicManipulabillity(double[] th) {
        Matrix m = invInertiaMatrix(th);
        Matrix j = jacobian(th);
        
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
    
    public final void calcMass(int index) {
        if (index > this.dof-1 || index < 0) {
            System.out.print("Index error");
            return;
        }
        mass[index] = counterWeight[index] + calcLinDensity(width[index])*(l[index]+lext[index]);
    }
    
    public final void calcCounterWeight(int index) {
        if (index > this.dof-1 || index < 0) {
            System.out.print("Index error");
            return;
        }
        double loadMass = me;
        for (int i=index+1; i<dof; i++) {
            loadMass += mass[i];
        }
        counterWeight[index] = loadMass*l[index]/lext[index] + calcLinDensity(width[index])*(l[index]*l[index]-lext[index]*lext[index])/(2.*lext[index]);
    }
    
    public static void main(String[] args) {
        GCPuma3 robot = new GCPuma3();
        
        double[] th = new double[] {0., PI/4, PI/4};
        double[] l = new double[] {1000., 1000., 1000.};
        double[] lext = new double[] {0, 500., 500.};
        
        //robot.setDensity(3.84);
        robot.setLength(l);
        robot.setExtendedLength(lext);
        robot.setMassOfEndeffector(3.);
        //robot.setTheta(th);
        //robot.setRadius(0.025);
        //double y;
        //y = newton(50.0, robot::fl3, robot::dfl3);
        //y = newton(30.0, robot::z);
        //System.out.println(y);
        //System.out.println(robot.calcSecMomentOfArea(y));

        robot.optimizeMaterial();
        for (double y : robot.width) {
            System.out.println("width:"+y);
        }
        for (double y : robot.mass) {
            System.out.println("mass:"+y);
        }
        for (double y : robot.counterWeight) {
            System.out.println("conterWeigth:"+y);
        }
        for (double y : robot.l) {
            System.out.println("length:"+y);
        }
        for (double y : robot.lext) {
            System.out.println("extended length:"+y);
        }
        //System.out.println(robot.fl3(100.));
        //System.out.println(robot.dfl3(100.));
        Count c = new Count(10);
        
        th = robot.invKinematics(new double[]{1400., 100., 300.}, th, 0.01, c);
        new Matrix(th, 3).print(8, 3);
        double[] x = robot.kinematics(th);
        new Matrix(x, 3).print(8, 3);
        robot.inertiaMatrix(th).print(8, 5);
        robot.inertiaMatrix(th).inverse().print(8, 5);
        
        System.out.println(robot.dynamicManipulabillity(th));
    }
}
