/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.Serializable;

/**
 *
 * @author Maxwell
 */
public class SavedIP implements Serializable{
    protected String ip;
    protected int port;
    protected String name;
    public SavedIP(String ip, String name, int port){
        this.ip=ip;
        this.port=port;
        this.name=name;
    }
}
