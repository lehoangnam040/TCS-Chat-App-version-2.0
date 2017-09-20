/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import message.ClientMessage;
import modal.User;
import view.MainScreen;

/**
 *
 * @author Nam
 */
public class MainScreenController {

    MainController mainControl;
    MainScreen screen;
    DefaultListModel model;

    public MainScreenController(MainController controller) {
        this.mainControl = controller;
        this.screen = new MainScreen(this);
        screen.setVisible(true);
        screen.lblUser.setText("Login as: " + controller.user.username);
        screen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }

        });
    }

    public void closeMainScreen() {
        screen.dispose();
        mainControl.loginControl.visibleLoginScreen();
    }

    public void displayUsersList(ArrayList<User> users) {
        //traverse all user ,set online status for them and add to JList
        model = new DefaultListModel();
        for (User user : users) {
            if (!user.equals(mainControl.user)) {
                //find others user to add
                model.addElement(user.username + (user.online ? " (online)" : ""));
            }
        }
        this.screen.listUsers.setModel(model);
    }

    public void addNewRegister(String name) {
        //add new line of new username to JList
        model.addElement(name);
    }

    public void setStatusOnline(String name) {
        //find user by index and set online status
        model.set(model.indexOf(name), name + " (online)");
    }

    public void setOfflineStatus(String offline_name) {
        //find user by index and remove online status
        model.set(model.indexOf(offline_name + " (online)"), offline_name);
    }

    public void logout() {
        //send mess
        ClientMessage mess = new ClientMessage();
        mess.from = mainControl.user;
        mess.command = ClientMessage.LOG_OUT;
        //if have offline message that not read yet, send it to server to store
        mess.offlineMessages = mainControl.offlineMessages;
        mainControl.sendMessageToServer(mess);
        //off listener
        mainControl.running = false;
        closeMainScreen();
    }

    public void chat() {
        String str = (String) screen.listUsers.getSelectedValue();
        if (str == null) {
            JOptionPane.showMessageDialog(screen, "You don't choose anyone to chat with!!!");
        } else {
            str = str.split(" ")[0];
            //create a chat dialog
            createChatDialog(str);
        }
    }

    public void createChatDialog(String username) {
        //if already have chat dialog with username then focus, other create new
        if (mainControl.chats.keySet().contains(username)) {
            //focus
            mainControl.chats.get(username).chatScreen.requestFocus();
        } else {
            //create
            ChatController chatControl = new ChatController(mainControl, username);
            mainControl.chats.put(username, chatControl);
            //read offline message
            if (mainControl.offlineMessages != null && mainControl.offlineMessages.containsKey(username)) {
                chatControl.loadOfflineMessages(mainControl.offlineMessages.get(username));
                countNumberOfflineMessage(mainControl.offlineMessages);
            }
        }
    }

    public void receiveMessage(String username, String message) {
        createChatDialog(username);
        mainControl.chats.get(username).chatScreen.txtChatHistory.append(username + ":\n\t" + message + "\n");
    }

    public void showOfflineMessagesScreen() {
        mainControl.offlineMessagesControl = new OfflineMessagesController(mainControl);
    }

    void getOfflineMessages(HashMap<String, ArrayList<String>> offlineMessages) {
        if (!offlineMessages.isEmpty()) {
            mainControl.offlineMessages = offlineMessages;
            countNumberOfflineMessage(offlineMessages);
        }
    }

    public void countNumberOfflineMessage(HashMap<String, ArrayList<String>> offlineMessages) {
        //count and display number offline messages
        int number_off_mess = 0;
        //traverse all others user who have offline messages
        for (String str : offlineMessages.keySet()) {
            //count number of message
            number_off_mess += offlineMessages.get(str).size();
        }
        screen.btnOffMess.setText("Offline Message " + (number_off_mess != 0 ? "(" + number_off_mess + ")" : ""));
    }

}
