/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import static java.lang.Math.PI;
import robotsimulation.Count;
import robotsimulation.Puma3;
import static tools.MyMath.RangeRandom;

/**
 *
 * @author Char Aznable
 */
public final class OnePuma3 extends Individual {

    static {
        geneSize = 3;
        setMin(new double[]{0.2, 0.2, 0.2});
        setMax(new double[]{1., 1., 1.});
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
        Count c = new Count(100);
        
        for (int cx=0; cx<10; cx++) {
            for (int cy=0; cy<10; cy++) {
                for (int cz=0; cz<10; cz++) {
                    double[] x = {RangeRandom(0.5, 1.1), RangeRandom(-0.25, 0.25), RangeRandom(0., 0.5)};
                    double[] th = robot.invKinematics(x, initTh, 0.01, c);
                    
                    if (th[0]==0. || th[1]==0. || th[2]==0.) {
                        //setFitness(0);
                        //return;
                    } else {
                        e+=1.;
                    }
                    //setFitness(1.);
                }
            }
        }
        setFitness(e);
        
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
    

}
