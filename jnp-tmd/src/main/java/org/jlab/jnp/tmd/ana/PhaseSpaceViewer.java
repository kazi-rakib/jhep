/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.ana;

import java.awt.Dimension;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;
import org.jlab.jnp.physics.reaction.PhaseSpace;

/**
 *
 * @author gavalian
 */
public class PhaseSpaceViewer extends JPanel {
    
    private List<JSpinner>  valuesMinimum = null;
    private List<JSpinner>  valuesMaximum = null;
    private List<JSpinner>  valuesCurrent = null;
    private PhaseSpace      phaseSpace    = new PhaseSpace();
    
    public PhaseSpaceViewer(){
       super();
       this.setBorder(BorderFactory.createTitledBorder("parameters"));
       this.setSize(600, 900);
       phaseSpace.add("a", 0.5, 0.0, 1.0);
       phaseSpace.add("b", 0.6, 0.0, 1.0);
       phaseSpace.add("c", 0.7, 0.0, 1.0);
       initUI();
       
    }
    
    private void initUI(){
        this.setLayout(new MigLayout());//"","[][]20[]","[]20[]"));
        Set<String> keys = phaseSpace.getKeys();
        
        for(String key : keys){
            JLabel  dimLabel = new JLabel(key);
            
            JSpinner spValue = this.createSpinner(100, phaseSpace.getDimension(key).getValue());
            JSpinner spMin   = this.createSpinner(100, phaseSpace.getDimension(key).getMin());
            JSpinner spMax   = this.createSpinner(100, phaseSpace.getDimension(key).getMax());
            spMax.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                   Double value = (Double) spMax.getModel().getValue();
                   System.out.println("MAXIMUM CHANGED TO " + value);
                   
                }
            });
            spValue.setEnabled(false);
            this.add(dimLabel,"pushx");
            this.add(spValue,"pushx, grow");
            this.add(spMin,"pushx, grow");
            this.add(spMax,"pushx, growx, wrap");
        }
    }
    
    private JSpinner createSpinner(int width, double value){
        JSpinner spinner = new JSpinner();
        SpinnerNumberModel model = new SpinnerNumberModel(0.0,-1000.0 ,1000.0,0.1);
        model.setValue(value);
        spinner.setModel(model);
        this.setSpinnerSize(spinner, width);
        return spinner;
    }
    
    private void setSpinnerSize(JSpinner jsp, int width){
        int h = jsp.getHeight();
        jsp.setSize(new Dimension(width, h));
    }
    
    public void addDimensionUI(){
        
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        PhaseSpaceViewer viewer = new PhaseSpaceViewer();
        frame.add(viewer);
        frame.pack();
        frame.setVisible(true);
    }
}
