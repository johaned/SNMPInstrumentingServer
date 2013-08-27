package edu.unicauca.snmpinstrumentingserver.jmx.model;

import javax.management.monitor.GaugeMonitor;
import javax.management.monitor.Monitor;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MyGaugeMonitor implements MyMonitor {


	@Element
	private String attribute;
	@Element
	private Long period;
	@Element
	private Double thresholdHigh;
	@Element
	private Double thresholdLow;
	@Attribute
	private String name;
	@Element
	private boolean difference;
	@Element
	private boolean notifyHigh;	
	@Element
	private boolean notifyLow;	
	
	public MyGaugeMonitor() {

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
	public Double getThresholdHigh() {
		return thresholdHigh;
	}
	public void setThresholdHigh(Double thresholdHigh) {
		this.thresholdHigh = thresholdHigh;
	}
	public Double getThresholdLow() {
		return thresholdLow;
	}
	public void setThresholdLow(Double thresholdLow) {
		this.thresholdLow = thresholdLow;
	}
	
	public GaugeMonitor getGaugeMonitor(){
		GaugeMonitor gm = new GaugeMonitor();
		gm.setDifferenceMode(getDifference());
		gm.setGranularityPeriod(getPeriod());
		gm.setNotifyHigh(getNotifyHigh());
		gm.setNotifyLow(getNotifyLow());
		gm.setObservedAttribute(getAttribute());
		gm.setThresholds(getThresholdHigh(), getThresholdLow());
		return gm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Monitor getMonitor() {
		return getGaugeMonitor();
	}

	public boolean getDifference() {
		return difference;
	}

	public void setDifference(boolean difference) {
		this.difference = difference;
	}

	public boolean getNotifyHigh() {
		return notifyHigh;
	}

	public void setNotifyHigh(boolean notifyHigh) {
		this.notifyHigh = notifyHigh;
	}

	public boolean getNotifyLow() {
		return notifyLow;
	}

	public void setNotifyLow(boolean notifyLow) {
		this.notifyLow = notifyLow;
	}

}
