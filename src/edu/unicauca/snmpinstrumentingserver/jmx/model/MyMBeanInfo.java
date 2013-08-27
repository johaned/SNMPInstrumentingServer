package edu.unicauca.snmpinstrumentingserver.jmx.model;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;

public class MyMBeanInfo {

	@Attribute
	private String className;
	@Attribute
	private String name;
	@Attribute
	private String description;
	@Attribute
	private String referenceProtocol;
	@Attribute
	private String type;
	@ElementArray
	private MyMBeanAttributeInfo[] attributes;
	@ElementArray(required=false)
	private MyMBeanNotificationInfo[] notifications;
	
	
	public MyMBeanInfo(){}
	
	public MyMBeanInfo(String className, String description, MBeanAttributeInfo[] attributes,
			MBeanConstructorInfo[] constructors, MBeanOperationInfo[] operations,
			MBeanNotificationInfo[] notifications)	throws IllegalArgumentException 
	{
		this.className=className;
		this.description=description;
		if(notifications!=null){
			this.notifications = new MyMBeanNotificationInfo[notifications.length];
			for (int i=0;i<notifications.length;i++) {
				this.notifications[i]=new MyMBeanNotificationInfo(notifications[i].getNotifTypes(),notifications[i].getName(), notifications[i].getDescription());
			}
		}else
			this.notifications=null;
		this.attributes = new MyMBeanAttributeInfo[attributes.length];
		for (int i=0;i<attributes.length;i++) {
			this.attributes[i]=new MyMBeanAttributeInfo(attributes[i].getName(), attributes[i].getType(), attributes[i].getDescription(), attributes[i].isReadable(), attributes[i].isWritable(), attributes[i].isIs());
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MyMBeanAttributeInfo[] getAttributes() {
		return attributes;
	}

	public void setAttributes(MyMBeanAttributeInfo[] attributes) {
		this.attributes = attributes;
	}

	public MyMBeanNotificationInfo[] getNotifications() {
		return notifications;
	}

	public void setNotifications(MyMBeanNotificationInfo[] notifications) {
		this.notifications = notifications;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReferenceProtocol() {
		return referenceProtocol;
	}

	public void setReferenceProtocol(String referenceProtocol) {
		this.referenceProtocol = referenceProtocol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
