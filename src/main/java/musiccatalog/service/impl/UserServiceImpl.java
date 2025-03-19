package musiccatalog.service.impl;

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
import musiccatalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PlaylistRepository playlistRepository,
                           TrackRepository trackRepository) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
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
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email name already exists");
        }
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(long id, UserUpdateDto userDto) {
        User user = getUserById(id);
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
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
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
