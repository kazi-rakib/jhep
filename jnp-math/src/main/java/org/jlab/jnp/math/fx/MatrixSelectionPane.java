/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.fx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.jlab.jnp.math.matrix.MatrixSelection;

/**
 *
 * @author gavalian
 */
public class MatrixSelectionPane {
    
    private GridPane grid = new GridPane();
    private MatrixSelection selector = null;
    private int spinnorWidth = 120;
    
    
    
    public MatrixSelectionPane(){
        initUI();
    }
    
    public MatrixSelectionPane(MatrixSelection s){
        selector = s;
        initUI();
    }
    
    
    public void setSelector(MatrixSelection s){
        selector = s;
        this.initUI();
    }
    
    private void initUI(){
        
        grid.getChildren().clear();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        
        if(selector!=null){
            int nrows = selector.getSize();
            for(int i = 0; i < nrows; i++){
                Integer order = i;
                Text label = new Text(order.toString());
                label.minWidth(120);
                grid.add(label, 0,i);
                MatrixSelection.DimensionSelector s = selector.getSelector(i);
                SpinnerValueFactory<Integer> valueFactoryMin = //
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(s.getMin(), s.getMax(), s.getMin());
                
                Spinner<Integer> spinnerMin = new Spinner<Integer>();   
                spinnerMin.setPrefWidth(this.spinnorWidth);
                spinnerMin.setEditable(true);
                spinnerMin.setValueFactory(valueFactoryMin);
                
                SpinnerValueFactory<Integer> valueFactoryMax = //
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(s.getMin(),s.getMax(),s.getMax());
                
                Spinner<Integer> spinnerMax = new Spinner<Integer>(); 
                spinnerMax.setPrefWidth(this.spinnorWidth);
                spinnerMax.setValueFactory(valueFactoryMax);
                
                
                spinnerMin.valueProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> ov, Integer t, Integer t1) {
                        s.setRange(t1, s.getBinMax());
                        System.out.println(selector.toString());
                        //System.out.println(selector.toString());
                        //System.out.println(" T = " + t + " t1 = " + t1);
                    }
                });
                
                spinnerMax.valueProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> ov, Integer t, Integer t1) {
                        s.setRange(s.getBinMin(),t1);
                        System.out.println(selector.toString());
                        //System.out.println(" T = " + t + " t1 = " + t1);
                    }
                });
                
                grid.add(spinnerMin,1, i);
                grid.add(spinnerMax,2, i); 
            }
            
            //Separator separator = new Separator();
            //grid.add(separator, 0,nrows,2,nrows);
            
            Text labelBin = new Text("Selected Bin:");
            grid.add(labelBin, 1, nrows+1);
            SpinnerValueFactory<Integer> valueFactoryBin = //
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(0,nrows-1,0);
            Spinner<Integer> spinnerBin = new Spinner<Integer>(); 
            spinnerBin.setPrefWidth(this.spinnorWidth);
            spinnerBin.setValueFactory(valueFactoryBin);
                
                
            spinnerBin.valueProperty().addListener(new ChangeListener<Integer>() {
                @Override
                public void changed(ObservableValue<? extends Integer> ov, Integer t, Integer t1) {
                    selector.setBin(t1);
                    //System.out.println(selector.toString());
                    //System.out.println(" T = " + t + " t1 = " + t1);
                }
            });
            
            grid.add(spinnerBin, 2, nrows+1);
            Text labelColumns = new Text("Columns :");
            
            ComboBox combo = new ComboBox();
            for(String list : selector.getColumns()){
                combo.getItems().add(list);
            }
            combo.getSelectionModel().select(0);
            combo.valueProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue ov, String t, String t1) {
                    System.out.println(ov);
                    System.out.println(t);
                    System.out.println(t1);
                    selector.setColumn(t1);
                    System.out.println("-------->>>> change for column : " + selector.getColumn());
                }    
            });
            combo.setPrefWidth(this.spinnorWidth);
            grid.add(labelColumns, 1, nrows+2);
            grid.add(combo, 2, nrows+2);
            
            Button buttonReset = new Button("Reset");
            buttonReset.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    selector.reset();
                }
            });
            grid.add(buttonReset, 2, nrows+3);
        }
    }
    
    public void reset(){
        
    }
    
    public GridPane getPane(){ return grid;}
    public MatrixSelection getSelector(){return this.selector;}
}
