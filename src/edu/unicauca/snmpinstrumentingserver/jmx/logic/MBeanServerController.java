package edu.unicauca.snmpinstrumentingserver.jmx.logic;

import edu.unicauca.snmpinstrumentingserver.jmx.model.MyDynamicMBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.management.*;


public class MBeanServerController {

    public static MBeanServer mbeanServer;

    public MBeanServerController() {
        super();
    }

    public static String changeAttribute(String domain, String type, String name, String attribute, String value) {
        String retorno = "OK";
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        Attribute attr = new Attribute(attribute, value);
        try {
            mbeanServer.setAttribute(new ObjectName(domain + ":type=" + type +",name="+name), attr);
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

    public static String changeAttributes(String domain, String type,String name, HashMap<String, String> attributes) {

        String retorno = "OK";
        mbeanServer = ManagementFactory.getPlatformMBeanServer();

        AttributeList listattr = new AttributeList();
        for (Entry<String, String> attribute : attributes.entrySet()) {
            listattr.add(new Attribute(attribute.getKey(), attribute.getValue()));
        }
        try {
            mbeanServer.setAttributes(new ObjectName(domain + ":type=" + type +",name="+name), listattr);
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
    
    public static Object getAttribute(String domain, String type, String name, String attr) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, MalformedObjectNameException{
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        return mbeanServer.getAttribute(new ObjectName(domain + ":type=" + type +",name="+name), attr);
    }
    
    public static AttributeList getAttributes(String domain, String type, String name, String[] attr) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, MalformedObjectNameException{
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        return mbeanServer.getAttributes(new ObjectName(domain + ":type=" + type +",name="+name), attr);
    }
    
     public static void sendNotification(MyDynamicMBean mdmb, String type, String message) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, MalformedObjectNameException, IntrospectionException{
        mdmb.sendNotification(type, message);
    }

    
}