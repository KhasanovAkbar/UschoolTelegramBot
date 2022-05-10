package univ.tuit.uschooltelegrambot.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import univ.tuit.uschooltelegrambot.domain.CompletedUser;
import univ.tuit.uschooltelegrambot.domain.Status;
import univ.tuit.uschooltelegrambot.repo.CompletedUserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Repository
public class CompletedUserCache implements Cache<CompletedUser> {

    @Autowired
    CompletedUserRepository completed;

    @Override
    public CompletedUser add(CompletedUser completedUser) {
        CompletedUser save;
        try {
            save = completed.save(completedUser);
        } catch (Exception e) {
            throw new NullPointerException("No id");
        }
        return save;
    }

    @Override
    public void remove(CompletedUser completedUser) {

    }

    @Override
    public CompletedUser update(CompletedUser completedUser) {
        return null;
    }

    @Override
    public CompletedUser findBy(Long id, Integer sequence) {
        return null;
    }

    @Override
    public CompletedUser findBy(Long id) {
        return null;
    }

    @Override
    public List<CompletedUser> getAll() {
        List<CompletedUser> all = completed.findAll();
        List<CompletedUser> completedUsers = new ArrayList<>();

        for (CompletedUser completedUser : all) {
            if (completedUser.getStatus().equals(Status.NEW.name()))
                completedUsers.add(completedUser);
        }
        return completedUsers;
    }


}
