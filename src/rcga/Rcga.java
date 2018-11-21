/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    
    //double sum;
    int gen;
    //double crossoverP;
    double alpha;
    
    List<Individual> population;
    List<Individual> offspring;
    final List<Individual> children;
    final List<Individual> parents;
    
    public Rcga(int populationNum, int generationLim, int parentsNum, int childrenNum) {
        if(populationNum<parentsNum) {
            System.out.println("Parameter error");
            //exit();
        }
        this.populationNum = populationNum;
        this.generationLim = generationLim;
        this.childrenNum = childrenNum;
        this.parentsNum = parentsNum;
        population = new ArrayList<>(populationNum);
        //offspring = new ArrayList<>(populationNum);
        children = new ArrayList<>(childrenNum);
        parents = new ArrayList<>(parentsNum);
    }
    
    public final void twoPointCrossover(List<Individual> p, List<Individual> c) {
        
        for (int i=0; i<c.size(); i++) {
            List<Individual> pCopy = new ArrayList<>(p);
            Individual parent1 = pCopy.remove(RangeRandom(0, pCopy.size()-1));
            Individual parent2 = pCopy.remove(RangeRandom(0, pCopy.size()-1));
            double[] aGene = parent1.getGene();
            double[] bGene = parent2.getGene();
            
            int geneSize = Individual.geneSize;
            int random1 = RangeRandom(1, geneSize-2);
            int random2 = RangeRandom(random1+1, geneSize-1);

            double[][] tmpGene1 = new double[2][random1];
            double[][] tmpGene2 = new double[2][random2-random1];
            double[][] tmpGene3 = new double[2][geneSize-random2];

            for (int j=0; i<random1; j++) {
                tmpGene1[0][j] = aGene[j];
                tmpGene1[1][j] = bGene[j];
            }
            for (int j=random1; j<random2; j++) {
                tmpGene2[0][j-random1] = aGene[j];
                tmpGene2[1][j-random1] = bGene[j];
            }
            for (int j=random2; j<geneSize; j++) {
                tmpGene3[0][j-random2] = aGene[j];
                tmpGene3[1][j-random2] = bGene[j];
            }


            double[][] allChildren = new double[8][geneSize];
            int count=0;
            for (int j=0; j<2; j++) {
                for (int k=0; k<2; k++) {
                    for(int l=0; l<2; l++) {
                        System.arraycopy(tmpGene1[j], 0, allChildren[count], 0, tmpGene1[j].length);
                        System.arraycopy(tmpGene2[k], 0, allChildren[count], tmpGene1[j].length, tmpGene2[k].length);
                        System.arraycopy(tmpGene3[l], 0, allChildren[count], tmpGene1[j].length+tmpGene2[k].length, tmpGene3[l].length);
                        count++;
                    }
                }
            }
            
            c.get(i).setGene(allChildren[RangeRandom(0, 7)]);
            
        }
        
        //child1.setGene(allChildren[RangeRandom(0, 7)]);
        //child2.setGene(allChildren[RangeRandom(0, 7)]);
        //ret[1] = c[RangeRandom(0, 7)];
        
        /*List<Individual> ret = new ArrayList<>();
        ret.add(a);
        ret.add(b);
        return ret;*/
        //Individual[] ret = new Individual[2];
        //ret[0] = a;
        //ret[1] = b;
    }
    public final void blxAlpha(List<Individual> p, List<Individual> c) {
        
        for (int i=0; i<c.size(); i++) {
            List<Individual> pCopy = new ArrayList<>(p);
            Individual parent1 = pCopy.remove(RangeRandom(0, pCopy.size()-1));
            Individual parent2 = pCopy.remove(RangeRandom(0, pCopy.size()-1));
            double[] aGene = parent1.getGene();
            double[] bGene = parent2.getGene();
            
            int geneSize = Individual.geneSize;
            
            double[] newGene = new double[geneSize];
            for (int j=0; j<geneSize; j++) {
                double minx = aGene[j]<bGene[j] ? aGene[j] : bGene[j];
                double maxx = minx==bGene[j] ? aGene[j] : bGene[j];
                double dx = abs(maxx-minx);
                double xNext;
                do {
                    xNext = random()*dx*(2*alpha+1) + minx - alpha*dx;
                } while(xNext<Individual.getMin()[j] || xNext>Individual.getMax()[j]);
                newGene[j] = xNext;
                //System.out.println("a:"+aGene[i]+"\tb:"+bGene[i]+"\tnext"+xNext);
            }
            c.get(i).setGene(newGene);
        }
    }
    
    
    abstract void crossover(List<Individual> p, List<Individual> c);
    abstract public void evolute();
    
    public final void bigbang() {
        
        gen=1;
        init();
        evaluate();
        generationInfo();
        eliteInfo();
        populationInfo();
        System.out.println();
        
        for (gen=2; gen<=generationLim; gen++) {
            evolute();
            evaluate();
            generationInfo();
            eliteInfo();
            populationInfo();
            System.out.println();
        }
    }
    
    public final int[] rouletteSelect(List<Individual> p, List<Individual> s) {
        int[] selectIndex = new int[s.size()];
        double sum=evaluate(p);
        
        for (int i=0; i<s.size(); i++) {
            int index;
            do {
                index = RangeRandom(0, p.size()-1);
            } while((p.get(index).getFitness()/sum) < random());
            s.get(i).setGene(p.get(index).getGene());
            selectIndex[i] = index;
        }
        return selectIndex;
    }
    
    public final int[] randomSelect(List<Individual> p, List<Individual> s) {
        int[] selectIndex = new int[s.size()];
        List<Individual> pCopy = new ArrayList<>(p);
        for (int i=0; i<s.size(); i++) {
            int index = RangeRandom(0, pCopy.size()-1);
            s.set(i, pCopy.remove(index));
            selectIndex[i] = index;
        }
        return selectIndex;
    }
    
    public final int[] eliteSelect(List<Individual> p, List<Individual> s) {
        int[] selectIndex = new int[s.size()];
        List<Individual> pCopy = new ArrayList<>(p);
        for (int i=0; i<s.size(); i++) {
            int index = getEliteIndex(pCopy);
            s.set(i, pCopy.remove(index));
            selectIndex[i] = index;
        }
        return selectIndex;
    }
    
    public final double evaluate() {
        return evaluate(population);
    }
    
    public final double evaluate(List<Individual> p) {
        double sum = 0;
        for(Individual a : p) {
            a.evaluate();
            sum += a.getFitness();
        }
        return sum;
    }
    
    public final void init(){
        for(Individual a: population) {
            a.init();
        }
    }
    
    public void generationInfo() {
        System.out.print("gen:"+gen+"\n");
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
            System.out.print(Arrays.toString(population.get(i).getGene())+"\n");
        }
    }
    
    public final Individual getElite() {
        return  getElite(population);
    }
    public final Individual getElite(List<Individual> p) {
        double highScore = p.get(0).getFitness();
        int index=0;
        for (int i=1; i<p.size(); i++) {
            if(p.get(i).getFitness() > highScore) {
                highScore = p.get(i).getFitness();
                index = i;
            } 
        }
        return  p.get(index);
    }
    public final int getEliteIndex(List<Individual> p) {
        double highScore = p.get(0).getFitness();
        int index=0;
        for (int i=1; i<p.size(); i++) {
            if(p.get(i).getFitness() > highScore) {
                highScore = p.get(i).getFitness();
                index = i;
            } 
        }
        return index;
    }
}
