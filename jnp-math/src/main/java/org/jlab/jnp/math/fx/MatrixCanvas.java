/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.fx;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.jlab.jnp.math.data.DataVector;

/**
 *
 * @author gavalian
 */
public class MatrixCanvas {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    final AreaChart<Number,Number> ac = 
            new AreaChart<Number,Number>(xAxis,yAxis);
    
    public MatrixCanvas(){
        
    }
    
    
    public AreaChart  getChart(){
        return ac;
    }
    
    public void setData(DataVector x, DataVector y){
        ac.getData().clear();
        XYChart.Series seriesGraph = new XYChart.Series();
        seriesGraph.setName("X-CHART");
        int nrows = x.getSize();
        for(int i = 0; i < nrows; i++){
            seriesGraph.getData().add(new XYChart.Data(x.valueOf(i),y.valueOf(i)));
        }
        ac.getData().add(seriesGraph);
    }
}
