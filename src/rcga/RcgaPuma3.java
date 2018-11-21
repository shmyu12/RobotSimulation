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
public class RcgaPuma3 extends Rcga{
    
    public RcgaPuma3(int populationNum, int generationLim, int parentsNum, int childrenNum) {
        super(populationNum, generationLim, parentsNum, childrenNum);
        
        for (int i=0; i<populationNum; i++) {
            population[i] = new OnePuma3();
            offspring[i] = new OnePuma3();
        }
    }

    @Override
    double[] crossover(double[] aGene, double[] bGene) {
        return blxAlpha(aGene, bGene);
    }    
}
