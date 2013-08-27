SNMPInstrumentingServer
=======================

Server to instrument SNMP according to JMX specification

This project makes a small middleware between SNMP and JMX Server.

Assumptions
	- JMX XML descriptor must exists (see snmp_instrumentig_stcav_descriptor project)
	- Netbeans IDE installed

SNMP Installing & Configuring
> sudo apt-get install snmpd

Server Configuring
	change the PATHMBEANDESCRIPTOR and MEDIASERVER parameters in Layout file into the edu.unicauca.snmpinstrumentingserver.model package.

	Put the MediaServer XML file into the directory with path to PATHMBEANDESCRIPTOR see the snmp_instrumentig_stcav_descriptor project, in this you will can find the generator to MediaServer XML file.

	you can modify the QUERYINGINTERVAL parameter in the same Layout file, this parameter indicates what is the interval time between queries to specific object in SNMP Server, this time is given in milliseconds.

	finally, you must add the next library:
		- simple-xml-2.6.9
		- gson-1.7.1 +
		- snmp4j-2.1.0

TODO
	- make a support system to make the notification process
	- incorporate maven 
	- incorporate automatic tests
