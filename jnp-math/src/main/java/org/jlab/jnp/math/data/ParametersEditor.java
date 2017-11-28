/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author gavalian
 */
public class ParametersEditor extends JPanel {
    
    private List<JSpinner>  valuesMinimum = null;
    private List<JSpinner>  valuesMaximum = null;
    private List<JSpinner>  valuesCurrent = null;
    
    private final Parameters   editorParameters = new Parameters();
    
    public static final int PARAM_EDITOR_NO_VALUE = 1;
    public static final int PARAM_EDITOR_NO_RANGE = 4;
    private int   PARAM_EDITOR_MODE = 0;
    private String parameterGroupName = "parameters";
    
    
    public ParametersEditor(int mode){
        PARAM_EDITOR_MODE = mode;
    }
    public ParametersEditor(Parameters pars){
        super();
        this.setBorder(BorderFactory.createTitledBorder(this.parameterGroupName));
        editorParameters.copyFrom(pars);
        initUI();
    }
    
    public ParametersEditor(Parameters pars, int mode){
        super();
        this.PARAM_EDITOR_MODE = mode;
        this.setBorder(BorderFactory.createTitledBorder(this.parameterGroupName));
        editorParameters.copyFrom(pars);
        initUI();
    }
    
    public ParametersEditor(Parameters pars, String name, int mode){
        super();
        this.PARAM_EDITOR_MODE  = mode;
        this.parameterGroupName = name;
        this.setBorder(BorderFactory.createTitledBorder(this.parameterGroupName));
        editorParameters.copyFrom(pars);
        initUI();
    }
    
    private void initUI(){
        setLayout(new MigLayout());
        for(Map.Entry<String,Parameter> entry : editorParameters.getParameters().entrySet()){
            String      name = entry.getKey();
            JSpinner spValue = this.createSpinner(100, entry.getValue().getValue());
            JSpinner spMin   = this.createSpinner(100, entry.getValue().getMin());
            JSpinner spMax   = this.createSpinner(100, entry.getValue().getMax());
            JLabel  dimLabel = new JLabel(entry.getKey());
            
            spMax.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                   Double value = (Double) spMax.getModel().getValue();
                   editorParameters.getParameter(name).setMax(value);
                   if(value<editorParameters.getParameter(name).getValue()){
                       spValue.setValue(value);
                       editorParameters.getParameter(name).setValue(value);
                   }
                   
                   if(value<=editorParameters.getParameter(name).getMin()){
                       Double min = editorParameters.getParameter(name).getMin();
                       spMax.setValue(min);
                       editorParameters.getParameter(name).setMax(min);
                   }
                }
            });
            
            spMin.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                   Double value = (Double) spMin.getModel().getValue();
                   editorParameters.getParameter(name).setMin(value);
                    if(value>editorParameters.getParameter(name).getValue()){
                       spValue.setValue(value);
                       editorParameters.getParameter(name).setValue(value);
                   }
                   
                   if(value>=editorParameters.getParameter(name).getMax()){
                       Double max = editorParameters.getParameter(name).getMax();
                       spMin.setValue(max);
                       editorParameters.getParameter(name).setMin(max);
                   }
                }
            });
            
            spValue.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                   Double value = (Double) spValue.getModel().getValue();
                   editorParameters.getParameter(name).setValue(value);
                   if(value<editorParameters.getParameter(name).getMin()){
                       spValue.setValue(editorParameters.getParameter(name).getMin());
                       editorParameters.getParameter(name).setValue(editorParameters.getParameter(name).getMin());
                   }
                   if(value>editorParameters.getParameter(name).getMax()){
                       spValue.setValue(editorParameters.getParameter(name).getMax());
                       editorParameters.getParameter(name).setValue(editorParameters.getParameter(name).getMax());
                   }
                   //editorParameters.getParameter(name).setMax(value);
                }
            });
            
            this.add(dimLabel,"pushx");
            this.add(spValue,"pushx, grow");
            this.add(spMin,"pushx, grow");
            this.add(spMax,"pushx, growx, wrap");
            
            int status = PARAM_EDITOR_MODE&ParametersEditor.PARAM_EDITOR_NO_RANGE;
            if(status!=0){
                spMin.setEnabled(false);
                spMax.setEnabled(false);
            }
            
            status = PARAM_EDITOR_MODE&ParametersEditor.PARAM_EDITOR_NO_VALUE;
            if(status!=0){
                spValue.setEnabled(false);
            }
        }
    }

    public Parameters getParameters(){ return this.editorParameters;}
    
    private JSpinner createSpinner(int width, double value){
        JSpinner spinner = new JSpinner();
        SpinnerNumberModel model = new SpinnerNumberModel(0.0,-1000.0 ,1000.0,0.01);
        model.setValue(value);
        spinner.setModel(model);
        //this.setSpinnerSize(spinner, width);
        return spinner;
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        
        Parameters parms_f1x = new Parameters("f1x");
        parms_f1x.addParameter("a",0.0, 1.0);
        parms_f1x.addParameter("b",0.0, 1.0);
        parms_f1x.addParameter("c",0.0, 1.0);
        
        Parameters parms_d1z = new Parameters("d1z");
        parms_d1z.addParameter("a", 1.0, 2.0);
        parms_d1z.addParameter("b", 1.0, 2.0);
        parms_d1z.addParameter("c", 1.0, 2.0);
        
        Parameters parms = new Parameters("sidis");
        parms.addParametersAsGroup(parms_f1x);
        parms.addParametersAsGroup(parms_d1z);
        System.out.println(parms.toString());
        
        Parameters subset = parms.getParametersAsGroup("f1x");
        System.out.println(subset.toString());
        ParametersEditor viewer = new ParametersEditor(parms,"Phase Space",ParametersEditor.PARAM_EDITOR_NO_VALUE);
        frame.add(viewer);
        frame.pack();
        frame.setVisible(true);
    }
}
