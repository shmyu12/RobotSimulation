/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotics;

/**
 *
 * @author raven
 */
public class Count {
    
    private int num;
    final int max;
    
    public Count(int max) {
        num = 0;
        this.max = max;
    }
    
    public void reset() {
        this.num = 0;
    }
    
    public boolean countUp() {
        this.num++;
        if (num>max) {
            return false;
        }
        return true;
    }
    
    public boolean countDown() {
        this.num--;
        if (num<0) {
            return false;
        }
        return true;
    }
}
