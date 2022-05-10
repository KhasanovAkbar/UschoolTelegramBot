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
@Table(name = "completedUser")
public class CompletedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sequence")
    private Integer id;

    private Long userId;

    private String username;

    private String name;

    private String surname;

    private String age;

    private String phoneNumber;

    private String status;

    private String registrationTime;
}
