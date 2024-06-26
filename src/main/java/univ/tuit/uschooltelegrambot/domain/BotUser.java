package univ.tuit.uschooltelegrambot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BotUser {

    private Long userId;

    private String username;

    private String name;

    private boolean hasVideo;

    private String cinemaId;

    private String status;

    private String registrationTime;

}
