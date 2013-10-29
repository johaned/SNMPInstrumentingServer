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
import edu.unicauca.snmpinstrumentingserver.jmx.model.MyDynamicMBean;
import edu.unicauca.snmpinstrumentingserver.snmp.logic.DynamicSnmpResourceFactory;

import edu.unicauca.snmpinstrumentingserver.model.Layout;
import edu.unicauca.snmpinstrumentingserver.snmp.model.SnmpResource;
import java.util.ArrayList;

@Path("/")
public class RemoteMBeanHelper {

	// Registra un MR para su gestion
	// curl -d "domain=SNMPInstrumentingServer&type=BroadcasterServer" http://192.168.119.35:9998/snmp_mbs/register
	@POST
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String register(
			@FormParam("domain") String domain, 
			@FormParam("type") String type) {
            String retorno="fail";
                SNMPInstrumentingServer.mdmbs = DynamicMBeanFactory.mbeans_register(Layout.PATHMBEANDESCRIPTOR, type);
                SNMPInstrumentingServer.srs = DynamicSnmpResourceFactory.snmp_resource_register(Layout.PATHMBEANDESCRIPTOR, type);

                for (SnmpResource snmpResource : SNMPInstrumentingServer.srs) {
                    snmpResource.startManaging();
                     (new Thread(snmpResource)).start();
                }
                if(SNMPInstrumentingServer.mdmbs.size()>0)
                    retorno="ok";
		return retorno;
	}

	// Remueve el registro de un MR
	// curl -X DELETE http://192.168.119.35:9998/snmp_mbs/BroadcasterServer/broadcasterTest
	@DELETE
	@Path("/{type}/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String remove(
                          @PathParam("type") String type,
               		  @PathParam("name") String name){
                for (MyDynamicMBean mbean : SNMPInstrumentingServer.mdmbs) {
                    if(mbean.getName().equals(name) && mbean.getType().equals(type)){
                        SNMPInstrumentingServer.mdmbs.remove(mbean);
                        break;
                    }
                }
                DynamicMBeanFactory.removeDynamicBean("SNMPInstrumentingServer",name,type);
                
                
                List<Integer> resindices = new ArrayList<Integer>();
                
                for (int i = 0; i < SNMPInstrumentingServer.srs.size(); i++ ) {
                    SnmpResource snmpResource = SNMPInstrumentingServer.srs.get(i);
                    if(snmpResource.getServerName().equals(type) && snmpResource.getName().equals(name)){
                        snmpResource.stoptManaging();
                        snmpResource.kill();
                        resindices.add(i);
                    }
                }
                SNMPInstrumentingServer.srs.remove(0);
                
                for(int i = 0; i < resindices.size(); i++)
                    SNMPInstrumentingServer.srs.remove(resindices.get(i));
                
		return "";
	}
	
}
