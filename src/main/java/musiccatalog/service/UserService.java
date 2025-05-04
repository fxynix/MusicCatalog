package musiccatalog.service;

import java.util.List;
import java.util.Optional;
import musiccatalog.cache.InMemoryCache;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.exception.ConflictException;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.User;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final InMemoryCache cache;

    @Autowired
    public UserService(UserRepository userRepository, PlaylistRepository playlistRepository,
                       InMemoryCache cache) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.cache = cache;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(long id) {
        String cacheKey = "users_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Optional.of(((User) cache.get(cacheKey))));
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + id));
        cache.put(cacheKey, user);
        return Optional.of(user);
    }

    public User getUserByName(String name)  {
        String cacheKey = "users_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (User) cache.get(cacheKey);
        }
        User user = userRepository.findUserByName(name);
        cache.put(cacheKey, user);
        return user;
    }

    public User createUser(UserCreateDto userDto) {
        if (userRepository.findUserByEmail(userDto.getEmail()) != null) {
            throw new ConflictException("Указанный Email уже занят");
        }
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        cache.clear();
        return userRepository.save(user);
    }

    public User updateUser(long id, UserUpdateDto userDto) {
        User user = getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено пользователя с ID = " + id));
        if (userRepository.findUserByEmail(userDto.getEmail()) != null
                && userRepository.findUserByEmail(userDto.getEmail()).getId() != id) {
            throw new ConflictException("Указанный Email уже занят");
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }

        if (userDto.getCreatedPlaylistsIds() != null) {
            user.setPlaylistsCreated(playlistRepository.findAllById(
                    userDto.getCreatedPlaylistsIds()));
        }
        cache.clear();
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено пользователя с ID = " + id));
        userRepository.delete(user);
        cache.clear();
    }

}
