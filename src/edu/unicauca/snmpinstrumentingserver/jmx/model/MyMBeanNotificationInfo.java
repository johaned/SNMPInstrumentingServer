package edu.unicauca.snmpinstrumentingserver.jmx.model;

import javax.management.MBeanNotificationInfo;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;

public class MyMBeanNotificationInfo {
	
	@Attribute
	private String name;
	@Attribute
	private String description;
	@ElementArray
	private String[] notifTypes;
	
	public MyMBeanNotificationInfo() {}

	public MyMBeanNotificationInfo(String[] notifTypes, String name, String description) 
	{
		this.setNotifTypes(notifTypes);
		this.setName(name);
		this.setDescription(description);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getNotifTypes() {
		return notifTypes;
	}

	public void setNotifTypes(String[] notifTypes) {
		this.notifTypes = notifTypes;
	}
	
	public MBeanNotificationInfo getMBeanNotificationInfo(){
		MBeanNotificationInfo mbni = new MBeanNotificationInfo(getNotifTypes(), getName(), getDescription());
		return mbni;
	}

}
