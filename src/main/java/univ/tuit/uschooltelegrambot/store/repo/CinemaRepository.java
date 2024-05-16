package univ.tuit.uschooltelegrambot.store.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.tuit.uschooltelegrambot.store.dto.CinemaDto;

public interface CinemaRepository extends JpaRepository<CinemaDto, Long> {

    CinemaDto findByCode(String code);

    CinemaDto findByCinemaId(String cinemaId);

    boolean existsByCinemaId(String fileId);

}
