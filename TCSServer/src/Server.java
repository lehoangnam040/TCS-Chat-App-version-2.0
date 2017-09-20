
import io.IOController;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.ClientMessage;
import message.ServerMessage;
import modal.User;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nam
 */
public class Server {

    ServerSocket server;
    ArrayList<User> users;
    HashMap<String, Socket> onlines;

    public Server() {
        //load from file
        users = IOController.loadUsersListFromFile();
        onlines = new HashMap<>();
    }

    public void start() {
        try {
            server = new ServerSocket(9999);
            ClientAccepter accepter = new ClientAccepter();
            accepter.start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class ClientAccepter extends Thread {

        @Override
        public void run() {
            System.out.println("Server starting........!");
            while (true) {
                try {
                    Socket socket = server.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    handler.start();
                } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public class ClientHandler extends Thread {

        //served client's information
        Socket socket;
        User client;

        private ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                OUTER:
                while (true) {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ClientMessage mess = (ClientMessage) ois.readObject();
                    switch (mess.command) {
                        case ClientMessage.REGISTER:
                            //check and add new user
                            addUser(mess.from);
                            break OUTER;
                        case ClientMessage.LOG_IN:
                            //check user is on list and check password
                            connectUser(mess.from);
                            break;
                        case ClientMessage.LOG_OUT:
                            IOController.updateOfflineMessages(mess.from.username, mess.offlineMessages);
                            notifyOffline(mess.from);
                            break OUTER;
                        case ClientMessage.CHAT:
                            forwardsChatMessage(mess);
                            break;
                    }
                }
            } catch (Exception ex) {
                System.out.println("user " + client.username + " suddenly lost connection");
                notifyOffline(client);
            }
        }

        public void addUser(User new_register) {
            ServerMessage mess = new ServerMessage();
            if (users.contains(new_register)) {
                //register fail
                mess.command = ServerMessage.REGISTER_FAIL;
            } else {
                //register success
                IOController.addUserToFile(new_register);
                notifyRegister(new_register);
                users.add(new_register);
                mess.command = ServerMessage.REGISTER_SUCCESS;
                //create file save offline message
                File file = new File("files/offline message/" + new_register.username + ".txt");
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            sendMessageToClient(mess);
        }

        public void notifyRegister(User new_register) {
            ServerMessage mess = new ServerMessage();
            mess.from = new_register;
            mess.command = ServerMessage.NEW_REGISTER;
            //traverse all users on list to write message obj notify new user 
            for (String str : onlines.keySet()) {
                sendMessageToOthers(mess, onlines.get(str));
            }
        }

        public void sendMessageToClient(ServerMessage mess) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(mess);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void sendMessageToOthers(ServerMessage mess, Socket socket) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(mess);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void connectUser(User new_login) {
            ServerMessage mess = new ServerMessage();
            if (!users.contains(new_login) || onlines.containsKey(new_login.username)) {
                //login fail
                mess.command = ServerMessage.LOGIN_FAIL;
            } else {
                User user = users.get(users.indexOf(new_login));
                if (user.password.equals(new_login.password)) {
                    //login success
                    client = user;
                    //change online status for user
                    user.online = true;
                    onlines.put(new_login.username, socket);
                    mess.command = ServerMessage.LOGIN_SUCCESS;
                    mess.users = users;
                    //send offline messages of this user for him(her), which load from file
                    mess.offlineMessages = IOController.loadOfflineMessages(user.username);
                    mess.to = user;
                    notifyOnline(user);
                } else {
                    mess.command = ServerMessage.LOGIN_FAIL;
                }
            }
            sendMessageToClient(mess);
        }

        public void notifyOnline(User new_online) {
            ServerMessage mess = new ServerMessage();
            mess.from = new_online;
            mess.command = ServerMessage.NEW_ONLINE;
            //traverse all users on list to write message obj notify new user 
            for (String str : onlines.keySet()) {
                if (!str.equals(new_online.username)) {
                    sendMessageToOthers(mess, onlines.get(str));
                }
            }
        }

        public void notifyOffline(User offline_user) {
            //remove khoi onlines
            onlines.remove(offline_user.username);
            users.get(users.indexOf(offline_user)).online = false;
            //gui cho other users
            ServerMessage mess = new ServerMessage();
            mess.from = offline_user;
            mess.command = ServerMessage.NEW_LOGOUT;
            for (String str : onlines.keySet()) {
                sendMessageToOthers(mess, onlines.get(str));
            }
        }

        private void forwardsChatMessage(ClientMessage clientMess) {
            for (User c : users) {
                if (c.equals(clientMess.to)) {
                    //find the receiver of this message and send the message to them
                    if (c.online) {
                        ServerMessage serverMess = new ServerMessage();
                        serverMess.from = clientMess.from;
                        serverMess.message = clientMess.message;
                        serverMess.command = ServerMessage.NEW_MESS;
                        sendMessageToOthers(serverMess, onlines.get(c.username));
                    } else {
                        //save offline message to files
                        IOController.addOfflineMessage(c.username, clientMess.from.username, clientMess.message);
                    }
                }
            }
        }

    }

}
