/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.schema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jlab.jnp.hipo.schema.Schema.SchemaEntry;

/**
 *
 * @author gavalian
 */
public class SchemaViewer extends JPanel {
    
    private JTable schemaTable = null;
    private Schema schema      = null;
    private SchemaTableModel model = new SchemaTableModel();
    
    
    public SchemaViewer(){
        super();
    
        this.setLayout(new BorderLayout());
        Object[][] data = {
    {"Kathy", "Smith",
     "Snowboarding", new Integer(5), new Boolean(false)},
    {"John", "Doe",
     "Rowing", new Integer(3), new Boolean(true)},
    {"Sue", "Black",
     "Knitting", new Integer(2), new Boolean(false)},
    {"Jane", "White",
     "Speed reading", new Integer(20), new Boolean(true)},
    {"Joe", "Brown",
     "Pool", new Integer(10), new Boolean(false)}
};
        String[] columnNames = {"First Name",
                        "Last Name",
                        "Sport",
                        "# of Years",
                        "Vegetarian"};
        Color headerColor = new Color(248, 206, 70);
        schemaTable = new JTable(model);
        JTableHeader Header =  schemaTable.getTableHeader();
        Header.setBackground(headerColor);
        JScrollPane scrollPane = new JScrollPane(schemaTable);
        this.add(scrollPane);
    }
    
    public void setSchema(Schema sc){
        this.model.setSchema(sc);
    }
    
    public static class SchemaTableModel extends DefaultTableModel {
        
        private Schema modelSchema = null;
        private List<String> schemaEntries = null;
        
        private String[] columnNames = new String[]{"Group","Name","Item","Type"};
        
        public void setSchema(Schema schema){
            this.modelSchema = schema;
            schemaEntries = schema.schemaEntryList();
        }
        
        public SchemaTableModel(){
            
        }
        
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }    
    
        @Override
        public int getColumnCount(){
            return columnNames.length;
        }
    
        @Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }
        
        @Override
        public int getRowCount(){
            if(modelSchema==null) return 0;
            return modelSchema.getEntries();   
        }
    
    
        @Override
        public Object getValueAt(int row, int column) {
            if(modelSchema==null) return "N/A";
            SchemaEntry entry = this.modelSchema.getEntry(this.schemaEntries.get(row));
            if(column==1){
                return entry.getName();
            }
            if(column==0){
                return this.modelSchema.getGroup();
            }
            if(column==2){
                return entry.getId();
            }
            
            if(column==3){
                return entry.getType().getName();
            }
            return "F";
        }
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        SchemaViewer scViewer = new SchemaViewer();
        Schema schema = new Schema("mc::event" , 32111, "pid/S:px/F:py/F:pz/F:vx/F:vy/F:vz/F:mass/F:parent/B:status/B");
        scViewer.setSchema(schema);
        frame.add(scViewer);
        frame.pack();
        frame.setVisible(true);
    }
}
