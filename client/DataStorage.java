/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Max Panec
 */
public class DataStorage implements Serializable{
    public Hashtable servers=null;
    public String name=null;
    public ArrayList<String> notAllowedNames=null;
    public DataStorage(){
        
    }
}
