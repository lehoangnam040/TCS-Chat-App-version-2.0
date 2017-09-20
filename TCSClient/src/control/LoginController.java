/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import javax.swing.JOptionPane;
import message.ClientMessage;
import modal.User;
import view.*;

/**
 *
 * @author Nam
 */
public class LoginController {

    MainController mainControl;
    Login login;

    public LoginController(MainController controller) {
        this.mainControl = controller;
        this.login = new Login(this);
        login.setVisible(true);
    }

    public void login() {
        //connect
        mainControl.connectServer();
        //take infomation from login form
        User user = new User();
        user.username = login.txtUser.getText();
        user.password = String.valueOf(login.txtPassword.getPassword());
        //send a message of user info to login
        ClientMessage clientMess = new ClientMessage();
        clientMess.from = user;
        clientMess.command = ClientMessage.LOG_IN;
        mainControl.sendMessageToServer(clientMess);
    }

    public void visibleLoginScreen() {
        login.setVisible(true);
    }

    public void openRegisterForm() {
        mainControl.registerControl = new RegisterController(mainControl);
        login.setVisible(false);
    }

    public void openMainScreen() {
        mainControl.mainScreenControl = new MainScreenController(mainControl);
        login.setVisible(false);
    }

    public void showLoginFail() {
        JOptionPane.showMessageDialog(login, "Login fail");
    }
}
