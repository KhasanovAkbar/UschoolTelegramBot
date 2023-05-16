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
public class BotUser {

    private Long userId;

    private String username;

    private String name ;

    private String surname;

    private String age ;

    private String phoneNumber ;

    private String state; // it saves button state

    private String status; //it saves this user fully register or not

    private String userStateLayer; //it saves user register layer

    private String registrationTime;

}
