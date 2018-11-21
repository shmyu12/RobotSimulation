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
public class MonsterRcga extends Rcga{
    
    public MonsterRcga(int populationNum, int generationLim) {
        super(populationNum, generationLim);
        
        crossoverP = 0.8;
        alpha = 0.5;
        for (int i=0; i<populationNum; i++) {
            population[i] = new TestMonster();
            offspring[i] = new TestMonster();
        }
    }

    @Override
    double[][] crossover(double[] aGene, double[] bGene) {
        return blxAlpha(aGene, bGene);
    }
    
    public static void main(String args[]){
        
        MonsterRcga world = new MonsterRcga(10, 100);
        world.bigbang();
    }
    
}
