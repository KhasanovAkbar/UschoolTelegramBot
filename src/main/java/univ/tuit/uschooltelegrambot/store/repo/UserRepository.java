package univ.tuit.uschooltelegrambot.store.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import univ.tuit.uschooltelegrambot.store.dto.BotUserDto;

import java.util.List;

public interface UserRepository extends JpaRepository<BotUserDto, Long> {

    BotUserDto findByUserId(Long id);

    @Query(value = "select u from BotUserDto u order by u.id desc ")
    List<BotUserDto> getAll();

    boolean existsByUserId(Long id);


}
