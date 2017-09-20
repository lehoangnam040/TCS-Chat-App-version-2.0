/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.Serializable;
import java.util.ArrayList;
import modal.User;

/**
 *
 * @author Nam
 */
public class ServerMessage extends Message implements Serializable {

    //message send from server to client
    public ArrayList<User> users;
    public static final int REGISTER_SUCCESS = -1;
    public static final int REGISTER_FAIL = -2;
    public static final int NEW_REGISTER = -3;
    public static final int LOGIN_SUCCESS = -4;
    public static final int LOGIN_FAIL = -5;
    public static final int NEW_ONLINE = -6;
    public static final int NEW_LOGOUT = -7;
    public static final int NEW_MESS = -8;
}
