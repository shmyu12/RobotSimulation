/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcga;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Char Aznable
 */
public class MonsterRcga extends Rcga{
    
    public MonsterRcga(int populationNum, int generationLim, int parentsNum, int childrenNum) {
        super(populationNum, generationLim, parentsNum, childrenNum);
        
        alpha = 0.3;
        for (int i=0; i<populationNum; i++) {
            population.add(new TestMonster());
            offspring.add(new TestMonster());
        }
        for (int i=0; i<parentsNum; i++) {
            parents.add(new TestMonster());
        }
        for (int i=0; i<childrenNum; i++) {
            children.add(new TestMonster());
        }
    }

    @Override
    void crossover(List<Individual> p, List<Individual> c) {
        blxAlpha(p, c);
    }
    
    @Override
    public void evolute() {
        for (int i=0; i<populationNum; i++) offspring.get(i).clone(population.get(i));
        int[] selectIndex = randomSelect(offspring, parents);
        crossover(parents, children);
        evaluate(children);
        eliteSelect(children, parents);
        for (int i=0; i<parentsNum; i++) {
            System.out.print(selectIndex[i]+"\t");
            offspring.get(selectIndex[i]).clone(parents.get(i));
        }
        System.out.println();
        for (int i=0; i<populationNum; i++) population.get(i).clone(offspring.get(i));
    }
    
    public static void main(String args[]){
        
        MonsterRcga world = new MonsterRcga(100, 1000, 4, 8);
        world.bigbang();
    }
    
}
