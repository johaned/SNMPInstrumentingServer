package edu.unicauca.snmpinstrumentingserver.jmx.model;

import javax.management.monitor.CounterMonitor;
import javax.management.monitor.Monitor;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MyCounterMonitor implements MyMonitor {


	@Element
	private String attribute;
	@Element
	private Long period;
	@Element
	private Integer threshold;
	@Element
	private Double offset;
	@Attribute
	private String name;
	
	public MyCounterMonitor() {

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
	public Integer getThreshold() {
		return threshold;
	}
	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}
	public Double getOffset() {
		return offset;
	}
	public void setOffset(Double offset) {
		this.offset = offset;
	}
	
	public CounterMonitor getCounterMonitor(){
		CounterMonitor cm = new CounterMonitor();
		cm.setObservedAttribute(getAttribute());
		cm.setDifferenceMode(false);
		cm.setGranularityPeriod(getPeriod());
		cm.setInitThreshold(getThreshold());
		cm.setNotify(true);
		cm.setOffset(getOffset());
		return cm;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getName() {
		return name;
	}

	public Monitor getMonitor() {
		return getCounterMonitor();
	}
}
