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
import view.OfflineMessages;

/**
 *
 * @author Nam
 */
public class OfflineMessagesController {

    MainController mainControl;
    OfflineMessages offlineMessagesScreen;
    DefaultListModel model;

    public OfflineMessagesController(MainController mainControl) {
        this.mainControl = mainControl;
        this.offlineMessagesScreen = new OfflineMessages(mainControl.mainScreenControl.screen, false, this);
        offlineMessagesScreen.setVisible(true);
        offlineMessagesScreen.lblUser.setText("Login as: " + mainControl.user.username);
        displayOfflineMessagesList(mainControl.offlineMessages);
        offlineMessagesScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }

        });
    }

    private void displayOfflineMessagesList(HashMap<String, ArrayList<String>> users) {
        //add all user have offline message and add to JList
        if (users != null) {
            model = new DefaultListModel();
            //traverse all users
            for (String username : users.keySet()) {
                model.addElement(username + " (" + users.get(username).size() + ")");
            }
            offlineMessagesScreen.listUsers.setModel(model);
        }
    }

    public void closeDialog() {
        offlineMessagesScreen.dispose();
    }

    public void readOfflineMessage() {
        String username = (String) offlineMessagesScreen.listUsers.getSelectedValue();
        if (username == null) {
            JOptionPane.showMessageDialog(offlineMessagesScreen, "You don't choose anyone to read offline message!!!");
        } else {
            model.remove(model.indexOf(username));
            username = username.split(" ")[0];
            ChatController chatControl = new ChatController(mainControl, username);
            mainControl.chats.put(username, chatControl);
            chatControl.loadOfflineMessages(mainControl.offlineMessages.get(username));
            mainControl.mainScreenControl.countNumberOfflineMessage(mainControl.offlineMessages);
        }
    }
}
