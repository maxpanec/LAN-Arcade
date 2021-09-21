/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pong;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.logging.Level;
import java.util.logging.Logger;
import client.*;
/**
 *
 * @author Maxwell
 */
public class Pong extends Canvas implements Runnable{
    public Ball ball=new Ball(250,250);
    protected Client client;
    protected Thread thread;
    private KeyInputPong keyInput;
    public boolean running=true;
    public int whatToRender=0;
    public String winner;
    public Pong(Client client) {
        this.client=client;
        this.addKeyListener(keyInput=new KeyInputPong(client));
    }

    public void renderGame(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs==null) {
            this.createBufferStrategy(3);
            return;
	}
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.gray);
        g.fillRect(0, 0, 500, 500);
        g.setColor(Color.blue);
        client.players.forEach((k,v)->{
            if(k.equals(client.name)){
                g.setColor(Color.blue);
                g.fillRect(((PlayerPong)v).x, ((PlayerPong)v).y, 100, 15);
            }
            else 
                g.setColor(Color.red);
                g.fillRect(((PlayerPong)v).x, ((PlayerPong)v).y, 100, 15);
        });
        g.setColor(Color.pink);
        g.fillOval((int)ball.x,(int)ball.y, 15, 15);
        g.dispose();
	bs.show();
    }
    public void renderLostConnection(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs==null) {
            this.createBufferStrategy(3);
            return;
	}
        Graphics g = bs.getDrawGraphics();
        g.drawString("Lost Connection", 220, 240);
        g.dispose();
	bs.show();
    }
    public void renderDisconnectedPlayer(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs==null) {
            this.createBufferStrategy(3);
            return;
	}
        Graphics g = bs.getDrawGraphics();
        g.drawString("Other Player Disconnected", 210, 240);
        g.dispose();
	bs.show();
    }
    public void renderWinner(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs==null) {
            this.createBufferStrategy(3);
            return;
	}
        Graphics g = bs.getDrawGraphics();
        g.drawString("Game Over", 210, 240);
        g.drawString(winner + " Has Won", 210, 250);
        g.drawString("Press Escape to Exit", 210, 260);
        g.dispose();
	bs.show();
    }
    public void run() {
        while(running){
            if(whatToRender==0)
                renderGame();
            else if(whatToRender==1){
                renderLostConnection();
            }
            else if(whatToRender==2){
                renderDisconnectedPlayer();
            }
            else if(whatToRender==3){
                renderWinner();
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException ex) {
                Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
    }
    public synchronized void stop(){
        try {
            this.removeKeyListener(keyInput);
            thread.join();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
