/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import pong.Ball;
import pong.PlayerPong;
import pong.Pong;

/**
 *
 * @author Max Panec
 */
public class Server {
    
    public static Vector<ClientHandler> handlers = new Vector<>();
    private final ServerSocket ss;
    public static int maxPlayers;
    protected static int numPlayers=0;
    protected static boolean sendData=true;
    protected String winner;
    protected Class gameClass;
    protected CheckConnections checkConnectionsThread;
    protected Thread pongThread;
    protected static Hashtable players;
    protected static Ball ball;
    protected final Object lock=new Object();
    public Server(int port, int maxPlayers, Class gameClass) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                closeConnections();
            }
        });
        ss=new ServerSocket(port,2);
        this.maxPlayers=maxPlayers;
        this.gameClass=gameClass;
        if(gameClass==Pong.class){
            players=new Hashtable<Integer,PlayerPong>();
        }
        checkNewConnections();
    }
    private void checkNewConnections(){
        checkConnectionsThread=new CheckConnections(ss,this);
        checkConnectionsThread.start();
    }
    public void sendPongData(){
        pongThread=new Thread(){
            public void run(){
                ball=new Ball(); 
                while(sendData){
                   ball.x+=ball.xSpeed;
                   ball.y+=ball.ySpeed;
                   if(ball.x<=1||ball.x+15>=499){
                        ball.xSpeed*=-1;
                   }
                   if((ball.x>=((PlayerPong)players.get(1)).x&&ball.x+15<=((PlayerPong)players.get(1)).x+100)&&(ball.y<=((PlayerPong)players.get(1)).y+16&&ball.y>=((PlayerPong)players.get(1)).y)){
                       ball.ySpeed*=-1;
                    }
                   if((ball.x>=((PlayerPong)players.get(2)).x&&ball.x+15<=((PlayerPong)players.get(2)).x+100)&&(ball.y+15>=((PlayerPong)players.get(2)).y&&ball.y+15<=((PlayerPong)players.get(2)).y+16)){
                       ball.ySpeed*=-1;
                   }
                   if(ball.y<=1||ball.y+15>=499){
                        ball.xSpeed=0;
                        ball.ySpeed=0;
                        winner=ball.y<=1?((PlayerPong)players.get(2)).name:((PlayerPong)players.get(1)).name;
                        for (ClientHandler mc : Server.handlers){
                            try {
                                    mc.sendData(winner);
                                } catch (SocketException ex1) {
                                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                        }
                        sendData=false;
                   }
                   try {
                       Thread.sleep(3);
                   } catch (InterruptedException ex) {
                       Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                   }
                }
                try {
                    closeConnections();
                    pongThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        pongThread.start();
    }
    private void closeConnections(){
        try {
            ss.close();
            for(ClientHandler handler:handlers){
                handler.s.close();
                handler.ois.close();
                handler.oos.close();
            }
        } 
        catch (IOException ex) {
            
        } 
    }
}
class CheckConnections extends Thread{
    private ServerSocket ss;
    private Server server;
    protected int numPlayers=0;
    public final Object lock;
    public CheckConnections(ServerSocket ss, Server server){
        this.ss=ss;
        this.server=server;
        lock=server.lock;
    }
    public void run(){
        boolean run = true;
        while (run)  
        { 
           try {
                System.out.println("checking for connection");
                Socket connection = ss.accept();
                if(server.handlers.size()>=server.maxPlayers){
                    connection.close();
                    continue;
                }
                numPlayers++;
                System.out.println("New client connected"); 
                ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream()); 
                oos.reset();
                ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());  
                System.out.println("Streams Setup");  
                ClientHandler handler = new ClientHandler(connection, ois, oos,server,numPlayers); 
                Thread t = new Thread(handler);  
                System.out.println("ClientThread Active"); 
                server.handlers.add(handler); 
                t.start(); 
           } catch (IOException ex) {
               run = false;
           }
        } 
    }
}

