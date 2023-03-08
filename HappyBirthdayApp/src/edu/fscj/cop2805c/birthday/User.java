// User.java
// D. Singletary
// 2/15/23
// Class which represents a user

// D. Singletary
// 2/26/23
// Added Locale property

package edu.fscj.cop2805c.birthday;

import java.time.ZonedDateTime;
import java.util.Locale;

public class User {
    private StringBuilder name;
    private String email;
    Locale locale;
    private ZonedDateTime birthday;

    public User(String fName, String lName, String email, Locale locale,
                ZonedDateTime birthday) {
        this.name = new StringBuilder();
        this.name.append(fName).append(" ").append(lName);
        this.email = email;
        this.locale = locale;
        this.birthday = birthday;
    }

    public StringBuilder getName() {
        return name;
    }

    public String getEmail() { return email; }

    public Locale getLocale() {
        return locale;
    }

    public ZonedDateTime getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
        String s = "User: " + this.name + ", Birthday: " + this.birthday;
        return s;
    }
}
