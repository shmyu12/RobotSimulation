/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.PI;
import static rcga.Individual.geneSize;
import robotics.GCPuma3;
import static tools.MyMath.rangeRandom;

/**
 *
 * @author raven
 */
public class OneGCPuma3 extends Individual {

    static {
        geneSize = 5;
        setMin(new double[]{250., 250., 250., 100., 100.});
        setMax(new double[]{1500., 1500., 1500., 500., 500.});
    }
    
    GCPuma3 robot;
    //geneList
    //l[0, 1, 2]   リンク長[mm]
    //lext[1, 2]    重力補償リンク長[mm]
    
    private final double me = 3.;
    
    public OneGCPuma3() {
        super();
        
        robot = new GCPuma3();
        robot.setMassOfEndEffector(me);
    }
    
    
    @Override
    public final void evaluate() {
        double e=0;
        double[] initTh = {0, PI/4, PI/4};
        
        double xlim[] = new double[] {500., 800., 1100.};
        double ylim[] = new double[] {-250., 0, 250.};
        double zlim[] = new double[] {100., 300., 500.};
        for (double x : xlim) {
            for (double y : ylim) {
                for (double z : zlim) {
                    robot.setAngle(initTh);
                    if (!robot.invKinematics(new double[]{x, y, z}, 1.)) {
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
                double[] x = {rangeRandom(500., 1100.), rangeRandom(-250., 250.), rangeRandom(100., 500.)};
                robot.setAngle(initTh);

                if (!robot.invKinematics(x, 1.)) {
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
        double[] length = new double[]{gene[0], gene[1], gene[2]};
        double[] extendedLength = new double[]{0., gene[3], gene[4]};
        robot.setLength(length);
        robot.setExtendedLength(extendedLength);
        robot.optimizeMaterial();
    }
    
    
    @Override
    public void setGene(double[] gene){
        super.setGene(gene);
        robot.setLength(new double[]{gene[0], gene[1], gene[2]});
        robot.setExtendedLength(new double[]{0, gene[3], gene[4]});
        robot.optimizeMaterial();
    }
    public static void main(String[] args) {
        
        OneGCPuma3 robot = new OneGCPuma3();
        robot.setGene(new double[]{280., 450., 800., 400., 400.});
        robot.robot.printParams();
        robot.evaluate();
        robot.writeFitness();
        System.out.println(robot.getFitness());
    }
    
}
