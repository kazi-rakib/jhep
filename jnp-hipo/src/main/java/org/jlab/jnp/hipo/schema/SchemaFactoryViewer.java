/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.schema;

import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

/**
 *
 * @author gavalian
 */
public class SchemaFactoryViewer extends JPanel {
    
    private JSplitPane splitPane = null;
    private JTable     factoryTable = null;
    private SchemaViewer schemaViewer = null;
    private SchemaFactory schemaFactory = null;    
    
    public SchemaFactoryViewer(){
        super();
        this.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        schemaViewer = new SchemaViewer();
        factoryTable = new JTable();
        
        JScrollPane scrollPane = new JScrollPane(factoryTable);
        
        splitPane.setTopComponent(schemaViewer);
        splitPane.setBottomComponent(scrollPane);
        this.add(splitPane);
    }
    
    
    public static void main(String[] args){
        try {
            JFrame frame = new JFrame();
            SchemaFactoryViewer scViewer = new SchemaFactoryViewer();
            Schema schema = new Schema("mc::event" , 32111, "pid/S:px/F:py/F:pz/F:vx/F:vy/F:vz/F:mass/F:parent/B:status/B");
            Schema schemadata = new Schema("data::event" , 32211, "pid/S:px/F:py/F:pz/F:vx/F:vy/F:vz/F:mass/F:parent/B:status/B");
            SchemaFactory factory = new SchemaFactory();
            
            factory.addSchema(schema);
            factory.addSchema(schemadata);
            
//        scViewer.setSchema(schema);
frame.add(scViewer);
frame.pack();
frame.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(SchemaFactoryViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
