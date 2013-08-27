/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unicauca.snmpinstrumentingserver.snmp.model;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 *
 * @author stcav
 */
public class SnmpManager {

    String agentIP;

    public SnmpManager() {
    }

    public String getAgentIP() {
        return agentIP;
    }

    public void setAgentIP(String agentIP) {
        this.agentIP = agentIP;
    }

    public ResponseEvent get(String oid) {
        try {
            Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();

            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(GenericAddress.parse("udp:" + agentIP + "/161"));
            target.setRetries(2);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version2c);
            ResponseEvent response = new ResponseEvent(snmp,target.getAddress(), pdu, null, null);
            response = snmp.send(pdu, target);
            System.out.println(response.getResponse().toString());
            snmp.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
