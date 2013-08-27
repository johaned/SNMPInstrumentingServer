package edu.unicauca.snmpinstrumentingserver.jmx.logic;


import javax.management.*;
import javax.management.monitor.*;

public class MonitorListener implements NotificationListener {

    public MonitorListener() {
        super();
    }

    public void handleNotification(Notification notification, Object handback) {
        
        MonitorNotification notif = (MonitorNotification)notification;
        Monitor monitor = (Monitor)notif.getSource();
        String type = notif.getType();
        String object=notif.getObservedObject().toString();
        String attribute=notif.getObservedAttribute();
        
        try {
            if (type.equals(MonitorNotification.OBSERVED_OBJECT_ERROR)) {
                System.out.println("\n\t>> " + notif.getObservedObject().getClass().getName() + " is not registered in the server");
                System.out.println("\t>> Stopping the CounterMonitor...\n");
                monitor.stop();
            }                
            else if (type.equals(MonitorNotification.OBSERVED_ATTRIBUTE_ERROR)) {
                System.out.println("\n\t>> " + attribute + " from " + object + " attribute is not contained in " + 
                                   notif.getObservedObject().getClass().getName());
                System.out.println("\t>> Stopping the CounterMonitor...\n");
                monitor.stop();
            }                
            else if (type.equals(MonitorNotification.OBSERVED_ATTRIBUTE_TYPE_ERROR)) {
                System.out.println("\n\t>> The type of " + attribute + " from " + object + " attribute is not correct");
                System.out.println("\t>> Stopping the CounterMonitor...\n");
                monitor.stop();
                
            }
            else if (type.equals(MonitorNotification.THRESHOLD_ERROR)) {
                System.out.println("\n\t>> Threshold type is not <Integer>");     
                System.out.println("\t>> Stopping the CounterMonitor...\n");
                monitor.stop();
            }                
            else if (type.equals(MonitorNotification.RUNTIME_ERROR)) {
                //System.out.println("\n\t>> Runtime error (?)"); 
                //System.out.println("\t>> Stopping the CounterMonitor...\n");
                monitor.start();
            }                
            else if (type.equals(MonitorNotification.THRESHOLD_VALUE_EXCEEDED)) {
                System.out.println("\n\t>> " + attribute + " from " + object + " has reached the threshold\n");
            }
            else if (type.equals(MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED)) {
                System.out.println("\n\t>> " + attribute + " from " + object + " has reached the High threshold\n");
            }
            else if (type.equals(MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED)) {
                System.out.println("\n\t>> " + attribute + " from " + object + " has reached the Low threshold\n");
            }
            else if (type.equals(MonitorNotification.STRING_TO_COMPARE_VALUE_MATCHED)) {
                System.out.println("\n\t>> " + attribute + " from " + object + " matches the compare value\n");
            }     
            else if (type.equals(MonitorNotification.STRING_TO_COMPARE_VALUE_DIFFERED)) {
                System.out.println("\n\t>> " + attribute + " from " + object + " differs the compare value\n");
            }            
            else {
                System.out.println("\n\t>> Unknown event type (?)\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}