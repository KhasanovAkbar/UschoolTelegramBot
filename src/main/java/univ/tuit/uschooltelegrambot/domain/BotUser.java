package univ.tuit.uschooltelegrambot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "botUser")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sequence")
    private Integer id;

    private Long userId;

    private String username;

    private String name = "Register1";

    private boolean isName = false;

    private String surname = "Register";

    private boolean isSurname = false;

    private String age = "Register";

    private boolean isAge = false;

    private String phoneNumber = "Register";

    private boolean isPhoneNumber = true;

    private String state;

    private String registrationTime;

    public boolean isName() {
        return isName;
    }

    public void setIsName(boolean name) {
        isName = name;
    }

    public boolean isSurname() {
        return isSurname;
    }

    public void setIsSurname(boolean surname) {
        isSurname = surname;
    }

    public boolean isAge() {
        return isAge;
    }

    public void setIsAge(boolean age) {
        isAge = age;
    }

    public boolean isPhoneNumber() {
        return isPhoneNumber;
    }

    public void setIsPhoneNumber(boolean phoneNumber) {
        isPhoneNumber = phoneNumber;
    }
}
