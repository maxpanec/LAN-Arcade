package client;

import java.awt.Component;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pong.*;
import server.Server;

public class Client {
    //Player variables
    public String name=null;
    protected ArrayList<String> notAllowedNames=null;
    public Hashtable players;
    //Frame variables
    protected Frame frame;
    protected Hashtable<Class,Component> components;
    //Server variable
    protected Server server;
    //Socket variables
    private Socket connection;
    public Thread dataInputThread;
    protected ObjectInputStream ois;
    public ObjectOutputStream oos;
    protected Hashtable<String,SavedIP> servers =null;
    public boolean readData=true;
    //Game variables
    public Pong pong=null;
    //DataStorage variables
    protected DataStorage data=null;
    
    //constructor
    public Client(){
        setLook();
        components=new Hashtable<Class,Component>();
        setupFileData();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                closeConnections();
            }
        });
    }
    //creates a server in the background and connects to it
    public void hostServer(String ip, int port,int numPlayers,Class gameClass) throws IOException, ClassNotFoundException{
        server=new Server(port,numPlayers,gameClass);
        connectToServer(ip,port);
    }
    //connects to a server
    public String connectToServer(String ip, int port){
        try{
        connection=new Socket(ip,port);
        }
        catch(ConnectException ex){
            ex.printStackTrace();
            return "";
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try{
            ois = new ObjectInputStream(connection.getInputStream());
            oos=new ObjectOutputStream(connection.getOutputStream());
            oos.reset();
        }
        catch(EOFException ex){
            closeConnections();
            return "";
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        Class whichGame=null;
        while(whichGame==null){
            try {
                whichGame=(Class)ois.readObject();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(whichGame==Pong.class){
            PlayerPong player=null;
            try {
                oos.writeObject(name);
                oos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            while(player==null){
                Object data = null;
                try {
                    data = ois.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(data.getClass()==PlayerPong.class){
                    player=(PlayerPong)data;
                }
                else if(data.getClass()==String.class){
                    if(data.equals("duplicateName")){
                        closeConnections();
                        notAllowedNames.add(name);
                        return "duplicateName";
                    }
                }
            }
            players=new Hashtable<String, PlayerPong>();
            players.put(player.name,player);
            try {
                oos.writeObject(player);
                oos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            createPongDataInputThread();
        }
        return "success";
    }
    //if the selected game is pong client starts reading pong data from the server
    private void createPongDataInputThread(){
        dataInputThread=new Thread(){
            public void run(){
                while(true){
                    try{
                        Object data=ois.readObject();
                        if(data.getClass()==PlayerPong.class){
                            PlayerPong playerData = (PlayerPong)data;
                            if(!players.containsKey(playerData.name)){
                                players.put(playerData.name, playerData);
                                oos.writeObject(players.get(name));
                                oos.flush();
                            }
                        }
                        else if(data.getClass()==String.class){
                            String temp = (String) data;
                            if(temp.equals("start")){
                                if(components.containsKey(Connections.class)) 
                                    frame.remove(components.get(Connections.class));
                                else if(components.containsKey(Waiting.class)) 
                                    frame.remove(components.get(Waiting.class));
                                frame.add(pong=new Pong(Client.this));
                                pong.start();
                                frame.pack();
                                break;
                            }
                        }
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                while(readData){
                    try{
                        Object data=ois.readObject();
                        if(data.getClass()==PlayerPong.class){
                            PlayerPong playerData = (PlayerPong)data;
                            players.replace(playerData.name, playerData);
                        }
                        else if(data.getClass()==Ball.class){
                            Ball ballData = (Ball)data;
                            pong.ball=ballData;
                        }
                        else if (data.getClass()==String.class){
                            System.out.print(data);
                            if(data.equals("player disconnected")){
                                pong.whatToRender=2;
                            }
                            else{
                                pong.whatToRender=3;
                                pong.winner=(String)data;
                            }
                            readData=false;
                            closeConnections();
                        }
                    }
                    catch(SocketException ex){
                        pong.whatToRender=1;
                        readData=false;
                        closeConnections();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    } 
                    catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } 
                }
                try {
                    dataInputThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        dataInputThread.start();
    }
    //helper methods
    private static void setLook(){
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }
    private void setupFileData(){
        try{
            data=FileIO.readData();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        if(data==null){
            data=new DataStorage();
            servers=new Hashtable<String,SavedIP>();
            notAllowedNames=new ArrayList<String>();
            components.put(Name.class, new Name(this));
            frame=new Frame(this);
            frame.add(components.get(Name.class));
            frame.pack();
        }
        else{
            servers=data.servers;
            name=data.name;
            notAllowedNames=data.notAllowedNames;
            if(servers==null){
                servers=new Hashtable<String,SavedIP>();
                data.servers=servers;
            }
            if(notAllowedNames==null){
                notAllowedNames=new ArrayList<String>();
                data.notAllowedNames=notAllowedNames;
            }
            if(name==null){
                components.put(Name.class, new Name(this));
                frame=new Frame(this);
                frame.add(components.get(Name.class));
                frame.pack();
            }
            else{
                components.put(Menu.class, new Menu(this));
                frame=new Frame(this);
                frame.add(components.get(Menu.class));
                frame.pack();
            }
        }
    }
    public void gameOver(){
        frame.remove(pong);
        frame.removeAll();
        pong=null;
        server=null;
        frame.add(components.put(Menu.class, new Menu(this)));
    }
    private void closeConnections(){
        try {
            connection.close();
            ois.close();
            oos.close();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
}
