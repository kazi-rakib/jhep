/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.map;

import org.jlab.jnp.hipo.data.HipoEvent;

/**
 *
 * @author gavalian
 */
public interface MapReducer {
    Boolean reduce(HipoEvent event);
}
