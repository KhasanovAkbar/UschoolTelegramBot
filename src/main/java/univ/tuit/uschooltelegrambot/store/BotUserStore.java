package univ.tuit.uschooltelegrambot.store;

import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.store.dto.BotUserDto;

import java.util.List;


public interface BotUserStore {

    BotUserDto add(BotUser botUser);

    BotUserDto findBy(Long id);

    List<BotUser> getAll();

}
