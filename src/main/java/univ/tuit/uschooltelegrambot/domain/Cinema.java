package univ.tuit.uschooltelegrambot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cinema {
    //
    private String code;

    private String addedTime;

    private String cinemaId;

    private String videoInfo;
}
