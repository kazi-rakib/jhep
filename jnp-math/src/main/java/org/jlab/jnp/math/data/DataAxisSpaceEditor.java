/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author gavalian
 */
public class DataAxisSpaceEditor extends JPanel {
    
    private List<JLabel>  axisLabels = new ArrayList<JLabel>();
    private List<JTextField> axisMin = new ArrayList<JTextField>();
    private DataAxisSpace    axisSpace = null;
    
    public DataAxisSpaceEditor(DataAxisSpace axis_sp){
        super();
        this.axisSpace = axis_sp;
        initUI();
    }
    
    public DataAxisSpaceEditor(){
        super();
        initUI();    
    }
    
    private void initUI(){
        GridLayout layout = new GridLayout(4,4,10,10);
        setLayout(layout);
        
        
    }
    
    public static void main(String[] args){
        DataAxisSpace  space = new DataAxisSpace();
        space.addAxis(new DataAxis("xb",10,0.0,1.0));
    }
}
