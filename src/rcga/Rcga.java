/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import java.util.Arrays;
import static rcga.Individual.getMax;
import static rcga.Individual.getMin;
import static tools.MyMath.RangeRandom;

/**
 *
 * @author Char Aznable
 */
abstract public class Rcga {
    
    //final int geneSize;
    final int populationNum;
    final int generationLim;
    
    double sum;
    int gen;
    double crossoverP;
    double alpha;
    
    final Individual[] population;
    final Individual[] offspring;
    
    public Rcga(int populationNum, int generationLim) {
        this.populationNum = populationNum;
        this.generationLim = generationLim;
        population = new Individual[populationNum];
        offspring = new Individual[populationNum];
    }
    
    public final double[][] twoPointCrossover(double[] aGene, double[] bGene) {
        
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
        
        
        double[][] children = new double[8][geneSize];
        int c=0;
        for (int i=0; i<2; i++) {
            for (int j=0; j<2; j++) {
                for(int k=0; k<2; k++) {
                    System.arraycopy(tmpGene1[i], 0, children[c], 0, tmpGene1[i].length);
                    System.arraycopy(tmpGene2[j], 0, children[c], tmpGene1[i].length, tmpGene2[j].length);
                    System.arraycopy(tmpGene3[k], 0, children[c], tmpGene1[i].length+tmpGene2[j].length, tmpGene3[k].length);
                    c++;
                }
            }
        }
        
        //a = new Individual();
        //b = new Individual();
        //a.setGene(children[RangeRandom(0, 7)]);
        //b.setGene(children[RangeRandom(0, 7)]);
        double[][] ret = new double[2][geneSize];
        ret[0] = children[RangeRandom(0, 7)];
        ret[1] = children[RangeRandom(0, 7)];
        
        /*List<Individual> ret = new ArrayList<>();
        ret.add(a);
        ret.add(b);
        return ret;*/
        //Individual[] ret = new Individual[2];
        //ret[0] = a;
        //ret[1] = b;
        return ret;
    }
    public final double[][] blxAlpha(double[] aGene, double[] bGene) {
        
        int geneSize = aGene.length;
        double[][] ret = new double[2][geneSize];
        
        for (int j=0; j<2; j++) {
            for (int i=0; i<geneSize; i++) {
                double minx = aGene[i]<bGene[i] ? aGene[i] : bGene[i];
                double maxx = minx==bGene[i] ? aGene[i] : bGene[i];
                double dx = abs(maxx-minx);
                double xNext;
                do {
                    xNext = random()*dx*(2*alpha+1) + minx - alpha*dx;
                } while(xNext<Individual.getMin()[i] || xNext>Individual.getMax()[i]);
                ret[j][i] = xNext;
                //System.out.println("a:"+aGene[i]+"\tb:"+bGene[i]+"\tnext"+xNext);
            }
        }
        return ret;
    }
    
    
    abstract double[][] crossover(double[] aGene, double[] bGene);
    
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
        int i;
        for (i=0; i<populationNum*crossoverP; i+=2) {
            double[][] newGene = crossover(randomSelect(), randomSelect());
            offspring[i].setGene(newGene[0]);
            offspring[i+1].setGene(newGene[1]);
        }
        for (; i<populationNum; i++) {
            offspring[i].setGene(rouletteSelect());
        }
        
        System.arraycopy(offspring, 0, population, 0, populationNum);
    }
    
    public final double[] rouletteSelect() {
        int i;
        do {
            i = RangeRandom(0, populationNum-1);
        } while((population[i].getFitness()/sum) < random());
        return population[i].getGene();
    }
    
    public final double[] randomSelect() {
        return population[RangeRandom(0, populationNum-1)].getGene();
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
        double[] eliteGene = getElite().getGene();
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
    
    public final Individual getElite() {
        double highScore = population[0].getFitness();
        int index=0;
        for (int i=1; i<populationNum; i++) {
            if(population[i].getFitness() > highScore) {
                highScore = population[i].getFitness();
                index = i;
            } 
        }
        return  population[index];
    }
}
