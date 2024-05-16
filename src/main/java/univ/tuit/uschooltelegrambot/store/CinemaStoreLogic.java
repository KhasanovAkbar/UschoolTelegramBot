package univ.tuit.uschooltelegrambot.store;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import univ.tuit.uschooltelegrambot.domain.Cinema;
import univ.tuit.uschooltelegrambot.store.dto.CinemaDto;
import univ.tuit.uschooltelegrambot.store.repo.CinemaRepository;

import java.util.List;

@Component
@Repository
public class CinemaStoreLogic implements CinemaStore {

    @Autowired
    CinemaRepository cinemaRepository;

    @Override
    public CinemaDto add(Cinema cinema) throws Exception {
        if (cinemaRepository.existsByCinemaId(cinema.getCinemaId())) {
            CinemaDto byCode = cinemaRepository.findByCinemaId(cinema.getCinemaId());
            Long id = byCode.getId();
            BeanUtils.copyProperties(cinema, byCode);
            byCode.setId(id);
            return cinemaRepository.save(byCode);
        }
        CinemaDto cinemaDto = new CinemaDto(cinema);
        return cinemaRepository.save(cinemaDto);
    }

    @Override
    public List<CinemaDto> getAll() throws Exception {
        return cinemaRepository.findAll();
    }

    @Override
    public CinemaDto findByCode(String code) throws Exception {
        //
        return cinemaRepository.findByCode(code);
    }

    @Override
    public CinemaDto findByCinemaId(String cinemaId) throws Exception {
        //
        return cinemaRepository.findByCinemaId(cinemaId);

    }

    @Override
    public String removeCinema(String cinemaId) throws Exception {
        //
        CinemaDto byCode = cinemaRepository.findByCode(cinemaId);
        if (byCode == null)
            return "Bunday ID li kino topilmadi";
        else {
            cinemaRepository.delete(byCode);
            return "Cinema removed successfully.";
        }

    }

}
