/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.Socket;
/**
 *
 * @author EvancePc
 */

public class Reception implements Runnable {

	private Socket socket = null;
	private JFrame frame = null;
        private String forum = null;

	public Reception(Socket s,JFrame f,String fm){
	    socket = s;
            frame = f;
            forum = fm;
	}

	public void run() {
                byte[] bytes;
		while(socket.isConnected()){
	        try {
                    bytes = new byte[socket.getInputStream().available()];
                    socket.getInputStream().read(bytes);
                    String data = new String(bytes);
                    if(!data.isEmpty()){
                        String [] value = data.split("#");
                        if(value[0].trim().equalsIgnoreCase(forum.trim())){
                        frame.jTextArea.append(value[1]+"\n");
                        }else if(value.length==1){
                            if(data.compareTo("System down")==1) {
                                frame.jTextArea.append("System down\n");
                                System.out.println("fermeture du socket");
                                frame.jTextPane1.setText("Hors ligne");
                                Thread.sleep(5500);
                                socket.close();
                                System.exit(0);
                            }else{
                              frame.jTextArea.append(value[0]+"\n");
                            }
                        }

                    }
		    } catch (IOException e) {
				//e.printStackTrace();
	            }catch(InterruptedException ex) {
                            //Thread.currentThread().interrupt();
                    }
		}
	}

}
