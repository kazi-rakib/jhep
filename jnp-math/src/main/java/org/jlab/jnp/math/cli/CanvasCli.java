/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.cli;

import org.jlab.groot.studio.DataStudio;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.cli.base.CliCommand;
import org.jlab.jnp.cli.base.CliSystem;

/**
 *
 * @author gavalian
 */
@CliSystem(system="canvas",info="canvas utilities for GROOT")
public class CanvasCli {
    @CliCommand(command="clear", info="clear canvas",
            defaults={},
            descriptions={})
    public void clear(){
        if(DataStudio.getInstance().getCanvasStore().isEmpty()==true){
            TCanvas canvas = new TCanvas("c1",500,500);
            DataStudio.getInstance().getCanvasStore().put("1", canvas);
        }
        DataStudio.getInstance().getCanvasStore().get("1").getCanvas().clear();
    }
    @CliCommand(command="zone", info="divide canvas into zones",
            defaults={"1","1"},
            descriptions={"rows","columns"})
    public void zone(int xz, int yz){
        if(DataStudio.getInstance().getCanvasStore().isEmpty()==true){
            TCanvas canvas = new TCanvas("c1",500,500);
            DataStudio.getInstance().getCanvasStore().put("1", canvas);
        }
        DataStudio.getInstance().getCanvasStore().get("1").getCanvas().divide(xz, yz);
    }
}
