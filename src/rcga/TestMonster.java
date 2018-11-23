/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static tools.MyMath.RangeRandom;

/**
 *
 * @author Char Aznable
 */
public class TestMonster extends Individual{

    static {
        geneSize =3;
        setMin(new double[]{0.0, 0.0, 0.0});
        setMax(new double[]{2., 1., 2.});
    }
    
    public TestMonster() {
        super();

    }
    
    @Override
    public void init() {
        double[] gene = new double[geneSize];
        for (int i=0; i<geneSize; i++) {
            gene[i] = RangeRandom(getMin()[i], getMax()[i]);
        }
        setGene(gene);
    }

    @Override
    public void evaluate() {
        double[] gene = getGene();
        double e = sin(gene[0])+sin(gene[1])+sin(gene[2]);
        setFitness(e);
    }
    
}
