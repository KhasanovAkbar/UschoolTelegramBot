package univ.tuit.uschooltelegrambot.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import univ.tuit.uschooltelegrambot.domain.Cinema;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cinema")
public class CinemaDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_sequence")
    private Long id;

    private String code;

    private String addedTime;

    private String cinemaId;

    private String videoInfo;


    public CinemaDto(Cinema cinema) {
        BeanUtils.copyProperties(cinema, this);
    }

    public Cinema toDomain() {
        Cinema cinema = new Cinema();
        BeanUtils.copyProperties(this, cinema);
        return cinema;
    }

    public static List<Cinema> toDomain(List<CinemaDto> cinemaDtos) {
        return cinemaDtos.stream().map(CinemaDto::toDomain).collect(Collectors.toList());
    }
}
