/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unicauca.snmpinstrumentingserver.main;

import edu.unicauca.snmpinstrumentingserver.jmx.logic.DynamicMBeanFactory;
import edu.unicauca.snmpinstrumentingserver.jmx.model.MyDynamicMBean;
import edu.unicauca.snmpinstrumentingserver.logic.Configurator;
import edu.unicauca.snmpinstrumentingserver.model.Layout;
import edu.unicauca.snmpinstrumentingserver.snmp.logic.DynamicSnmpResourceFactory;
import edu.unicauca.snmpinstrumentingserver.snmp.model.SnmpResource;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 *
 * @author stcav
 */
public class SNMPInstrumentingServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<MyDynamicMBean> mdmbs;
        List<SnmpResource> srs;
        JMXConnectorServer cs = null;
       
        to_configure_JMXAgent();
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            LocateRegistry.createRegistry(Integer.parseInt(Layout.PORT));
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + InetAddress.getLocalHost().getHostName() + ":"+Layout.PORT+"/jmxrmi");
            cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
            cs.start();
            System.out.println("Escuchando en " + cs.getAddress());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        mdmbs = DynamicMBeanFactory.mbeans_register(Layout.PATHMBEANDESCRIPTOR, Layout.MEDIASERVER);
        srs = DynamicSnmpResourceFactory.snmp_resource_register(Layout.PATHMBEANDESCRIPTOR, Layout.MEDIASERVER);
        
        for (SnmpResource snmpResource : srs) {
            snmpResource.startManaging();
            snmpResource.run();    
        }
        
        
    }
    
    
    private static void to_configure_JMXAgent() {
        try {
            Configurator c = Configurator.getInstance();
            if (!c.isConfigured()) {
                c.to_configure_JMXParams(Layout.HOSTNAME, Layout.PORT);
                System.out.println("El sistema esta configurado - host: " + System.getProperty("visualvm.display.name") + " port: " + System.getProperty("com.sun.management.jmxremote.port"));
            } else {
                System.out.println("El sistema esta configurado - host: " + System.getProperty("visualvm.display.name") + " port: " + System.getProperty("com.sun.management.jmxremote.port"));
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }
}
