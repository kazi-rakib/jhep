/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.fx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jlab.jnp.math.data.DataAxis;
import org.jlab.jnp.math.data.DataVector;
import org.jlab.jnp.math.matrix.MatrixSelection;
import org.jlab.jnp.math.matrix.SparseMatrix;

/**
 *
 * @author gavalian
 */
public class MatrixApplication extends Application {
    
    SparseMatrix matrix = null;
    MatrixSelectionPane pane = null;
    MatrixCanvas        canvas = null;
    SplitPane splitPane = new SplitPane();
    
    @Override
    public void start(Stage primaryStage) {
        
        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);
        
        
        this.initMatrix();
        
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(newMenuItem, saveMenuItem,
        new SeparatorMenuItem(), exitMenuItem);
        menuBar.getMenus().addAll(fileMenu);
        newMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    drawMatrix();
                }
            });
        primaryStage.setTitle("Matrix");
        
        //MatrixSelection selection = new MatrixSelection(new int[]{5,7,12,34});
        //selection.setColumns(Arrays.asList(new String[]{"cross","acceptance","error"}));
        
        
        pane = new MatrixSelectionPane(matrix.getSelector());
        
        Accordion accordion = new Accordion (); 
        TitledPane titled = new TitledPane("Selector",pane.getPane());
        
        canvas = new MatrixCanvas();
        
        accordion.getPanes().add(titled);
        splitPane.setDividerPositions(0.75);
        splitPane.getItems().addAll(canvas.getChart(),accordion);
        root.setCenter(splitPane);
        //StackPane root = new StackPane();
        //root.getChildren().add(splitPane);
        primaryStage.setScene(new Scene(root, 900, 550));
        primaryStage.show();
        
        this.initMatrix();
    }
    
    public void drawMatrix(){
        System.out.println("------------> drawing");
        DataVector vecX = matrix.getAxisVector(0);
        DataVector vecY = matrix.getProjection(this.pane.getSelector());
        this.canvas.setData(vecX, vecY);
    }
    
    public void initMatrix(){
        
        matrix = new SparseMatrix(new String[]{"cross","acceptance","error"});
        DataAxis  axisX = new DataAxis("a",25,0.0,1.0);
        DataAxis  axisY = new DataAxis("b",35,0.0,1.0);
        matrix.initAxis(axisX,axisY);
        /*for(int i = 0; i < 10; i++){
            DataVector<Float>  vec = new DataVector<Float>(new Float[]{ (float) 0.1, (float) 0.4});
            matrix.addData(vec, i+1,1);
        }*/
        
        Map<String,Double>  items = new HashMap<String,Double>();
        for(int i = 0; i < 32500; i++){
            double a = Math.random();
            double b = Math.random();
            double w = Math.random();
            
            items.clear();
            items.put("a", a); items.put("b", b);
            matrix.fill(0, items, 1.0);
            matrix.fill(1, items, w);
        }
        matrix.show();
        matrix.printContent();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    
}
