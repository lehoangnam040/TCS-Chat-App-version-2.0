/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.JOptionPane;
import message.ClientMessage;
import modal.User;
import view.*;

/**
 *
 * @author Nam
 */
public class RegisterController {

    MainController controller;
    Register register;

    public RegisterController(MainController controller) {
        this.controller = controller;
        this.register = new Register(this);
        register.setVisible(true);
        register.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeRegisterForm();
            }

        });
    }

    public void register() {
        //take information in form and send to server to register
        //check username
        if (register.txtUser.getText().matches("^[a-zA-Z0-9@._-]{3,}")) {
            if (register.txtName.getText().isEmpty() || register.txtPass.getPassword().length == 0) {
                JOptionPane.showMessageDialog(register, "Information must not be blank");
            } else if (Arrays.equals(register.txtPass.getPassword(), register.txtRetypePass.getPassword())) {
                //connect
                controller.connectServer();
                User client = new User();
                client.fullname = register.txtName.getText();
                client.username = register.txtUser.getText();
                client.password = String.valueOf(register.txtPass.getPassword());
                ClientMessage clientMess = new ClientMessage();
                clientMess.from = client;
                clientMess.command = ClientMessage.REGISTER;
                controller.sendMessageToServer(clientMess);
            } else {
                JOptionPane.showMessageDialog(register, "Re-type password is not equal password, check it!!");
            }
        } else {
            JOptionPane.showMessageDialog(register, "Invalid user name!!!!");
        }
    }

    public void closeRegisterForm() {
        register.dispose();
        controller.loginControl.visibleLoginScreen();
    }

    public void showRegisterFail() {
        JOptionPane.showMessageDialog(register, "Register fail!!!");
    }
}
