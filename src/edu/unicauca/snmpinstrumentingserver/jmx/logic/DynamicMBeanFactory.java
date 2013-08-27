package edu.unicauca.snmpinstrumentingserver.jmx.logic;

import edu.unicauca.snmpinstrumentingserver.jmx.model.*;
import edu.unicauca.snmpinstrumentingserver.model.Layout;
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


public class DynamicMBeanFactory {

    public static MBeanServer mbeanServer;
    public static String filename = "RWebservices";
    public static String filenameM = "exampleMonitors";
    private static MonitorListener listener = new MonitorListener();
    private static MessageListener attlist = new MessageListener();
    private static List<Monitor> monitors = new ArrayList<Monitor>();

    public DynamicMBeanFactory() {
        super();
    }

    public static List<MyDynamicMBean> mbeans_register(String path, String filename) {
        List<MyDynamicMBean> mdmbs = new ArrayList<MyDynamicMBean>();
        filename += ".xml";
        try {
            Serializer serializer = new Persister();
            File source = new File(path + "/" + filename);
            MyManRes mmr = serializer.read(MyManRes.class, source);
            int index=0;
            for (MyMBeanInfo mmbi : mmr.getMacroAttributes()) {
                String tempDirMBean = mmr.getName()+System.currentTimeMillis();
                System.out.println("--> mmbi: "+ mmbi.getName());
                System.out.println("--> [ domain: "+mmr.getDomain() +" name: "+mmbi.getName()+" type: "+mmr.getName()+ " nameFile: "+mmr.getName()+" ]");
                mdmbs.add(getDynamicBean(mmr.getDomain(), mmbi.getName(), mmr.getName(), path, mmr.getName(),index));
                System.out.println("--> |Registered MBean: "+mdmbs.get(mdmbs.size()-1).getName()+"| ");
                index++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mdmbs;
    }

    public static MyDynamicMBean getDynamicBean(String domain, String name, String type, String pathXml, String namefile, int index) {
        //Register a MBean
        String filenameU = null;
        if (namefile == null) {
            filenameU = filename;
        } else {
            filenameU = namefile;
        }

        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        MyDynamicMBean dynamicMBean = null;

        try {
            dynamicMBean = new MyDynamicMBean(pathXml + "/" + filenameU + ".xml", index);
            dynamicMBean.setDomain(domain);
            dynamicMBean.setName(name);
            dynamicMBean.setType(type);
            mbeanServer.registerMBean(dynamicMBean, new ObjectName(domain + ":type=" + type + ",name=" + name));
            mbeanServer.addNotificationListener(new ObjectName(domain + ":type=" + type + ",name=" + name), attlist, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Monitors ms = loadMonitor(pathXml, filenameU);
        if (ms != null) {
            for (MyMonitor mm : ms.getMonitors()) {
                Monitor m = mm.getMonitor();
                String oname = mm.getName() + "_" + type + "_" + m.getObservedAttribute();
                try {
                    m.addObservedObject(new ObjectName(domain + ":type=" + type + ",name=" + name));
                    mbeanServer.registerMBean(m, new ObjectName(oname));
                } catch (InstanceAlreadyExistsException e) {
                    e.printStackTrace();
                } catch (MBeanRegistrationException e) {
                    e.printStackTrace();
                } catch (NotCompliantMBeanException e) {
                    e.printStackTrace();
                } catch (MalformedObjectNameException e) {
                    e.printStackTrace();
                }
                try {
                    m.addNotificationListener(listener, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                monitors.add(m);
                m.start();
            }
        }

        return dynamicMBean;
    }

    public static String removeDynamicBean(String domain, String name, String type) {
        //Unregister MBean
        String retorno = "";
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            Set<?> dynamicData;
            dynamicData = mbeanServer.queryMBeans(new ObjectName(domain + ":type=" + type + ",name=" + name), null);
            for (Iterator<?> it = dynamicData.iterator(); it.hasNext();) {
                ObjectInstance oi = (ObjectInstance) it.next();
                ObjectName oName = oi.getObjectName();
                mbeanServer.unregisterMBean(oName);
            }
            if (dynamicData.size() > 0) {
                retorno = "OK";
            } else {
                retorno = "Not Found";
            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        }
        //Unregister CounterMonitor MBean associated
        Set<?> dynamicData;
        try {
            dynamicData = mbeanServer.queryMBeans(new ObjectName("Services:*"), null);
            for (Iterator<?> it = dynamicData.iterator(); it.hasNext();) {
                ObjectInstance oi = (ObjectInstance) it.next();
                ObjectName oName = oi.getObjectName();
                System.out.println("name=" + oName.toString());
                if (oName.toString().contains("_" + name + "_")) {
                    Monitor m = null;
                    for (int i = 0; i < monitors.size(); i++) {
                        if (((Monitor) monitors.get(i)).containsObservedObject(new ObjectName(domain + ":type=" + type + ",name=" + name))) {
                            m = (Monitor) monitors.get(i);
                            break;
                        }
                    }
                    if (m != null) {
                        m.stop();
                        try {
                            mbeanServer.unregisterMBean(oName);
                        } catch (MBeanRegistrationException e) {
                            e.printStackTrace();
                        } catch (InstanceNotFoundException e) {
                            e.printStackTrace();
                        }
                        monitors.remove(m);
                    }
                }
            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    public static String setAttribute(String domain, String name, String type, String attribute, String value) {
        String retorno = "OK";
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        Attribute attr = new Attribute(attribute, value);
        try {
            mbeanServer.setAttribute(new ObjectName(domain + ":type=" + type + ",name=" + name), attr);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            retorno = e.toString();
        } catch (InvalidAttributeValueException e) {
            e.printStackTrace();
            retorno = e.toString();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
            retorno = e.toString();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            retorno = e.toString();
        } catch (ReflectionException e) {
            e.printStackTrace();
            retorno = e.toString();
        } catch (MBeanException e) {
            e.printStackTrace();
            retorno = e.toString();
        }
        return retorno;
    }

    public static String setAttributes(String domain, String name, String type, HashMap<String, String> attributes) {

        String retorno = "OK";
        mbeanServer = ManagementFactory.getPlatformMBeanServer();

        AttributeList listattr = new AttributeList();
        for (Entry<String, String> attribute : attributes.entrySet()) {
            listattr.add(new Attribute(attribute.getKey(), attribute.getValue()));
        }
        try {
            mbeanServer.setAttributes(new ObjectName(domain + ":type=" + type + ",name=" + name), listattr);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            retorno = e.toString();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            retorno = e.toString();
        } catch (ReflectionException e) {
            e.printStackTrace();
            retorno = e.toString();
        }
        return retorno;
    }

    public static Monitors loadMonitor(String xmlPath, String xmlFileName) {
        Serializer serializer = new Persister();
        File source = new File(xmlPath + "/" + xmlFileName + "Monitors.xml");
        Monitors mm = null;
        try {
            mm = serializer.read(Monitors.class, source);
        } catch (FileNotFoundException e) {
            System.out.println("El MBean no tiene configuración de monitores.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mm;
    }
}