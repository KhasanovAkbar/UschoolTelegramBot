package univ.tuit.uschooltelegrambot.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import univ.tuit.uschooltelegrambot.domain.Price;

public interface PriceRepository extends JpaRepository<Price, Integer> {
}
