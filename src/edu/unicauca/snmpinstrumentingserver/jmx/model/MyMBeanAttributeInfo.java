package edu.unicauca.snmpinstrumentingserver.jmx.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class MyMBeanAttributeInfo{
	
	@Attribute
	private String name;
	@Attribute
	private String type;
	@Attribute
	private String description;
	@Attribute(required=false)
	private String referenceProtocol;
	@Attribute
	private boolean isReadable;
	@Attribute
	private boolean isWritable;
	@Attribute
	private boolean isIs;
	@Element(required=false)
	private String value;
	
	public MyMBeanAttributeInfo() 
	{
	}

	public MyMBeanAttributeInfo(String name, String type, String description, boolean isReadable, boolean isWritable, boolean isIs) 
	{
		this.name=name;
		this.type=type;
		this.description=description;
		this.isReadable=isReadable;
		this.isWritable=isWritable;
		this.isIs=isIs;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public boolean isReadable() {
		return isReadable;
	}


	public void setReadable(boolean isReadable) {
		this.isReadable = isReadable;
	}


	public boolean isWritable() {
		return isWritable;
	}


	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}


	public boolean isIs() {
		return isIs;
	}


	public void setIs(boolean isIs) {
		this.isIs = isIs;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getReferenceProtocol() {
		return referenceProtocol;
	}

	public void setReferenceProtocol(String referenceProtocol) {
		this.referenceProtocol = referenceProtocol;
	}
}
