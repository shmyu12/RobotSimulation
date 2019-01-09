/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

/**
 *
 * @author Char Aznable
 */
abstract public class Individual {

    
    private final double[] gene;
    private static double[] min;
    private static double[] max;
    private double fitness;
    static int geneSize;
    
    static {
    }
    
    public Individual() {
        //Individual.geneSize = geneSize;
        gene = new double[geneSize];
        //min = new double[geneSize];
        //max = new double[geneSize];
    }
    
    public Individual(Individual clone) {
        //Individual.geneSize = geneSize;
        gene = new double[geneSize];
        min = new double[geneSize];
        max = new double[geneSize];
        clone(clone);
    }
    
    abstract public void init();
    abstract public void evaluate();

    public final void clone(Individual clone) {
        setGene(clone.getGene());
        setFitness(clone.getFitness());
    }
    
    public void setGene(double[] gene) {
        System.arraycopy(gene, 0, this.gene, 0, this.gene.length);
    }
    
    public final double[] getGene() {
        double[] ret = new double[gene.length];
        System.arraycopy(gene, 0, ret, 0, gene.length);
        return ret;
    }

    public final void setFitness(double f) {
        fitness = f;
    }
    
    public final double getFitness() {
        return fitness;
    }
    
    public static final double[] getMin() {
        double[] ret = new double[geneSize];
        System.arraycopy(min, 0, ret, 0, geneSize);
        return ret;
    }
    
    public static final double[] getMax() {
        double[] ret = new double[geneSize];
        System.arraycopy(max, 0, ret, 0, geneSize);
        return ret;
    }
    
    public static final void setMin(double min[]) {
        Individual.min = min;
    }
    
    public static final void setMax(double max[]) {
        Individual.max = max;
    }
    
    public static final void setMinMax(int index, double min, double max) {
        Individual.max[index] = max;
        Individual.min[index] = min;
    }

    @Override
    public boolean equals(Object o) {
        Individual a = (Individual) o;
        double[] aGene = a.getGene();
        double[] bGene = this.getGene();
        
        if( aGene.length != bGene.length || a.getFitness() != this.getFitness()) return false;
        for (int i=0; i<aGene.length; i++) {
            if(aGene[i] != bGene[i]) return false; 
        }
        return true;
    }
    
    
}
