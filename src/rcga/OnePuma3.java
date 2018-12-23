/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.PI;
import robotics.Count;
import robotics.Puma3;
import static tools.MyMath.RangeRandom;

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
        robot.setMassOfEndeffector(me);
    }

    @Override
    public final void evaluate() {
        robot.setLength(getGene());
        double e=0;
        double[] initTh = {0, PI/4, PI/4};
        Count c = new Count(20);
        
        for (int i=0; i<100; i++) {
            double[] x = {RangeRandom(0.5, 1.1), RangeRandom(-0.25, 0.25), RangeRandom(0.3, 0.5)};
            c.num = 0;
            double[] th = robot.invKinematics(x, initTh, 0.05, c);

            if (th[0]==0. && th[1]==0. && th[2]==0.) {
                setFitness(0);
                //e-=1.;
                return;
            } else if (th[1]+th[2]>=PI) {
                setFitness(0);
                return;
            } else {
                e+=robot.dynamicManipulabillity(th);
            }
            //setFitness(1.);
        }
        setFitness(e);
        
    }
    
    public final void writeFitness() {
        robot.setLength(getGene());
        double e;
        double[] initTh = {0, PI/4, PI/4};
        Count c = new Count(20);
        
        try (PrintWriter pw = new PrintWriter("plotData.csv")) {
            pw.print("format,3\r\n" + "memo1\r\n" + "memo2\r\n");
            
            for (int i=0; i<1000; i++) {
                double[] x = {RangeRandom(0.5, 1.1), RangeRandom(-0.25, 0.25), RangeRandom(0.3, 0.5)};
                c.num = 0;
                double[] th = robot.invKinematics(x, initTh, 0.05, c);

                if (th[0]==0. && th[1]==0. && th[2]==0.) {
                    e=0;
                } else if (th[1]+th[2]>=PI) {
                    e=0;
                } else {
                    e=robot.dynamicManipulabillity(th);
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
            gene[i] = RangeRandom(getMin()[i], getMax()[i]);
        }
        setGene(gene);
        //robot.setLength(gene);
    }
    
    public static void main(String[] args) {
        
        OnePuma3 robot = new OnePuma3();
        robot.setGene(new double[]{0.4, 0.3, 0.75});
        robot.evaluate();
        robot.writeFitness();
        System.out.println(robot.getFitness());
    }
    

}
