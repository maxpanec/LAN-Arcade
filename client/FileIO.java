/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

/**
 *
 * @author Maxwell
 */
public class FileIO {
    public static void storeData(DataStorage data) throws IOException{
        try{
            FileOutputStream fos= new FileOutputStream(new File("data.txt"));
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (FileNotFoundException ex) {
            new File("data.txt").createNewFile();
            storeData(data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static DataStorage readData() throws IOException{
        try{
            FileInputStream fis= new FileInputStream(new File("data.txt"));
            ObjectInputStream ois= new ObjectInputStream(fis);
            DataStorage temp=(DataStorage)ois.readObject();
            ois.close();
            return temp;
        } catch (FileNotFoundException ex) {
            new File("data.txt").createNewFile();
        }
        catch(EOFException ex){
            return null;
        }
        catch (ClassNotFoundException ex) {
             ex.printStackTrace();
        }
        return null;
    }
}
