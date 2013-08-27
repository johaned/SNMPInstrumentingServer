package edu.unicauca.snmpinstrumentingserver.jmx.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

@Root
public class MyManRes {
	@Attribute
	private String name;
	@Attribute
	private String domain;
	@Attribute(required=false)
	private String description;
	@Attribute	
	private String referenceProtocol;
	@ElementArray
	private MyMBeanInfo[] macroAttributes;
	
	public MyManRes(){}
	
	public MyManRes(String name, String domain, String description, String referenceProtocol, MyMBeanInfo[] macroAttributes) throws IllegalArgumentException 
	{
		this.name=name;
		this.domain=domain;
		this.description=description;
		this.referenceProtocol=referenceProtocol;
		this.macroAttributes=macroAttributes;
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

	public String getReferenceProtocol() {
		return referenceProtocol;
	}

	public void setReferenceProtocol(String referenceProtocol) {
		this.referenceProtocol = referenceProtocol;
	}

	public MyMBeanInfo[] getMacroAttributes() {
		return macroAttributes;
	}

	public void setMacroAttributes(MyMBeanInfo[] macroAttributes) {
		this.macroAttributes = macroAttributes;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
