/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.matrix;

/**
 *
 * @author gavalian
 */
public interface IMatrix {
    
    int     getRank();
    int     getBins(int dim);
    double  getValue(int... bins);
    
}
