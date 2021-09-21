/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pong;

import client.Client;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 *
 * @author Maxwell
 */
public class KeyInputPong extends KeyAdapter{
    private Client client;
    public KeyInputPong(Client client){
        this.client=client;
    }
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        try{
            PlayerPong temp;
            switch (key) {
                case KeyEvent.VK_A:
                    ((PlayerPong)client.players.get(client.name)).x+=-6;
                    temp = new PlayerPong(client.name,((PlayerPong)client.players.get(client.name)).x,((PlayerPong)client.players.get(client.name)).y);
                    client.oos.writeObject(temp);
                    client.oos.flush();
                    break;
                case KeyEvent.VK_D:
                    ((PlayerPong)client.players.get(client.name)).x+=6;
                    temp = new PlayerPong(client.name,((PlayerPong)client.players.get(client.name)).x,((PlayerPong)client.players.get(client.name)).y);
                    client.oos.writeObject(temp);
                    client.oos.flush();
                    break;
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
        catch(IOException ee){
            ee.printStackTrace();
        }
    }

}
