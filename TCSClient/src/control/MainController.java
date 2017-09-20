/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import message.ClientMessage;
import message.ServerMessage;
import modal.User;

/**
 *
 * @author Nam
 */
public class MainController {

    //frame control
    LoginController loginControl;
    RegisterController registerControl;
    MainScreenController mainScreenControl;
    OfflineMessagesController offlineMessagesControl;

    //others
    public User user;
    Socket socket;
    boolean running;

    HashMap<String, ChatController> chats = new HashMap();
    HashMap<String, ArrayList<String>> offlineMessages;

    public MainController() {
        loginControl = new LoginController(this);
    }

    public void connectServer() {
        try {
            socket = new Socket("localhost", 9999);
            //create obj to read object from server
            ServerListener listener = new ServerListener();
            listener.start();
        } catch (IOException ex) {
            System.out.println("IO error");
        }
    }

    class ServerListener extends Thread {

        @Override
        public void run() {
            running = true;
            try {
                while (running) {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ServerMessage mess = (ServerMessage) ois.readObject();
                    //handle each case of server response
                    switch (mess.command) {
                        case ServerMessage.NEW_REGISTER:
                            String usernameFrom = mess.from.username;
                            mainScreenControl.addNewRegister(usernameFrom);
                            break;
                        case ServerMessage.REGISTER_SUCCESS:
                            registerControl.closeRegisterForm();
                            running = false;
                            break;
                        case ServerMessage.REGISTER_FAIL:
                            registerControl.showRegisterFail();
                            running = false;
                            break;
                        case ServerMessage.NEW_ONLINE:
                            usernameFrom = mess.from.username;
                            mainScreenControl.setStatusOnline(usernameFrom);
                            //if this user already opened chat dialog with new online
                            if (chats.containsKey(usernameFrom)) {
                                chats.get(usernameFrom).setOnlineStatus();
                            }
                            break;
                        case ServerMessage.LOGIN_SUCCESS:
                            user = mess.to;
                            loginControl.openMainScreen();
                            mainScreenControl.displayUsersList(mess.users);
                            mainScreenControl.getOfflineMessages(mess.offlineMessages);
                            break;
                        case ServerMessage.LOGIN_FAIL:
                            loginControl.showLoginFail();
                            break;
                        case ServerMessage.NEW_LOGOUT:
                            usernameFrom = mess.from.username;
                            mainScreenControl.setOfflineStatus(usernameFrom);
                            if (chats.containsKey(usernameFrom)) {
                                chats.get(usernameFrom).setOfflineStatus();
                            }
                            break;
                        case ServerMessage.NEW_MESS:
                            usernameFrom = mess.from.username;
                            mainScreenControl.receiveMessage(usernameFrom, mess.message);
                            break;
                    }
                }
            } catch (Exception ex) {
                if (registerControl != null && registerControl.register != null) {
                    registerControl.closeRegisterForm();
                } else {
                    mainScreenControl.closeMainScreen();
                }
                JOptionPane.showMessageDialog(loginControl.login, "Server shut down");
            }
        }

    }

    public void sendMessageToServer(ClientMessage message) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
