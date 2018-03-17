/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.abs;

import java.util.List;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import org.jlab.jnp.hipo.base.DataMap;
import org.jlab.jnp.hipo.base.DataMapReducer;

/**
 *
 * @author gavalian
 */
public class AbsDataMapReducer implements DataMapReducer {

    Expression mapExpression = null;
        static Operator operatorGT = new Operator(">", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
            @Override
            public double apply(final double... args) {
                if(args[0]>args[1]) return 1.0;
                return 0.0;
            }
    };
    
    static Operator operatorLT = new Operator("<", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
        @Override
        public double apply(final double... args) {
            if(args[0]<args[1]) return 1.0;
            return 0.0;
        }
    };

    static Operator operatorEQ = new Operator("==", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
        @Override
        public double apply(final double... args) {
            if(args[0]==args[1]) return 1.0;
            return 0.0;
        }
    };

    static Operator operatorAND = new Operator("&&", 2, true, Operator.PRECEDENCE_ADDITION) {
        @Override
        public double apply(final double... args) {
            if(args[0]>0.0&&args[1]>0.0) return 1.0;
            return 0.0;
        }
    };
    
    static Operator operatorOR = new Operator("||", 2, true, Operator.PRECEDENCE_ADDITION) {
        @Override
        public double apply(final double... args) {
            if(args[0]>0.0||args[1]>0.0) return 1.0;
            return 0.0;
        }
    };    
    
    public AbsDataMapReducer(String expression, String[] variables){
        ExpressionBuilder builder = new ExpressionBuilder(expression);
        builder.operator(operatorAND)
                .operator(operatorOR)
                .operator(operatorGT)
                .operator(operatorLT)
                .operator(operatorEQ);
        builder.variables(variables);
        mapExpression = builder.build();
    }
    
    @Override
    public boolean reduce(DataMap map) {
        if(map.getStatus()==false) return false;
        List<String> keys = map.getKeys();
        //System.out.println(" size = " + keys.size());
        for(int i = 0; i < keys.size(); i++){
            String name = keys.get(i);
            //System.out.println(" name = " + name + " value = " + map.getValue(name));
            mapExpression.setVariable(name, map.getValue(name));
        }
        double result = mapExpression.evaluate();
        if(result<0.5) return false;
        //System.out.println(" result = " + result);
        return true;
    }
    
    
    
}
