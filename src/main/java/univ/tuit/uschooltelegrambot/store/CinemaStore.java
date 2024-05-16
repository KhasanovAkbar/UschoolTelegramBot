package univ.tuit.uschooltelegrambot.store;

import univ.tuit.uschooltelegrambot.domain.Cinema;
import univ.tuit.uschooltelegrambot.store.dto.CinemaDto;

import java.util.List;

public interface CinemaStore{

    CinemaDto add(Cinema cinema) throws Exception;

    List<CinemaDto> getAll() throws Exception;
    CinemaDto findByCode(String code) throws Exception;

    CinemaDto findByCinemaId(String cinemaId) throws Exception;

    String removeCinema(String cinemaId) throws Exception;
}
