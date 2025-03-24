package musiccatalog.service;

import java.util.ArrayList;
import java.util.List;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;
import musiccatalog.model.User;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.repository.TrackRepository;
import musiccatalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final InMemoryCache cache;

    @Autowired
    public UserService(UserRepository userRepository, PlaylistRepository playlistRepository,
                       TrackRepository trackRepository, InMemoryCache cache) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
        this.cache = cache;
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        String cacheKey = "users_all";
        if (cache.containsKey(cacheKey)) {
            return (List<User>) cache.get(cacheKey);
        }
        cache.put(cacheKey, users);
        return users;
    }

    public User getUserById(long id) {
        User user = userRepository.findUserById(id);
        String cacheKey = "users_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (User) cache.get(cacheKey);
        }
        cache.put(cacheKey, user);
        return user;
    }

    public User getUserByName(String name)  {
        User user = userRepository.findUserByName(name);
        String cacheKey = "users_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (User) cache.get(cacheKey);
        }
        cache.put(cacheKey, user);
        return user;
    }

    public User createUser(UserCreateDto userDto) {
        if (userRepository.findUserByEmail(userDto.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email name already exists");
        }
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        cache.clear();
        return userRepository.save(user);
    }

    public User updateUser(long id, UserUpdateDto userDto) {
        User user = getUserById(id);
        if (userRepository.findUserByEmail(userDto.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email name already exists");
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
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
        if (userDto.getSubscribedPlaylistsIds() != null) {
            user.setPlaylistsSubscribed(playlistRepository.findAllById(
                    userDto.getSubscribedPlaylistsIds()));
        }
        List<Playlist> playlists = new ArrayList<>();
        if (userDto.getSubscribedPlaylistsIds() != null
                && !userDto.getSubscribedPlaylistsIds().isEmpty()) {
            for (Long playlistId : userDto.getSubscribedPlaylistsIds()) {
                Playlist playlist = playlistRepository.findById(playlistId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Album not found"));
                playlist.getSubscribers().add(user);
                playlists.add(playlist);
            }
            user.setPlaylistsSubscribed(playlists);
        }
        List<Track> tracks = new ArrayList<>();
        if (userDto.getLikedTracksIds() != null
                && !userDto.getLikedTracksIds().isEmpty()) {
            for (Long trackId : userDto.getLikedTracksIds()) {
                Track track = trackRepository.findById(trackId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Album not found"));
                track.getLikedByUsers().add(user);
                tracks.add(track);
            }
            user.setLikedTracks(tracks);
        }
        cache.clear();
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User was not found"));
        userRepository.delete(user);
        cache.clear();
    }

}
