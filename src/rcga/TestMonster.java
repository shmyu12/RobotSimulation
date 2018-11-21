/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import static tools.MyMath.RangeRandom;

/**
 *
 * @author Char Aznable
 */
public class TestMonster extends Individual{

    static {
        geneSize =3;
        setMin(new double[]{0.1, 0.2, 0.2});
        setMax(new double[]{1., 10., 1.});
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
        double e = gene[0]+gene[1]+gene[2];
        setFitness(e);
    }
    
}
