package edu.unicauca.snmpinstrumentingserver.jmx.model;

import javax.management.monitor.Monitor;

public interface MyMonitor {
	public String getName();
	public void setName(String name);
	public Monitor getMonitor();
}
