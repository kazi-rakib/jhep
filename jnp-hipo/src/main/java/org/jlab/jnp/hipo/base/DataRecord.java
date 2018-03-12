/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.base;

/**
 *
 * @author gavalian
 */
public interface DataRecord {
    boolean   readRecord(DataSource source, int index);
    boolean   readEvent(DataEvent event, int index);
    int       getEventCount();
}
