/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ParticleList {
    /**
     * Array containing particles.
     */
    private final List<Particle>  particles = new ArrayList<Particle>();
    /**
     * creates an empty list of particles.
     */
    public ParticleList(){
        
    }
    
    /**
     * adds particle to the list.
     * @param p new Particle to add to the list.
     */
    public void add(Particle p){
        this.particles.add(p);
    }
    /**
     * resized the array by eliminating excess elements from the array if the array
     * is larger than current size, or adds new particles if the current size is 
     * smaller than size given as argument.
     * @param size desired size of the array
     */
    public void resize(int size){
        int cnt = count();
        if(size>count()){
            //System.out.println("resizing UP from " + cnt + " to " + size);
            for(int i = 0; i < size-cnt; i++) add(new Particle());
        } else {
            //System.out.println("resizing DOWN from " + cnt + " to " + size + "  removal " + (cnt-size));
            int nr = cnt-size;
            for(int i = 0; i < nr; i++) particles.remove(0);
        }
        //System.out.println(" after resize " + this.particles.size());
    }
    /**
     * clears the list. resulting list will have zero elements.
     */
    public void clear(){ particles.clear();}
    /**
     * returns number of the particles in the list.
     * @return particle count in the list.
     */
    public int count(){ return particles.size();}
    /**
     * returns number of particles of given charge
     * @param charge charge of the particle
     * @return count
     */
    public int countByCharge(int charge){
        int count = 0;
        for(Particle p : particles){
            if(p.charge()==charge&&p.getStatus()==1) count++;
        }
        return count;
    }
    /**
     * returns number of particles with given particle id (LUND id)
     * @param pid particle id
     * @return count number of particles with given pid
     */
    public int countByPid(int pid){
        int count = 0;
        for(Particle p : particles){
            if(p.pid()==pid&&p.getStatus()==1) count++;
        }
        return count;
    }
    
    public void reset(){
        int iter = count();
        for(int i = 0; i < iter; i++){
            particles.get(i).initParticle(0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);            
        }
    }
   
    /**
     * returns index-th particle from the list.
     * @param index index of the particle
     * @return Particle class.
     */
    public Particle get(int index){
        return this.particles.get(index);
    }
    /**
     * returns particle with id = pid and skips 'skip' particles
     * @param pid
     * @param skip
     * @return Particle class
     */
    public Particle getByPid(int pid, int skip){
        int skipped = 0;
        for(int i = 0; i < particles.size();i++){
            if(particles.get(i).pid()==pid&&particles.get(i).getStatus()==1){
                if(skipped==skip){
                    return particles.get(i);
                } else {
                    skipped++;
                }
            }
        }
        return null;
    }
    /**
     * returns particles by charge skipping skip particles.
     * @param charge
     * @param skip
     * @return Particle class
     */
    public Particle getByCharge(int charge, int skip){
        int skipped = 0;
        for(int i = 0; i < particles.size();i++){
            if(particles.get(i).charge()==charge&&particles.get(i).getStatus()==1){
                if(skipped==skip){
                    return particles.get(i);
                } else {
                    skipped++;
                }
            }
        }
        return null;
    }
    /**
     * Sorts particles according to their PID, and their momentum.
     * The momentum will be in descending order. 
     */
    public void sort(){
        Collections.sort(particles);
    }
    /**
     * returns LUND representation string of the list. For each particle
     * toLundString() method is called.
     * @return string representation of the event.
     */
    public String toLundString(){
        StringBuilder str = new StringBuilder();
        for(int loop = 0; loop < particles.size(); loop++){
            str.append(String.format("%5d", loop+1));
            str.append(particles.get(loop).toLundString());
            str.append("\n");
        }
        return str.toString();
    }
}
