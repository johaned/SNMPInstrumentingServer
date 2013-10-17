/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unicauca.snmpinstrumentingserver.main;

/**
 *
 * @author gestv
 */

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.unicauca.snmpinstrumentingserver.jmx.logic.DynamicMBeanFactory;
import edu.unicauca.snmpinstrumentingserver.snmp.logic.DynamicSnmpResourceFactory;

import edu.unicauca.snmpinstrumentingserver.model.Layout;
import edu.unicauca.snmpinstrumentingserver.snmp.model.SnmpResource;

@Path("/")
public class RemoteMBeanHelper {

	// Registra un MR para su gestion
	//curl -d "ip=192.168.119.35&port=10001&domain=broadcaster&type=Webservices" http://192.168.119.35:9998/snmp_mbs/register
	@POST
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String register(
			@FormParam("ip") String ip,
			@FormParam("port") String port,
			@FormParam("domain") String domain, 
			@FormParam("type") String type) {
		System.out.println(ip+" "+port+" "+domain+" "+type);
                SNMPInstrumentingServer.mdmbs = DynamicMBeanFactory.mbeans_register(Layout.PATHMBEANDESCRIPTOR, type);
                SNMPInstrumentingServer.srs = DynamicSnmpResourceFactory.snmp_resource_register(Layout.PATHMBEANDESCRIPTOR, type);

                for (SnmpResource snmpResource : SNMPInstrumentingServer.srs) {
                    snmpResource.startManaging();
                     (new Thread(snmpResource)).start();
                }
		return "";
	}

	// Remueve el registro de un MR
	//curl -d "ip=192.168.119.35&port=10001" -X DELETE http://192.168.119.35:9998/snmp_mbs/broadcaster/Webservices
	@DELETE
	@Path("/{domain}/{type}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String remove(
			@FormParam("ip") String ip,
			@FormParam("port") String port){
                System.out.println(ip+" "+port);
		return "";
	}
	
}
