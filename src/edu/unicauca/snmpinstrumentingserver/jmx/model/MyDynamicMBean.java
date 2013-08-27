package edu.unicauca.snmpinstrumentingserver.jmx.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class MyDynamicMBean implements DynamicMBean, NotificationEmitter{

	private final Properties properties;
	private MyMBeanInfo mbeaninfo;
	private String xmlFileName; 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<ListenerFilterHandbackTriplet> _listeners = new ArrayList();
	private String domain;
	private String name;
	private String type;
	private int changeCount=1;
        private int index;

	public MyDynamicMBean(String xmlFileName, int index) throws IOException {
		this.properties = new Properties();
		this.xmlFileName = xmlFileName;
                this.index=index;
		loadXml();
	}


	public synchronized Object getAttribute(String name) throws AttributeNotFoundException {
		String value = this.properties.getProperty(name);
		String valueType="";
		if (value != null) {
			MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[properties.size()];
			for (int i = 0; i < attrs.length; i++) {
				MyMBeanAttributeInfo info=mbeaninfo.getAttributes()[i];
				if(info.getName().equals(name)){
					valueType=info.getType();
					break;
				}
			}
			if (valueType.equals("java.lang.Integer"))
				return Integer.parseInt(value);
			else if (valueType.equals("java.lang.Long"))
				return Long.parseLong(value);
			else if (valueType.equals("java.lang.Float"))
				return Float.parseFloat(value);
			else if (valueType.equals("java.lang.Double"))
				return Double.parseDouble(value);
			else if (valueType.equals("java.lang.Boolean"))
				return Boolean.parseBoolean(value);
			else
				return value;
		}
		throw new AttributeNotFoundException("No such property: " + name);
	}

	public synchronized void setAttribute(Attribute attribute) throws InvalidAttributeValueException, MBeanException, AttributeNotFoundException {
		String name = attribute.getName();
		if (this.properties.getProperty(name) == null)
			throw new AttributeNotFoundException(name);
		Object value = attribute.getValue();
		this.properties.setProperty(name, changeTypeToString(value));
		Notification notification = new AttributeChangeNotification(this, changeCount++, System.currentTimeMillis(), "Attribute value changed", attribute.getName(), attribute.getValue().getClass().getName(), value, value);
        sendNotification(notification, attribute);
	}

	public synchronized AttributeList getAttributes(String[] names) {
		AttributeList list = new AttributeList();
		for (String name : names) {
			String value = this.properties.getProperty(name);
			if (value != null)
				list.add(new Attribute(name, value));
		}
		return list;
	}

	public synchronized AttributeList setAttributes(AttributeList list) {
		Attribute[] attrs = (Attribute[]) list.toArray(new Attribute[0]);
		AttributeList retlist = new AttributeList();
		for (Attribute attr : attrs) {
			String name = attr.getName();
			Object value = attr.getValue();
			this.properties.setProperty(name, changeTypeToString(value));
			retlist.add(new Attribute(name, value));
			
			Notification notification = new AttributeChangeNotification(this, changeCount++, System.currentTimeMillis(), "Attribute value changed", attr.getName(), attr.getValue().getClass().getName(), value, value);
	        sendNotification(notification, attr);
		}
		
		return retlist;
	}
	
	public Object invoke(String name, Object[] args, String[] sig) throws MBeanException, ReflectionException {
		if ((name.equals("reload")) && ((args == null) || (args.length == 0)) && ((sig == null) || (sig.length == 0))) {
			System.out.println("invocado reload");
			try {
				loadXml();
				return null;
			} catch (IOException e) {
				throw new MBeanException(e);
			}
		}
		throw new ReflectionException(new NoSuchMethodException(name));
	}

	public synchronized MBeanInfo getMBeanInfo() {
		SortedSet<String> names = new TreeSet<String>();
		for (Iterator<?> localIterator1 = this.properties.keySet().iterator(); localIterator1.hasNext();) {
			Object name = localIterator1.next();
			names.add((String) name);
		}

		MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[names.size()];
		for (int i = 0; i < attrs.length; i++) {
			MyMBeanAttributeInfo info=mbeaninfo.getAttributes()[i];
			attrs[i] = new MBeanAttributeInfo(info.getName(), info.getType(), info.getDescription(), info.isReadable(), info.isWritable(), info.isIs());
		}
		
		MBeanNotificationInfo[] nots = getNotificationInfo();
		MBeanOperationInfo[] opers = { new MBeanOperationInfo("reload", "Reload properties from file", null, "void", 1) };
		MBeanInfo mbinfo=new MBeanInfo(getClass().getName(), "Property Manager MBean", attrs, null, opers, nots);
		setMbeaninfo(new MyMBeanInfo(mbinfo.getClassName(), mbinfo.getDescription(), attrs, null, opers, nots));
		return mbinfo;
	}

	private void loadXml() throws IOException {
		Serializer serializer = new Persister();
		File source = new File(xmlFileName);
		try {
			MyManRes manres= serializer.read(MyManRes.class, source);
			mbeaninfo = manres.getMacroAttributes()[index];
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int i=0;i<mbeaninfo.getAttributes().length;i++) {
			String ja=mbeaninfo.getAttributes()[i].getValue();
			if(ja==null)
				ja="";
			this.properties.setProperty(mbeaninfo.getAttributes()[i].getName(), ja);
		}

	}

	public MyMBeanInfo getMbeaninfo() {
		return mbeaninfo;
	}

	public void setMbeaninfo(MyMBeanInfo mbeaninfo) {
		this.mbeaninfo = mbeaninfo;
	}

	public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
		if (listener != null)
            	_listeners.add(new ListenerFilterHandbackTriplet(listener, filter, null));
	}

	public void removeNotificationListener(NotificationListener arg0) throws ListenerNotFoundException {
	}
	
	public void removeNotificationListener(NotificationListener listener,NotificationFilter filter, Object handback)throws ListenerNotFoundException {
	} 
	
	private void sendNotification (Notification notification, Attribute attribute) {
        for (int aa = 0; aa < _listeners.size(); aa++) {
            ListenerFilterHandbackTriplet triplet = (ListenerFilterHandbackTriplet)_listeners.get(aa);
            NotificationListener listener = triplet.getListener();
            Properties props = new Properties();
            props.setProperty("attribute", attribute.getName());
            props.setProperty("value", changeTypeToString(attribute.getValue()));
            notification.setUserData(props);
            try {
				listener.handleNotification(notification, new ObjectName(getDomain() + ":type=" + getType()));
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			}
        }
    }
	
	public void sendNotification (String type, String message) {
        Notification notification = new Notification(type, this, 0,System.currentTimeMillis(),message);    
        for (int aa = 0; aa < _listeners.size(); aa++) {
            ListenerFilterHandbackTriplet triplet = (ListenerFilterHandbackTriplet)_listeners.get(aa);
            NotificationListener listener = triplet.getListener();
	        try {
				listener.handleNotification(notification, new ObjectName(getDomain() + ":type=" + getType()));
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			}
        }
    }
    
    private class ListenerFilterHandbackTriplet {
        private NotificationListener _listener;

        NotificationListener getListener () {
            return  _listener;
        }
        private NotificationFilter _filter;

        private Object _handback;

        @SuppressWarnings("unused")
		Object getHandback () {
            return  _handback;
        }

        ListenerFilterHandbackTriplet (NotificationListener listener, NotificationFilter filter, 
                Object handback) {
            _listener = listener;
            _filter = filter;
            _handback = handback;
        }

        public String toString () {
            return  _listener + "/" + _filter + "/" + _handback;
        }
    }

	public String getDomain() {
		return domain;                
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private String changeTypeToString(Object value){
		String value2 = null;
		if (value instanceof Integer)
			value2=Integer.toString((Integer)value);
		else if (value instanceof Long)
			value2=Long.toString((Long)value);
		else if (value instanceof Float)
			value2=Float.toString((Float)value);
		else if (value instanceof Double)
			value2=Double.toString((Double)value);
		else if (value instanceof Boolean)
			value2=Boolean.toString((Boolean)value);
		else
			value2=(String)value;
		return value2;
	}


	public MBeanNotificationInfo[] getNotificationInfo() {
		MBeanNotificationInfo[] nots = null;
		if(mbeaninfo.getNotifications()!=null){
			nots = new MBeanNotificationInfo[mbeaninfo.getNotifications().length];
			for (int i = 0; i < nots.length; i++) {
				MyMBeanNotificationInfo info=mbeaninfo.getNotifications()[i];
				nots[i] = new MBeanNotificationInfo(info.getNotifTypes(), info.getName(), info.getDescription());
			}
		}
		return nots;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}