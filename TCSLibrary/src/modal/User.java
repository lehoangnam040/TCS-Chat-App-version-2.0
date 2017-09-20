/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modal;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Nam
 */
public class User implements Serializable {

    public String fullname;
    public String username;
    public String password;
    public boolean online;

    public User(String username) {
        this.username = username;
    }

    public User() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.username, other.username);
    }

    @Override
    public String toString() {
        return username + " - " + fullname + " - " + password;
    }

}
