/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pong;

import java.io.Serializable;

/**
 *
 * @author Maxwell
 */
public class PlayerPong implements Serializable{
    public String name;
    public int x=0;
    public int y=0;
    
    public PlayerPong(String name, int x, int y) {
	this.name=name;
        this.x=x;
        this.y=y;
    }

}
