package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.model.Artist;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;
import musiccatalog.model.User;
import musiccatalog.repository.ArtistRepository;
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
    private final ArtistRepository artistRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ArtistRepository artistRepository,
                           PlaylistRepository playlistRepository, TrackRepository trackRepository) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
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
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        List<Artist> likedArtists = artistRepository.findAllById(userDto.getLikedArtistsIds());
        user.setLikedArtists(likedArtists);
        List<Playlist> playlists = playlistRepository.findAllById(userDto.getPlaylistsIds());
        user.setPlaylists(playlists);
        List<Track> likedTracks = trackRepository.findAllById(userDto.getLikedTracksIds());
        user.setLikedTracks(likedTracks);
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
        if (userDto.getEmail() != null) {
            genre.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            genre.setPassword(userDto.getPassword());
        }
        if (userDto.getLikedArtistsIds() != null) {
            genre.setLikedArtists(artistRepository.findAllById(userDto.getLikedArtistsIds()));
        }
        if (userDto.getPlaylistsIds() != null) {
            genre.setPlaylists(playlistRepository.findAllById(userDto.getPlaylistsIds()));
        }
        if (userDto.getLikedTracksIds() != null) {
            genre.setLikedTracks(trackRepository.findAllById(userDto.getLikedTracksIds()));
        }
        return userRepository.save(genre);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
