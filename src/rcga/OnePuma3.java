/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import robotsimulation.Puma3;
import static tools.MyMath.RangeRandom;

/**
 *
 * @author Char Aznable
 */
public final class OnePuma3 extends Individual {

    Puma3 robot;
    //geneList
    //l[3]   リンク長[m]
    //rho   リンク線密度[kg/m]
    //r     リンク断面の半径(円筒近似)[m]
    
    private final double rho = 3.84;
    private final double r = 0.025;
    private final double me = 3.;
    
    static {
        geneSize = 3;
        setMin(new double[]{0.1, 0.2, 0.2});
        setMax(new double[]{1., 2., 1.});
    }
    
    public OnePuma3() {
        super();
        
        robot = new Puma3();
        robot.setDensity(rho);
        robot.setRadius(r);
        robot.setMassOfEndeffector(me);
    }

    @Override
    public final void evaluate() {
        
    }

    @Override
    public final void init() {
        double[] gene;
        gene = new double[]{
            RangeRandom(0.2, 1.5), //l[0]
            RangeRandom(0.2, 1.5), //l[1]
            RangeRandom(0.2, 1.5)};//l[2]
        robot.setLength(gene);
    }
    

}
