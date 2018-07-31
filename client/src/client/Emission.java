/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.Socket;
//import java.util.Scanner;
import java.awt.event.*;
import java.applet.Applet;
/**
 *
 * @author EvancePc
 */

public class Emission implements Runnable {

	private Socket socket = null;
        private JFrame frame = null;
        private String nom = null;
        private String forum = "";


	public Emission(Socket s, JFrame f, String n, String fm) {
            socket = s;
            frame = f;
            nom = n;
            forum  = fm;
	}

        public void Write(String message){
            try {
                    socket.getOutputStream().write(message.getBytes());
                    socket.getOutputStream().flush();
            }catch (IOException e) {
                        //e.printStackTrace();
            }
        }
	public void run() {
            frame.envoyer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    String value = frame.textArea1.getText();
                        if(!value.isEmpty()){
                            Write(forum+"#"+nom.toUpperCase()+" : "+value);
                            frame.textArea1.setText("");
                        }
                }
            });

	}
}
