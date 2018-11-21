/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import java.util.List;

/**
 *
 * @author Char Aznable
 */
public class RcgaPuma3 extends Rcga{
    
    public RcgaPuma3(int populationNum, int generationLim, int parentsNum, int childrenNum) {
        super(populationNum, generationLim, parentsNum, childrenNum);
        
        for (int i=0; i<populationNum; i++) {
            population.add(new OnePuma3());
            offspring.add(new OnePuma3());
        }
        for (int i=0; i<parentsNum; i++) {
            parents.add(new OnePuma3());
        }
        for (int i=0; i<childrenNum; i++) {
            children.add(new OnePuma3());
        }
    }

    @Override
    void crossover(List<Individual> p, List<Individual> c) {
        blxAlpha(p, c);
    }

    @Override
    public void evolute() {
        int[] selectIndex = randomSelect(population, parents);
        crossover(parents, children);
        eliteSelect(children, children);
        for (int i=0; i<childrenNum; i++) {
            population.set(selectIndex[i], children.get(i));
        }
    }
}
