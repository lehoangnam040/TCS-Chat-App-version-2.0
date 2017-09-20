/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import modal.User;

/**
 *
 * @author Nam
 */
public class IOController {

    public static void addUserToFile(User new_user) {
        try {
            Files.write(Paths.get("files/users.txt"), (new_user.toString() + "\r\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.out.println("error when add new user to file");
        }
    }

    public static ArrayList<User> loadUsersListFromFile() {
        ArrayList<User> users = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("files/users.txt"));
            String str = reader.readLine();
            while (str != null) {
                String[] info = str.split(" - ");
                User user = new User();
                user.username = info[0];
                user.fullname = info[1];
                user.password = info[2];
                user.online = false;
                users.add(user);
                str = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("can't found file users.txt to load");
        } catch (IOException ex) {
            System.out.println("error when load users info");
        }
        return users;
    }

    public static void addOfflineMessage(String user, String from_user, String message) {
        if (!message.isEmpty()) {
            try {
                Files.write(Paths.get("files/offline message/" + user + ".txt"),
                        (from_user + " : " + message + "\r\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException ex) {
                System.out.println("error when write offline messages to file");
            }
        }
    }

    public static HashMap<String, ArrayList<String>> loadOfflineMessages(String username) {
        HashMap<String, ArrayList<String>> off_mess = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("files/offline message/" + username + ".txt"));
            String str = reader.readLine();
            while (str != null) {
                String[] info = str.split(" : ", 2);
                if (off_mess.containsKey(info[0])) {
                    //if user info[0] already have message then add more
                    off_mess.get(info[0]).add(info[1]);
                } else {
                    ArrayList<String> messages = new ArrayList<>();
                    if (info[1] != null) {
                        messages.add(info[1]);
                    }
                    off_mess.put(info[0], messages);
                }
                str = reader.readLine();
            }
        } catch (Exception ex) {
            System.out.println("error when load offline messages from file");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(IOController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return off_mess;
    }

    public static void updateOfflineMessages(String username, HashMap<String, ArrayList<String>> off_mess) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("files/offline message/" + username + ".txt"));
            if (off_mess != null) {
                for (String user : off_mess.keySet()) {
                    ArrayList<String> messages = off_mess.get(user);
                    for (String message : messages) {
                        writer.write(user + " : " + message + "\n");
                    }
                }
            } else {
                writer.write("");
            }
        } catch (IOException ex) {
            Logger.getLogger(IOController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(IOController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
