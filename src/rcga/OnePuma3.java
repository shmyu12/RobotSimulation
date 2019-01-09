/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.PI;
import robotics.Puma3;
import static tools.MyMath.rangeRandom;

/**
 *
 * @author Char Aznable
 */
public final class OnePuma3 extends Individual {

    static {
        geneSize = 3;
        setMin(new double[]{0.2, 0.2, 0.2});
        setMax(new double[]{1.5, 1.5, 1.5});
    }
    
    Puma3 robot;
    //geneList
    //l[3]   リンク長[m]
    //rho   リンク線密度[kg/m]
    //r     リンク断面の半径(円筒近似)[m]
    
    private final double rho = 3.43;
    private final double r = 0.025;
    private final double me = 3.;
    
    public OnePuma3() {
        super();
        
        robot = new Puma3();
        robot.setDensity(rho);
        robot.setRadius(r);
        robot.setMassOfEndEffector(me);
    }

    @Override
    public final void evaluate() {
        robot.setLength(getGene());
        double e=0;
        double[] initTh = {0, PI/4, PI/4};
        
        double xlim[] = new double[] {0.5, 0.8, 1.1};
        double ylim[] = new double[] {-0.25, 0, 0.25};
        double zlim[] = new double[] {0.1, 0.3, 0.5};
        for (double x : xlim) {
            for (double y : ylim) {
                for (double z : zlim) {
                    robot.setAngle(initTh);
                    if (!robot.invKinematics(new double[]{x, y, z}, 0.01)) {
                        setFitness(0);
                        return;
                    } else if (!robot.isSafe()) {
                        setFitness(0);
                        return;
                    } else {
                        e+=robot.dynamicManipulabillity();
                    }
                }
            }
        }
        /*for (int i=0; i<100; i++) {
            double[] r = {rangeRandom(0.5, 1.1), rangeRandom(-0.25, 0.25), rangeRandom(0.1, 0.5)};
            robot.setAngle(initTh);

            if (!robot.invKinematics(r, 0.01)) {

            } else if (!robot.isSafe()) {

            } else {
                e+=robot.dynamicManipulabillity();
            }
        }*/
        setFitness(e);
        
    }
    
    public final void writeFitness() {
        robot.setLength(getGene());
        double e;
        double[] initTh = {0, PI/4, PI/4};
        
        try (PrintWriter pw = new PrintWriter("plotData.csv")) {
            pw.print("format,3\r\n" + "memo1\r\n" + "memo2\r\n");
            
            for (int i=0; i<2000; i++) {
                double[] x = {rangeRandom(0.5, 1.1), rangeRandom(-0.25, 0.25), rangeRandom(0.1, 0.5)};
                robot.setAngle(initTh);

                if (!robot.invKinematics(x, 0.01)) {
                    e=0;
                } else if (!robot.isSafe()) {
                    e=0;
                } else {
                    e=robot.dynamicManipulabillity();
                }
                pw.print(x[0]+","+x[1]+","+x[2]+","+e+"\r\n");
            }
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public final void init() {
        double[] gene;
        gene = new double[geneSize];
        for (int i=0; i<geneSize; i++) {
            gene[i] = rangeRandom(getMin()[i], getMax()[i]);
        }
        setGene(gene);
        //robot.setLength(gene);
    }
    
    public static void main(String[] args) {
        
        OnePuma3 robot = new OnePuma3();
        robot.setGene(new double[]{0.28, 0.45, 0.69});
        robot.evaluate();
        robot.writeFitness();
        System.out.println(robot.getFitness());
    }
    

}
