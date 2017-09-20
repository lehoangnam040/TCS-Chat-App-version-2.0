/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import message.ClientMessage;
import modal.User;
import view.Chat;

/**
 *
 * @author Nam
 */
public class ChatController {

    MainController mainControl;
    Chat chatScreen;
    String chatWith;

    public ChatController(MainController mainControl, String chatWith) {
        this.mainControl = mainControl;
        this.chatWith = chatWith;
        this.chatScreen = new Chat(mainControl.mainScreenControl.screen, false, this);
        chatScreen.setVisible(true);
        chatScreen.lblStatus.setText(mainControl.user.username + " chat to " + chatWith);
        chatScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeChatScreen();
            }
        });
        chatScreen.txtChat.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getModifiers() == KeyEvent.SHIFT_MASK && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    newLine();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendChatMessage();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (chatScreen.txtChat.getText().equals("\n")) {
                        chatScreen.txtChat.setText("");
                    }
                }
            }
        });
    }

    public void closeChatScreen() {
        chatScreen.dispose();
        mainControl.chats.remove(chatWith);
    }

    public void sendChatMessage() {
        //check if message is not empty and write message obj to send to server
        if (!chatScreen.txtChat.getText().equals("")) {
            //if text from chat text input is not empty then send
            chatScreen.txtChatHistory.append(mainControl.user.username + ":\n");
            String[] messages = chatScreen.txtChat.getText().split("\n");
            for (String message : messages) {
                ClientMessage mess = new ClientMessage();
                mess.message = message;
                mess.from = mainControl.user;
                mess.to = new User(chatWith);
                mess.command = ClientMessage.CHAT;
                mainControl.sendMessageToServer(mess);
                chatScreen.txtChatHistory.append("\t" + message + "\n");
            }

        }
        chatScreen.txtChat.setText("");
    }

    public void loadOfflineMessages(ArrayList<String> messages) {
        chatScreen.txtChatHistory.append(chatWith + ":\n\t");
        for (String message : messages) {
            chatScreen.txtChatHistory.append(message + "\n\t");
        }
        mainControl.offlineMessages.remove(chatWith);
    }

    public void newLine() {
        chatScreen.txtChat.append("\n");
    }

    void setOfflineStatus() {
        chatScreen.lblOffline.setText("(offline)");
    }

    void setOnlineStatus() {
        chatScreen.lblOffline.setText("");
    }

}
