/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unicauca.snmpinstrumentingserver.logic;

import java.net.UnknownHostException;

/**
 *
 * @author stcav
 */
public class Configurator {
    private static Configurator instance;
    private boolean isConfigured;

    public Configurator() {
        isConfigured=false;
    }
    
    public static Configurator getInstance() throws UnknownHostException{
		if(instance==null){
			instance = new Configurator();
		}
		return instance;
	}
    public void to_configure_JMXParams(String hostname, String port){
        System.setProperty("com.sun.management.jmxremote", "true");
        System.setProperty("com.sun.management.jmxremote.port", port);
        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
        System.setProperty("com.sun.management.jmxremote.ssl", "false");
        System.setProperty("visualvm.display.name", hostname);
        isConfigured=true;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    public void setIsConfigured(boolean isConfigured) {
        this.isConfigured = isConfigured;
    }
    
    
}
