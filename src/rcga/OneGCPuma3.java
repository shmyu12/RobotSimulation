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
import robotics.Count;
import robotics.GCPuma3;
import robotics.Puma3;
import static tools.MyMath.rangeRandom;

/**
 *
 * @author raven
 */
public class OneGCPuma3 extends Individual {

    static {
        geneSize = 3;
        setMin(new double[]{250., 250., 250., 100., 100.});
        setMax(new double[]{1500., 1500., 1500., 500., 500.});
    }
    
    GCPuma3 robot;
    //geneList
    //l[1, 2, 3]   リンク長[mm]
    //lext[2, 3]    重力補償リンク長[mm]
    //r[2, 3]     リンク断面の幅[mm]リンク1とリンク2の幅は一緒
    
    private final double rho = 3.43;
    private final double r = 0.025;
    private final double me = 3.;
    
    public OneGCPuma3() {
        super();
        
        robot = new GCPuma3();
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
            double[] x = {rangeRandom(0.5, 1.1), rangeRandom(-0.25, 0.25), rangeRandom(0.3, 0.5)};
            c.reset();
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
                double[] x = {rangeRandom(0.5, 1.1), rangeRandom(-0.25, 0.25), rangeRandom(0.3, 0.5)};
                c.reset();
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
            gene[i] = rangeRandom(getMin()[i], getMax()[i]);
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
