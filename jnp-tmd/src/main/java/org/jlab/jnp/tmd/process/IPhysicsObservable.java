/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.Map;

/**
 *
 * @author gavalian
 */
public interface IPhysicsObservable {
    public String getName();
    public double getValue(Map<String,Double> ps);
}
