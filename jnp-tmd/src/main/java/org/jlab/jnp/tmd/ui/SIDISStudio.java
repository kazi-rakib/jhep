/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.jnp.foam.MCFoam;
import org.jlab.jnp.math.data.Parameter;
import org.jlab.jnp.math.data.Parameters;
import org.jlab.jnp.math.data.ParametersEditor;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.jlab.jnp.tmd.process.SIDISPhysicsObservables;
import org.jlab.jnp.tmd.process.SIDISReactionWeight;
import org.jlab.jnp.utils.data.ArrayUtils;

/**
 *
 * @author gavalian
 */
public class SIDISStudio  extends JPanel implements ActionListener {
    
    private SIDISReactionWeight cross = new SIDISReactionWeight();
    private Parameters          reactionPhaseSpace = new Parameters();
    private ParametersEditor    editorParams = null;
    private ParametersEditor    editorPhaseSpace = null;
    private EmbeddedCanvasTabbed canvas = null;
    
    private H1F                   h1_Q2  = null;
    private H1F                   h1_PT  = null;
    private H1F                   h1_PHI = null;
    private H1F                   h1_XB  = null;
    private H1F                   h1_Z   = null;
    private H2F                   h2_PT_Z = null;
    
    public SIDISStudio(){
        super();
        this.setSize(800, 800);
        this.setLayout(new BorderLayout());        
        initUI();
    }
    
    private void initUI(){
        
        JSplitPane splitPane = new JSplitPane();
        
        
        canvas = new EmbeddedCanvasTabbed();
        
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel,BoxLayout.Y_AXIS));
        editorParams = new ParametersEditor(cross.getObservables().getParameters(),
                ParametersEditor.PARAM_EDITOR_NO_RANGE);
        parametersPanel.add(editorParams);
        
        reactionPhaseSpace.addParameter("E", 11.0,11.0);
        reactionPhaseSpace.addParameter("q2", 1.0,10.0);
        reactionPhaseSpace.addParameter("xb", 0.0,1.0);
        reactionPhaseSpace.addParameter("z", 0.0,1.0);
        reactionPhaseSpace.addParameter("pt", 0.0,3.0);
        reactionPhaseSpace.addParameter("phi", -Math.PI,Math.PI);
        
        editorPhaseSpace = new ParametersEditor(reactionPhaseSpace,"Kinematics",ParametersEditor.PARAM_EDITOR_NO_VALUE);
        
        parametersPanel.add(editorPhaseSpace);
        
        
        JPanel actionPanel = new JPanel();
        actionPanel.setBorder(BorderFactory.createTitledBorder("action"));
        actionPanel.setLayout(new FlowLayout());
        JButton buttonRun = new JButton("Run");
        JButton buttonReset = new JButton("Reset");
        
        
        buttonRun.addActionListener(this);
        actionPanel.add(buttonRun);
        actionPanel.add(buttonReset);
        
        parametersPanel.add(actionPanel);
        
        splitPane.setLeftComponent(parametersPanel);
        splitPane.setRightComponent(canvas);
        this.add(splitPane,BorderLayout.CENTER);
        //this.add(parametersPanel,BorderLayout.LINE_START);
    }
    
    private void initHistograms(){
        canvas.getCanvas().clear();
        canvas.getCanvas().divide(3, 2);
        Parameters pars  = this.editorPhaseSpace.getParameters();
        h1_Q2 = new H1F("Q2","Q^2 [GeV^2]",40,pars.getParameter("q2").getMin(),pars.getParameter("q2").getMax());
        h1_Q2.setFillColor(33);
        canvas.getCanvas().cd(0); canvas.getCanvas().draw(h1_Q2);
        h1_XB = new H1F("XB","Bjorken x", 40,pars.getParameter("xb").getMin(),pars.getParameter("xb").getMax());
        h1_XB.setFillColor(33);
        canvas.getCanvas().cd(1); canvas.getCanvas().draw(h1_XB);
        h1_Z  = new H1F("Z","z",40,pars.getParameter("z").getMin(),pars.getParameter("z").getMax());
        h1_Z.setFillColor(33);
        canvas.getCanvas().cd(2); canvas.getCanvas().draw(h1_Z);
        h1_PT = new H1F("PT","pt",40,pars.getParameter("pt").getMin(),pars.getParameter("pt").getMax());
        h1_PT.setFillColor(33);
        canvas.getCanvas().cd(3); canvas.getCanvas().draw(h1_PT);
        h1_PHI = new H1F("PHI","#phi [rad]",40,pars.getParameter("phi").getMin(),pars.getParameter("phi").getMax());
        h1_PHI.setFillColor(34);
        canvas.getCanvas().cd(4); canvas.getCanvas().draw(h1_PHI);
        this.h2_PT_Z = new H2F("PHI","pt vs z",10,
                pars.getParameter("pt").getMin(),pars.getParameter("pt").getMax(),
                10,pars.getParameter("z").getMin(),pars.getParameter("z").getMax());
        canvas.getCanvas().cd(5); canvas.getCanvas().draw(h2_PT_Z);
        canvas.getCanvas().setAxisTitleSize(24);
        canvas.getCanvas().update();
        
    }
    
    private void runSimulation(){
        
        System.out.println("Running....");
        
        this.initHistograms();
        PhaseSpace space = new PhaseSpace();
        Parameters pars  = this.editorPhaseSpace.getParameters();
        for(Map.Entry<String,Parameter> entry : pars.getParameters().entrySet()){
            space.add(entry.getValue().getName(), entry.getValue().getValue(), 
                    entry.getValue().getMin(), entry.getValue().getMax());
        }
        
        this.cross.setPhaseSpace(space);
        
        
        Parameters obsPars = this.editorParams.getParameters();
        
        System.out.println(" FROM EDITOR ");
        System.out.println(obsPars.toString());
        cross.getObservables().setParameters(obsPars);
                
        
        MCFoam foam = new MCFoam(this.cross);
        foam.init();
        double[] unitValues = new double[space.getKeys().size()];
        double[] physValues = new double[space.getKeys().size()];
        for(int i = 0; i < 5000; i++){
                        
            foam.getRandom(unitValues);
            space.setUnit(unitValues);
            space.getValues(physValues);
            h1_Q2.fill(space.getDimension("q2").getValue());
            h1_XB.fill(space.getDimension("xb").getValue());
            h1_PT.fill(space.getDimension("pt").getValue());
            h1_Z.fill(space.getDimension("z").getValue());
            h1_PHI.fill(space.getDimension("phi").getValue());
            h2_PT_Z.fill(space.getDimension("pt").getValue(),space.getDimension("z").getValue());
            
            //System.out.println(ArrayUtils.getString(physValues, "%9.5f", " "));
        }
        System.out.println(" done......");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Run")==0){
           runSimulation(); 
        }
    }
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SIDISStudio studio = new SIDISStudio();
        frame.add(studio);
        frame.pack();
        frame.setVisible(true);
    }

   
}
