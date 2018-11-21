/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotsimulation;

import Jama.Matrix;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author raven
 */
public final class Puma3 extends Robot{

    public Puma3(int dof) {
        super(3);
    }
    
    public Puma3() {
        super(3);
    }

    @Override
    public final double[] kinematics(double[] th) {
        this.setTheta(th);
        double[] re = new double[]
            {cos(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),sin(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),l[0]+l[1]*cos(th[1])+l[2]*cos(th[1]+th[2])};
        
        return re;
    }

    @Override
    public final double[] invKinematics(double[] r, double[] th, double precision) {
        Matrix rVec = new Matrix(r, r.length);
        Matrix xVec = new Matrix(kinematics(th), r.length);
        Matrix thVec = new Matrix(th, th.length);
        Matrix dthVec;
        Matrix errVec;
        
        errVec = rVec.minus(xVec);
        int count = 0;
        //System.out.println(errVec.norm2());
        while(errVec.norm2() > precision) {
            Matrix j = jacobian(thVec.getColumnPackedCopy());
            dthVec = j.inverse().times(errVec);
            thVec.plusEquals(dthVec);
            if(dthVec.norm1() >= PI/2) thVec = new Matrix(
                    invKinematics(
                            xVec.plusEquals(rVec).timesEquals(0.5).getColumnPackedCopy(),
                            thVec.minusEquals(dthVec).getColumnPackedCopy(),
                            precision),
                    3);
            xVec.setMatrix(0, 2, 0, 0, new Matrix(kinematics(thVec.getColumnPackedCopy()), 3));
            errVec = rVec.minus(xVec);
            
            count++;
            if (count>=100) break;
        }
        
        System.out.println("Count:"+count);
        return thVec.getColumnPackedCopy();
    }

    @Override
    public final Matrix jacobian(double[] th) {
        Matrix j;
        this.setTheta(th);
        
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
        Matrix m = null;
        return m;
    }
    
}
