/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

/**
 *
 * @author EvancePc
 */
public class Client {

    public static Socket socket = null;
    private static String serverName = "abaque.ca";
    private static int   serverPort = 1550;
    public static Thread t;
    public static boolean connect  = false;
    public static String forum = "";
    public static String nom = "";
    private static JFrame frame;
    private static Chat chat;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        frame = new JFrame();
        frame.setVisible(true);
        frame.jTextPane1.setText("Hors ligne");
        frame.amitie.addActionListener( new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e){
                   frame.culture.setEnabled(false);
                   frame.sport.setEnabled(false);
                   forum = "amitie";
               }

        });
        frame.culture.addActionListener( new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e){
                  frame.sport.setEnabled(false);
                  frame.amitie.setEnabled(false);
                  forum = "culture";
               }

        });
        frame.sport.addActionListener( new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e){
                 frame.culture.setEnabled(false);
                 frame.amitie.setEnabled(false);
                 forum = "sport";
               }
        });
            frame.addWindowListener( new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        try {
                             if(connect){
                                socket.close();
                                connect = false;
                                chat.stopThread();
                                t.interrupt();
                                System.out.println("fermeture du socket");
                             }
                        }catch (IOException e) {
	                    System.err.println("Erreur lors de la fermeture");
	                }
                        System.exit(0);
                    }
                } );
            frame.connexion.addActionListener( new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e){
                   try {
                           nom = frame.nickname.getText();
                           if(!connect && !forum.isEmpty() && !nom.isEmpty()){
                            frame.nickname.setEditable(false);
                            socket = new Socket(serverName, serverPort);
                            connect = true;
                            frame.jTextPane1.setText("En ligne");
                            System.out.println("Connexion établie avec le serveur");
                            chat = new Chat(socket, frame, nom, forum);
                            t = new Thread(chat);
                            t.start();
                           }else{
                               if(forum.isEmpty()) {
                                   JOptionPane.showMessageDialog(frame, "Choisir le forum");
                                   return;
                               }
                               if(nom.isEmpty()){
                                   JOptionPane.showMessageDialog(frame, "Choisir le nickname");
                                   //return;
                               }
                           }
                        }catch (IOException ex) {
	                    System.err.println("Impossible de se connecter au serveur");
	                }
               }
            });
            frame.deconnexion.addActionListener( new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e){
                   try {
                          if(connect){
                            chat.e.Write("client deconnecte");
                            socket.close();
                            connect = false;
                            chat.stopThread();
                            t.interrupt();
                            frame.jTextPane1.setText("Hors ligne");
                            System.out.println("fermeture du socket");
                            }
                            frame.culture.setEnabled(true);
                            frame.sport.setEnabled(true);
                            frame.amitie.setEnabled(true);
                            forum = "";
                        }catch (IOException ex) {
	                    System.err.println("Erreur lors de la fermeture");
	                }
                        System.exit(0);
               }
            });


    }

}
