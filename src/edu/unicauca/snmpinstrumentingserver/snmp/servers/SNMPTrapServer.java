/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unicauca.snmpinstrumentingserver.snmp.servers;

import edu.unicauca.snmpinstrumentingserver.jmx.InstrumentingController;
import edu.unicauca.snmpinstrumentingserver.model.Layout;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.tools.console.SnmpRequest;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

/**
 *
 * @author johaned
 */
public class SNMPTrapServer implements CommandResponder {

    public SNMPTrapServer() {
    }

    public static void main(String[] args) {
        SNMPTrapServer snmp4jTrapReceiver = new SNMPTrapServer();
        try {
            snmp4jTrapReceiver.listen(new UdpAddress("127.0.0.1/" + Layout.SNMPTRAPPORT));
        } catch (IOException e) {
            System.err.println("Error in Listening for Trap");
            System.err.println("Exception Message = " + e.getMessage());
        }
    }

    /**
     * This method will listen for traps and response pdu's from SNMP agent.
     */
    public synchronized void listen(TransportIpAddress address)
            throws IOException {
        AbstractTransportMapping transport;
        if (address instanceof TcpAddress) {
            transport = new DefaultTcpTransportMapping((TcpAddress) address);
        } else {
            transport = new DefaultUdpTransportMapping((UdpAddress) address);
        }

        ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
        MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(
                threadPool, new MessageDispatcherImpl());

        // add message processing models
        mtDispatcher.addMessageProcessingModel(new MPv1());
        mtDispatcher.addMessageProcessingModel(new MPv2c());

        // add all security protocols
        SecurityProtocols.getInstance().addDefaultProtocols();
        SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

        // Create Target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));

        Snmp snmp = new Snmp(mtDispatcher, transport);
        snmp.addCommandResponder(this);

        transport.listen();
        System.out.println("Listening on " + address);

        try {
            this.wait();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method will be called whenever a pdu is received on the given port
     * specified in the listen() method
     */
    public synchronized void processPdu(CommandResponderEvent cmdRespEvent) {
        System.out.println("Received PDU...");
        PDU pdu = cmdRespEvent.getPDU();
        if (pdu != null) {
            //** Notify to JMX Core
            try {
                InstrumentingController ic = InstrumentingController.getInstance();
                Iterator iterator = pdu.getVariableBindings().iterator();
                while (iterator.hasNext()) {
                    System.out.println(((VariableBinding) iterator.next()).getOid());
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(SNMPTrapServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            //**
            System.out.println("Trap Type = " + pdu.getType());
            System.out.println("Variable Bindings = "
                    + pdu.getVariableBindings() + " +++ " + pdu.getRequestID());
            int pduType = pdu.getType();
            if ((pduType != PDU.TRAP) && (pduType != PDU.V1TRAP)
                    && (pduType != PDU.REPORT) && (pduType != PDU.RESPONSE)) {
                pdu.setErrorIndex(0);
                pdu.setErrorStatus(0);
                pdu.setType(PDU.RESPONSE);
                StatusInformation statusInformation = new StatusInformation();
                StateReference ref = cmdRespEvent.getStateReference();
                try {
                    System.out.println(cmdRespEvent.getPDU());
                    cmdRespEvent.getMessageDispatcher().returnResponsePdu(
                            cmdRespEvent.getMessageProcessingModel(),
                            cmdRespEvent.getSecurityModel(),
                            cmdRespEvent.getSecurityName(),
                            cmdRespEvent.getSecurityLevel(), pdu,
                            cmdRespEvent.getMaxSizeResponsePDU(), ref,
                            statusInformation);
                } catch (MessageException ex) {
                    System.err.println("Error while sending response: "
                            + ex.getMessage());
                    LogFactory.getLogger(SnmpRequest.class).error(ex);
                }
            }
        }
    }
}
