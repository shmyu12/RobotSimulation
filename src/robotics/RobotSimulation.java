/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotics;

import Jama.Matrix;
import static java.lang.Math.PI;

/**
 *
 * @author raven
 */
public class RobotSimulation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Puma3 robot = new Puma3();
        
        double[] th = new double[] {0., PI/2, PI/5};
        double[] l = new double[] {1., 1., 1.};
        robot.setDensity(3.84);
        robot.setLength(l);
        robot.setMassOfEndEffector(3.);
        robot.setAngle(th);
        robot.setRadius(0.025);
        
        robot.invKinematics(new double[]{0.4, -0.25, 0.3}, 0.001);
        /*th[0] %= 2*PI;
        th[1] %= 2*PI;
        th[2] %= 2*PI;
        th[0] += th[0]<0 ? 2*PI : 0;
        th[1] += th[1]<0 ? 2*PI : 0;
        th[2] += th[2]<0 ? 2*PI : 0;*/
        new Matrix(th, 3).print(3, 3);
        double[] x = robot.kinematics();
        new Matrix(x, 3).print(3, 3);
        robot.invInertiaMatrix().print(2, 4);
        robot.inertiaMatrix().inverse().print(2, 4);
        /*for (double th1 = 0; th1<PI/2; th1+=PI/10) {
            th[1] = th1;
            double[] re = robot.kinematics(th);
            System.out.println(re[0]+", "+re[1]+", "+re[2]);
            System.out.println(th[0]+", "+th[1]+", "+th[2]);
            robot.jacobian(th).print(5, 3);
            //System.out.println(robot.jacobian(th).get(0, 0)+", "+robot.jacobian(th).get(0, 1)+", "+robot.jacobian(th).get(0, 2));
        }*/
    }
}
