/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.base;

import java.util.List;

/**
 *
 * @author gavalian
 */
public interface DataRecord {
    
    int      readRecord(DataSource source, int index);
    int      readEvent(DataEvent event, int index);
    /**
     * reads the next event from the record, returns the
     * length in bytes of the event that was read.
     * @param event DataEvent object to read into
     * @return number of bytes read
     */
    int      nextEvent(DataEvent event);
    /**
     * reads list of events into the provided array,
     * the number of full events read will be returned.
     * @param evList list of data events
     * @param maxCount maximum count to read
     * @return returns the number of events that were read.
     */
    int      nextEvents(List<DataEvent> evList, int maxCount);
    /**
     * returns the count of events in the record.
     * @return 
     */
    int      getEventCount();
    
}
