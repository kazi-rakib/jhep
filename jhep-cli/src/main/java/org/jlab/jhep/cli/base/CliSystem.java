/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.cli.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author gavalian
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CliSystem {
    String system();
    String info();
}
