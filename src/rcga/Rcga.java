/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import java.util.Arrays;
import static tools.MyMath.RangeRandom;

/**
 *
 * @author Char Aznable
 */
abstract public class Rcga {
    
    //final int geneSize;
    final int populationNum;
    final int generationLim;
    final int childrenNum;
    final int parentsNum;
    
    double sum;
    int gen;
    double crossoverP;
    double alpha;
    
    final Individual[] population;
    final Individual[] offspring;
    final Individual[] children;
    final Individual[] parents;
    
    public Rcga(int populationNum, int generationLim, int parentsNum, int childrenNum) {
        this.populationNum = populationNum;
        this.generationLim = generationLim;
        this.childrenNum = childrenNum;
        this.parentsNum = parentsNum;
        population = new Individual[populationNum];
        offspring = new Individual[populationNum];
        children = new Individual[childrenNum];
        parents = new Individual[parentsNum];
    }
    
    public final double[] twoPointCrossover(double[] aGene, double[] bGene) {
        
        //double[] aGene = a.getGene();
        //double[] bGene = b.getGene();
        
        int geneSize = aGene.length;
        int random1 = RangeRandom(1, geneSize-2);
        int random2 = RangeRandom(random1+1, geneSize-1);
        
        double[][] tmpGene1 = new double[2][random1];
        double[][] tmpGene2 = new double[2][random2-random1];
        double[][] tmpGene3 = new double[2][geneSize-random2];
        
        for (int i=0; i<random1; i++) {
            tmpGene1[0][i] = aGene[i];
            tmpGene1[1][i] = bGene[i];
        }
        for (int i=random1; i<random2; i++) {
            tmpGene2[0][i-random1] = aGene[i];
            tmpGene2[1][i-random1] = bGene[i];
        }
        for (int i=random2; i<geneSize; i++) {
            tmpGene3[0][i-random2] = aGene[i];
            tmpGene3[1][i-random2] = bGene[i];
        }
        
        
        double[][] allChildren = new double[8][geneSize];
        int c=0;
        for (int i=0; i<2; i++) {
            for (int j=0; j<2; j++) {
                for(int k=0; k<2; k++) {
                    System.arraycopy(tmpGene1[i], 0, allChildren[c], 0, tmpGene1[i].length);
                    System.arraycopy(tmpGene2[j], 0, allChildren[c], tmpGene1[i].length, tmpGene2[j].length);
                    System.arraycopy(tmpGene3[k], 0, allChildren[c], tmpGene1[i].length+tmpGene2[j].length, tmpGene3[k].length);
                    c++;
                }
            }
        }
        
        //a = new Individual();
        //b = new Individual();
        //a.setGene(children[RangeRandom(0, 7)]);
        //b.setGene(children[RangeRandom(0, 7)]);
        double[] ret;
        ret = allChildren[RangeRandom(0, 7)];
        //ret[1] = children[RangeRandom(0, 7)];
        
        /*List<Individual> ret = new ArrayList<>();
        ret.add(a);
        ret.add(b);
        return ret;*/
        //Individual[] ret = new Individual[2];
        //ret[0] = a;
        //ret[1] = b;
        return ret;
    }
    public final double[] blxAlpha(double[] aGene, double[] bGene) {
        
        int geneSize = aGene.length;
        double[] ret = new double[geneSize];
        
        for (int i=0; i<geneSize; i++) {
            double minx = aGene[i]<bGene[i] ? aGene[i] : bGene[i];
            double maxx = minx==bGene[i] ? aGene[i] : bGene[i];
            double dx = abs(maxx-minx);
            double xNext;
            do {
                xNext = random()*dx*(2*alpha+1) + minx - alpha*dx;
            } while(xNext<Individual.getMin()[i] || xNext>Individual.getMax()[i]);
            ret[i] = xNext;
            //System.out.println("a:"+aGene[i]+"\tb:"+bGene[i]+"\tnext"+xNext);
        }
        return ret;
    }
    
    
    abstract double[] crossover(double[] aGene, double[] bGene);
    
    public final void bigbang() {
        
        gen=1;
        init();
        evaluate();
        //generationInfo();
        populationInfo();
        System.out.println();
        
        for (gen=2; gen<=generationLim; gen++) {
            evolute();
            evaluate();
            //generationInfo();
            //eliteInfo();
            populationInfo();
            System.out.println();
        }
    }
    
    public final void evolute() {
        int geneSize = population[0].getGene().length;
        
        double[] index = new double[parentsNum];
        
        for (int i=0; i<parentsNum; i++) {
            int rand = RangeRandom(0, populationNum);
            for (int j=0; j<i; j++) {
                if (index[i] == rand) {
                    rand = RangeRandom(0, populationNum);
                    j = -1;
                }
            }
            index[i] = rand;
            parents[i].setGene(population[i].getGene());
        }
        
        for (int i=0; i<childrenNum; i++) {
            children[i].setGene(crossover(randomSelect(parents), randomSelect(parents)));
        }
        
        for (int i=0; i<parentsNum; i++) {
            
        }
        
        System.arraycopy(offspring, 0, population, 0, populationNum);
    }
    
    public final double[] rouletteSelect() {
        return rouletteSelect(population);
    }
    public final double[] rouletteSelect(Individual[] p) {
        sum=0;
        for (Individual a : p) {
            sum += a.getFitness();
        }
        int i;
        do {
            i = RangeRandom(0, p.length-1);
        } while((p[i].getFitness()/sum) < random());
        return p[i].getGene();
    }
    
    public final double[] randomSelect() {
        return randomSelect(population);
    }
    public final double[] randomSelect(Individual[] p) {
        return population[RangeRandom(0, p.length-1)].getGene();
    }
    
    public final void evaluate() {
        sum = 0;
        for(Individual a:population) {
            a.evaluate();
            sum += a.getFitness();
        }
    }
    
    public final void init(){
        for(Individual a: population) {
            a.init();
        }
    }
    
    public void generationInfo() {
        System.out.print("gen:"+gen+"\nsum:"+sum+"\n");
    }
    
    public void eliteInfo() {
        double[] eliteGene = getElite();
        for (int i=0; i<eliteGene.length; i++) {
            System.out.print("Gene["+i+"]="+eliteGene[i]+"\t");
        }
        System.out.println();
    }
    
    public final void populationInfo() {
        for (int i=0; i<populationNum; i++) {
            System.out.print(Arrays.toString(population[i].getGene())+"\n");
        }
    }
    
    public final double[] getElite() {
        return  getElite(population);
    }
    public final double[] getElite(Individual[] p) {
        double highScore = p[0].getFitness();
        int index=0;
        for (int i=1; i<p.length; i++) {
            if(p[i].getFitness() > highScore) {
                highScore = p[i].getFitness();
                index = i;
            } 
        }
        return  p[index].getGene();
    }
    /*public final double[][] getElite(Individual[] p, int num) {
        
    }*/
}
