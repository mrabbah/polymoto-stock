/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.choranet.gesticom.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author RABBAH
 */
public class BackupDirectoryFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        return (name.startsWith("BACKUP") &&(new File(dir, name)).isDirectory());
    }
    
}
