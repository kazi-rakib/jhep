/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;

/**
 *
 * @author gavalian
 */
public class SIDISProcessorStudio extends JPanel implements ActionListener {
    private SIDISProcessor        processor = null;
    private EmbeddedCanvasTabbed  tabCanvas = null;
    
    private JSplitPane splitPane = null;
    
    public SIDISProcessorStudio(){
        super();
        setLayout(new BorderLayout());        
        initUI();
        initEnvironment();
    }
    
    private void initEnvironment(){
        
    }
    
    private void initUI(){
        splitPane = new JSplitPane();
        tabCanvas = new EmbeddedCanvasTabbed();
        splitPane.setRightComponent(tabCanvas);
        
        this.add(splitPane,BorderLayout.CENTER);
    }
    
    
    private void initMenu(){
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[action] ---> " + e.getActionCommand());
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        SIDISProcessorStudio studio = new SIDISProcessorStudio();
        frame.add(studio);
        frame.setVisible(true);        
    }

  
}
