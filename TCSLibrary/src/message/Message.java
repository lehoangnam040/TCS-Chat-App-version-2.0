/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import modal.User;

/**
 *
 * @author Nam
 */
public abstract class Message implements Serializable {

    public User from;
    public User to;
    public String message;
    public int command;
    public HashMap<String, ArrayList<String>> offlineMessages;

}
