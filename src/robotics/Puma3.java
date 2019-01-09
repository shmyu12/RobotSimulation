/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotics;

import Jama.Matrix;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author raven
 */
public final class Puma3 extends Robot{

    private Count c;
    double density;
    double radius;
    double massOfEndEffector;
    
    public Puma3(int dof) {
        super(3);
        c = new Count(20);
    }
    
    public Puma3() {
        this(3);
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
        double[] th = jointAngle.clone();
        
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
            
            if (!c.countUp()) {
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
        c.reset();
        setAngle(th);
        if(jointAngle[0]==0. && jointAngle[1]==0. && jointAngle[2]==0.) {
            return false;
        } else {
            return true;
        }
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
        double[] th = jointAngle;
        double[] l = linkLength;
        double r = 0.025;
        double rho = density;
        double me = massOfEndEffector;
        
        double[][] arraym = new double[][]
            {{
                1./12.*(3.*(2*l[0]+l[1]+l[2])*r*r*rho+3.*l[1]*r*r*rho*cos(th[1])*cos(th[1])+3.*l[2]*r*r*rho*cos(th[1]+th[2])*cos(th[1]+th[2])+4.*(l[1]*l[1]*(3.*me+l[1]*rho+3.*l[2]*rho)*sin(th[1])*sin(th[1])+3.*l[1]*l[2]*(2.*me+l[2]*rho)*sin(th[1])*sin(th[1]+th[2])+l[2]*l[2]*(3.*me+l[2]*rho)*sin(th[1]+th[2])*sin(th[1]+th[2]))),
                0,
                0},
            {
                0,
                (l[1]*l[1]+l[2]*l[2])*me+1./12.*(4.*l[1]*l[1]*l[1]+12.*l[1]*l[1]*l[2]+3.*l[2]*l[2]*l[2]+3.*l[1]*r*r)*rho+l[1]*l[2]*(2.*me+l[2]*rho)*cos(th[2]),
                1./4.*l[2]*(l[2]*(4.*me+l[2]*rho)+2.*l[1]*(2.*me+l[2]*rho)*cos(th[2]))},
            {
                0,
                1./4.*l[2]*(l[2]*(4.*me+l[2]*rho)+2.*l[1]*(2*me+l[2]*rho)*cos(th[2])),
                1./4.*l[2]*l[2]*(4.*me+l[2]*rho)
            }};
        return new Matrix(arraym);
    }
    
    public final Matrix invInertiaMatrix() {
        double[] th = jointAngle;
        double[] l = linkLength;
        double r = radius;
        double rho = density;
        double me = massOfEndEffector;
        
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
    
    public final double dynamicManipulabillity() {
        Matrix m = invInertiaMatrix();
        Matrix j = jacobian();
        
        double[] s = j.times(m).svd().getSingularValues();
        double dm = 1.;
        for (double sigma : s) {
            dm *= sigma;
        }
        return dm;
    }
    
    public final void setDensity(double density) {
        this.density = density;
    }
    
    public final void setRadius(double radius) {
        this.radius = radius;
    }
    
    public final void setMassOfEndEffector(double me) {
        this.massOfEndEffector = me;
    }

    public final boolean isSafe() {
        return jointAngle[1]+jointAngle[2]<PI;
    }
    
public static void main(String[] args) {
        Puma3 robot = new Puma3();
        
        double[] th = new double[] {0., PI/2, PI/5};
        double[] l = new double[] {1000., 1000., 1000.};
        
        robot.setDensity(3.84);
        robot.setLength(l);
        robot.setMassOfEndEffector(3.);
        robot.setAngle(th);
        robot.setRadius(0.025);

        for (double y : robot.linkLength) {
            System.out.println("length:"+y);
        }
        
        robot.invKinematics(new double[]{1600., 200., 500.}, 0.01);
        NumberFormat nf = new DecimalFormat("#0.0##E00");
        new Matrix(th, 3).print(9, 3);
        double[] x = robot.kinematics();
        new Matrix(x, 3).print(9, 3);
        robot.inertiaMatrix().print(nf, 12);
        robot.invInertiaMatrix().print(nf, 12);
        robot.inertiaMatrix().inverse().print(nf, 12);
    }
}


