package univ.tuit.uschooltelegrambot.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import univ.tuit.uschooltelegrambot.domain.CompletedUser;

public interface CompletedUserRepository extends JpaRepository<CompletedUser, Long> {

    CompletedUser getByUserId(Long id);
}
