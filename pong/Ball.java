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
public class Ball implements Serializable{
    public double x=250;
    public double y=250;
    public double xSpeed=.5;
    public double ySpeed=.5;
    
    public Ball(){
        int randX=(int)(Math.random()*2);
        int randY=(int)(Math.random()*2);
        if(randX==0)
            xSpeed=-xSpeed;
        if(randY==0)
            ySpeed=-ySpeed;
    }
    public Ball(double x,double y){
        this.x=x;
        this.y=y;
    }
}
