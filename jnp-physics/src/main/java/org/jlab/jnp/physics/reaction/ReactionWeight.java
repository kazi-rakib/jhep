/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics.reaction;

import java.util.Map;

/**
 *
 * @author gavalian
 */
public interface ReactionWeight {
    public double getWeight(Map<String,Double> ps);
}
