package univ.tuit.uschooltelegrambot.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import univ.tuit.uschooltelegrambot.domain.BotUser;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "botUser")
public class BotUserDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sequence")
    private Integer id;

    private Long userId;

    private String username = "test";

    private String name = "name";

    private String status;

    private boolean hasVideo;

    private String cinemaId;

    private String registrationTime;

    public BotUserDto(BotUser botUser) {
        BeanUtils.copyProperties(botUser, this);
    }

    public BotUser toDomain() {
        BotUser botUser = new BotUser();
        BeanUtils.copyProperties(this, botUser);
        return botUser;
    }

    public static List<BotUser> toDomain(List<BotUserDto> botUserDtos) {
        return botUserDtos.stream().map(BotUserDto::toDomain).collect(Collectors.toList());
    }
}
