package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    List<User> getAllUsers();

    User getUserById(long id);

    User getUserByName(String name);

    User createUser(UserCreateDto userDto);

    User updateUser(long id, UserUpdateDto userDto);

    void deleteUser(Long id);
}