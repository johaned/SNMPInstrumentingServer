/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unicauca.snmpinstrumentingserver.jmx.model;

/**
 * @author stcav
 */
public class MbeanAbstract {
    private Long id;
    private String name;
    private MyDynamicMBean mdmb;

    public MbeanAbstract() {
    }

    public MbeanAbstract(MyDynamicMBean mdmb, String label) {
        this.id = get_id_from_labelLayout(label);
        this.name = get_name_from_labelLayout(label);
        this.mdmb = mdmb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MyDynamicMBean getMdmb() {
        return mdmb;
    }

    public void setMdmb(MyDynamicMBean mdmb) {
        this.mdmb = mdmb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String get_label(){
        return id+":"+name;
    }
    
    private Long get_id_from_labelLayout(String label){
        return Long.parseLong(label.split(":")[0]);
    }
    
    private String get_name_from_labelLayout(String label){
        return label.split(":")[1];
    }
    
}
