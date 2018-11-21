/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotsimulation;

import static java.lang.Math.*;

/**
 *
 * @author raven
 */
public class Kinematics {
    
    static double[] calc(Robot robot, double[] th) {
        double[] l = robot.getLength();
        double r = robot.getRadius();
        double rho = robot.getDensity();
        double me = robot.getMassOfEndeffector();

        double[] re = new double[]
            {cos(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),sin(th[0])*(l[1]*sin(th[1])+l[2]*sin(th[1]+th[2])),l[0]+l[1]*cos(th[1])+l[2]*cos(th[1]+th[2])};
        
        return re;
    }
}
