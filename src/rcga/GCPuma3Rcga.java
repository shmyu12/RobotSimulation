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
public class GCPuma3Rcga extends Rcga{
    
    public GCPuma3Rcga(int populationNum, int generationLim, int parentsNum, int childrenNum) {
        super(populationNum, generationLim, parentsNum, childrenNum);
        
        alpha = 0.5;
        
        for (int i=0; i<populationNum; i++) {
            population.add(new OneGCPuma3());
            offspring.add(new OneGCPuma3());
        }
        for (int i=0; i<parentsNum; i++) {
            parents.add(new OneGCPuma3());
        }
        for (int i=0; i<childrenNum; i++) {
            children.add(new OneGCPuma3());
        }
    }

    @Override
    void crossover(List<Individual> p, List<Individual> c) {
        blxAlpha(p, c);
    }

    @Override
    public void evolute() {
        jgg();
    }
    
    
    public static void main(String args[]){
        
        GCPuma3Rcga world = new GCPuma3Rcga(50, 100, 10, 30);
        world.bigbang();
    }
}
