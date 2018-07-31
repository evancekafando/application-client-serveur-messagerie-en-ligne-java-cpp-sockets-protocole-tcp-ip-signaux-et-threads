/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.*;
/**
 *
 * @author EvancePc
 */
public class Chat implements Runnable {

    private Socket socket = null;
    private JFrame frame = null;
    private Thread t1, t2;
    public Emission e;
    private Reception r;
    private String nom = null;
    private String forum = null;

    public Chat(Socket s,JFrame f, String n, String fm){
        socket = s;
        frame = f;
        nom = n;
        forum  = fm;
    }

    public void stopThread(){
        t1.interrupt();
        t2.interrupt();
    }

    public void run (){
        e = new Emission(socket, frame,nom,forum);
        t1 = new Thread(e);
        t1.start();
        r = new Reception(socket, frame, forum);
        t2 = new Thread(r);
        t2.start();
        System.out.println("Le client est connecte!");
    }

}
