package edu.unicauca.snmpinstrumentingserver.snmp.logic;

import edu.unicauca.snmpinstrumentingserver.jmx.logic.MessageListener;
import edu.unicauca.snmpinstrumentingserver.jmx.logic.MonitorListener;
import edu.unicauca.snmpinstrumentingserver.jmx.model.*;
import edu.unicauca.snmpinstrumentingserver.model.Layout;
import edu.unicauca.snmpinstrumentingserver.snmp.model.SnmpResource;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import javax.management.monitor.Monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


public class DynamicSnmpResourceFactory {

    public static MBeanServer mbeanServer;
    public static String filename = "RWebservices";
    public static String filenameM = "exampleMonitors";
    private static MonitorListener listener = new MonitorListener();
    private static MessageListener attlist = new MessageListener();
    private static List<Monitor> monitors = new ArrayList<Monitor>();

    public DynamicSnmpResourceFactory() {
        super();
    }

    public static List<SnmpResource> snmp_resource_register(String path, String filename) {
        List<SnmpResource> srs = new ArrayList<SnmpResource>();
        filename += ".xml";
        try {
            Serializer serializer = new Persister();
            File source = new File(path + "/" + filename);
            MyManRes mmr = serializer.read(MyManRes.class, source);
            int index=0;
            for (MyMBeanInfo mmbi : mmr.getMacroAttributes()) {
                System.out.println("--> mmbi: "+ mmbi.getName());
                System.out.println("--> [ domain: "+mmr.getDomain() +" name: "+mmbi.getName()+" type: "+mmr.getName()+ " nameFile: "+mmr.getName()+" ]");
                srs.add(new SnmpResource(mmbi.getAttributes(), mmr.getReferenceProtocol(), Layout.QUERYINGINTERVAL, mmr.getName(), mmbi.getName()));
                System.out.println("--> |Added SNMP Resource: "+srs.get(srs.size()-1).getName()+"| ");
                index++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return srs;
    }
}