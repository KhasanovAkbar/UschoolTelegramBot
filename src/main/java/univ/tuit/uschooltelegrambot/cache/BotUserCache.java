package univ.tuit.uschooltelegrambot.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.repo.UserRepository;

import java.util.*;

@Component
@Repository
public class BotUserCache implements Cache<BotUser> {

    @Autowired
    UserRepository users;


    @Override
    public BotUser add(BotUser botUser) {
        BotUser save;
        if (botUser.getUserId() != null) {
            save = users.save(botUser);
        } else
            throw new NullPointerException("No id");
        return save;
    }

    @Override
    public void remove(BotUser botUser) {
        users.delete(botUser);
    }

    @Override
    public BotUser update(BotUser botUser) {
        BotUser save;
        if (botUser.getUserId() != null) {
            BotUser byUserId = users.findByUserIdAndId(botUser.getUserId(), botUser.getId());
            if (byUserId != null && botUser.getId().equals(byUserId.getId()))
                users.delete(byUserId);
            save = users.save(botUser);
        } else
            throw new NullPointerException("No id");
        return save;
    }

    @Override
    public BotUser findBy(Long id, Integer sequence) {
        return users.findByUserIdAndId(id, sequence);
    }

    @Override
    public BotUser findBy(Long id) {
        return null;
    }

    @Override
    public List<BotUser> getAll() {
        return users.getAll();
    }


}
