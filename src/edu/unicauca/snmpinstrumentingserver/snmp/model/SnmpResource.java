/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unicauca.snmpinstrumentingserver.snmp.model;

import com.sun.jmx.remote.security.MBeanServerAccessController;
import edu.unicauca.snmpinstrumentingserver.jmx.logic.MBeanServerController;
import edu.unicauca.snmpinstrumentingserver.jmx.model.MyMBeanAttributeInfo;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanAttributeInfo;
import org.snmp4j.event.ResponseEvent;

/**
 *
 * @author stcav
 */
public class SnmpResource extends SnmpManager implements Runnable {

    private MyMBeanAttributeInfo[] mbais;
    private boolean alive = true;
    private boolean managing = false;
    private int queryInterval;
    private String serverName;
    private String name;
    private ResponseEvent responseEvent;
    private HashMap<String,String> previousValues;

    public SnmpResource() {
    }

    public SnmpResource(MyMBeanAttributeInfo[] mbais, String dirip, int queryInterval, String serverName, String name) {
        this.agentIP = dirip;
        this.mbais = mbais;
        this.queryInterval = queryInterval;
        this.serverName = serverName;
        this.name = name;
        this.previousValues = new HashMap<String, String>();
        // Initalizing all OID values as empty
        for (MyMBeanAttributeInfo mbai : mbais) {
            previousValues.put(mbai.getReferenceProtocol(), "");
        }
    }

    @Override
    public void run() {
        String currentValue;
        String previousValue;
        while (alive) {
            System.out.println("-- " + name + " --");
            while (managing) {
                for (MyMBeanAttributeInfo mbai : mbais) {
                    System.out.println("--> Querying <" + mbai.getName() + "> SNMP Attribute  of <" + name + ">  Resourse of <" + serverName + "> Server");
                    responseEvent =  this.get(mbai.getReferenceProtocol());
                    currentValue = responseEvent.getResponse().getVariableBindings().firstElement().toValueString();
                    previousValue = previousValues.get(mbai.getReferenceProtocol());
                    if (previousValue.equals(currentValue)){
                        System.out.println("-- No Change!");
                    }else{
                        System.out.println("-- Reporting the change!"); 
                        previousValues.put(mbai.getReferenceProtocol(),currentValue);
                        MBeanServerController.changeAttribute("SNMPInstrumentingServer", serverName, name, mbai.getName(), currentValue);
                    }
                }
                System.out.println("--------------------------------------------------------------------------------");
                try {
                    Thread.sleep(queryInterval);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SnmpResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(queryInterval);
            } catch (InterruptedException ex) {
                Logger.getLogger(SnmpResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }

    public boolean isManaging() {
        return managing;
    }

    public void setManaging(boolean managing) {
        this.managing = managing;
    }

    public void stoptManaging() {
        setManaging(false);
    }

    public void startManaging() {
        setManaging(true);
    }

    public MyMBeanAttributeInfo[] getMbais() {
        return mbais;
    }

    public void setMbais(MyMBeanAttributeInfo[] mbais) {
        this.mbais = mbais;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQueryInterval() {
        return queryInterval;
    }

    public void setQueryInterval(int queryInterval) {
        this.queryInterval = queryInterval;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
