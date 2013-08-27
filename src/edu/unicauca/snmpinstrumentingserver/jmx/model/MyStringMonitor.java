package edu.unicauca.snmpinstrumentingserver.jmx.model;

import javax.management.monitor.Monitor;
import javax.management.monitor.StringMonitor;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MyStringMonitor implements MyMonitor{


	@Element
	private String attribute;
	@Element
	private Long period;
	@Element
	private String compareValue;
	@Attribute
	private String name;
	
	public MyStringMonitor() {

	}
	
	public String getAttribute() {
		return attribute;
	}

	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	
	public StringMonitor getStringMonitor(){
		StringMonitor sm = new StringMonitor();
		sm.setGranularityPeriod(getPeriod());
		sm.setNotifyDiffer(true);
		sm.setNotifyMatch(true);
		sm.setObservedAttribute(getAttribute());
		sm.setStringToCompare(getCompareValue());
		return sm;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(String compareValue) {
		this.compareValue = compareValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Monitor getMonitor() {
		return getStringMonitor();
	}
}
