package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.model.User;
import musiccatalog.repository.UserRepository;
import musiccatalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findAll().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public User getUserByName(String name)  {
        return userRepository.findAll().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User createUser(UserCreateDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setLikedArtists(userDto.getLikedArtists());
        user.setPlaylists(userDto.getPlaylists());
        user.setLikedTracks(userDto.getLikedTracks());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(long id, UserUpdateDto userDto) {
        User genre = getUserById(id);
        if (genre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (userDto.getName() != null) {
            genre.setName(userDto.getName());
        }
        if (userDto.getId() != null) {
            genre.setId(userDto.getId());
        }
        if (userDto.getEmail() != null) {
            genre.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            genre.setPassword(userDto.getPassword());
        }
        if (userDto.getLikedArtists() != null) {
            genre.setLikedArtists(userDto.getLikedArtists());
        }
        if (userDto.getPlaylists() != null) {
            genre.setPlaylists(userDto.getPlaylists());
        }
        if (userDto.getLikedTracks() != null) {
            genre.setLikedTracks(userDto.getLikedTracks());
        }
        return userRepository.save(genre);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
