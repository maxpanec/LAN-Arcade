/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pong.Ball;
import pong.PlayerPong;
import pong.Pong;

/**
 *
 * @author Max Panec
 */
public class ClientHandler implements Runnable{
	
    private String name=null;
    protected final ObjectInputStream ois; 
    public final ObjectOutputStream oos; 
    protected Socket s;
    private Server server;
    public Thread ballThread;
    private int clientNum;
    public ClientHandler(Socket s, ObjectInputStream ois, ObjectOutputStream oos, Server server, int clientNum) { 
    	this.ois = ois; 
    	this.oos = oos;
    	this.s = s;
        this.server=server;
        this.clientNum=clientNum;
        System.out.println("\n"+clientNum+"\n");
    }
    public synchronized void sendData(Object temp) throws SocketException{
        try {
            oos.writeObject(temp);
            oos.flush();
        } 
        catch(SocketException ex){
           throw new SocketException(); 
        }catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void startDefaultGame(Class gameClass){
        try {
            sendData(gameClass);
        } catch (SocketException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(server.gameClass==Pong.class){
            boolean connected=true;
            while(connected){
                try{
                    Object data = ois.readObject();
                    if(data.getClass()==PlayerPong.class){
                        if(((PlayerPong)data).y>=250)
                            Server.players.replace(2, (PlayerPong)data);
                        else
                            Server.players.replace(1, (PlayerPong)data);
                        for (ClientHandler mc : Server.handlers)  
                        { 
                            if(!mc.equals(this)){
                                mc.sendData(data);
                            }
                        }  
                    }
                    else if(data.getClass()==String.class){
                        boolean duplicateName=false;
                        for(ClientHandler handler:server.handlers){
                            if(handler.equals(this))
                                continue;
                            if(handler.name.equals((String)data)){
                                duplicateName=true;
                                break;
                            }    
                        }
                        if(duplicateName){
                            oos.writeObject("duplicateName");
                            s.close();
                            oos.close();
                            ois.close();
                            Server.handlers.remove(this);
                            server.checkConnectionsThread.numPlayers--;
                            synchronized (server.lock) {
                                server.lock.notifyAll();
                            }
                            return;                     
                        }
                        name=(String)data;
                        PlayerPong temp;
                        if(clientNum==1){
                            temp=new PlayerPong(name,200,25);
                            Server.players.put(1, temp);
                            oos.writeObject(temp);
                            oos.flush();
                        }
                        else if(clientNum==2){
                            temp=new PlayerPong((String)data,200,460);
                            Server.players.put(2, temp);
                            oos.writeObject(temp);
                            oos.flush();
                        }
                    }
                } 
                catch(EOFException ex){
                    System.out.println("\n"+Server.handlers.size());
                    Server.handlers.remove(this);
                    System.out.println("\n"+Server.handlers.size());
                    connected=false;
                    Server.sendData=false;
                    for (ClientHandler mc : Server.handlers)  
                        { 
                            if(!mc.equals(this)){
                                try {
                                    mc.sendData("player disconnected");
                                } catch (SocketException ex1) {
                                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                            }
                        } 
                    try {
                        ois.close();
                        oos.close();
                        s.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    
                }
                catch (IOException e) {
                    System.out.println("\n"+Server.handlers.size());
                    Server.handlers.remove(this);
                    System.out.println("\n"+Server.handlers.size());
                    connected=false;
                    try {
                        ois.close();
                        oos.close();
                        s.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    Server.sendData=false; 
                } 
                catch (ClassNotFoundException ex) {
                    connected=false;
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }      
        }
    }
    public void createBallThread(){
        ballThread=new Thread(){
            public void run(){
                while(true){
                if(Server.ball!=null){
                    try {
                        /*if(server.ball.xSpeed==0){
                            sendData(server.winner);
                            break;
                        }*/
                        Ball temp=new Ball(Server.ball.x,Server.ball.y);
                        sendData(temp);
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch(SocketException ex){
                        try {
                            ballThread.join();
                        } catch (InterruptedException ex1) {
                            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
                }
            }
        };
        ballThread.start();
    }
    public void run() { 
        startDefaultGame(server.gameClass);
    } 
}