package univ.tuit.uschooltelegrambot.store;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.store.dto.BotUserDto;
import univ.tuit.uschooltelegrambot.store.repo.UserRepository;

import java.util.List;

@Component
@Repository
public class BotUserStoreLogic implements BotUserStore {

    @Autowired
    UserRepository userRepository;

    @Override
    public BotUserDto add(BotUser botUser) {
        BotUserDto save;
        if (userRepository.existsByUserId(botUser.getUserId())) {
            BotUserDto byUserId = userRepository.findByUserId(botUser.getUserId());
            Integer id = byUserId.getId();
            BeanUtils.copyProperties(botUser, byUserId);
            byUserId.setId(id);
            return userRepository.save(byUserId);
        } else
            save = new BotUserDto(botUser);
        return userRepository.save(save);

    }

/*    public BotUser update(BotUserDto botUser) {
        BotUserDto save;
        if (botUser.getUserId() != null) {
            BotUserDto byUserId = userRepository.findByUserId(botUser.getUserId());
            if (byUserId != null && botUser.getId().equals(byUserId.getId()))
                userRepository.delete(byUserId);
            save = userRepository.save(botUser);
        } else
            throw new NullPointerException("No id");
        return save.toDomain();
    }*/

    @Override
    public BotUserDto findBy(Long id) {
        return userRepository.findByUserId(id);
    }

    @Override
    public List<BotUser> getAll() {
        return BotUserDto.toDomain(userRepository.getAll());
    }


}
