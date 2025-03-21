package musiccatalog.service.impl;

import java.util.ArrayList;
import java.util.List;
import musiccatalog.dto.create.PlaylistCreateDto;
import musiccatalog.dto.update.PlaylistUpdateDto;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;
import musiccatalog.model.User;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.repository.TrackRepository;
import musiccatalog.repository.UserRepository;
import musiccatalog.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository,
                               TrackRepository trackRepository, UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    @Override
    public Playlist getPlaylistById(long id) {
        return playlistRepository.findAll().stream()
                .filter(playlist -> playlist.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Playlist> getPlaylistByName(String name)  {
        return playlistRepository.findAll().stream()
                .filter(playlist -> playlist.getName().equals(name))
                .toList();
    }

    @Override
    public Playlist createPlaylist(PlaylistCreateDto playlistDto) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistDto.getName());
        List<Track> tracks = trackRepository.findAllById(playlistDto.getTracksIds());
        playlist.setTracks(tracks);
        User author = userRepository.findById(playlistDto.getAuthorId())
                .orElseThrow(()
                        -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
        playlist.setAuthor(author);
        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist updatePlaylist(long id, PlaylistUpdateDto playlistDto) {
        Playlist playlist = getPlaylistById(id);
        if (playlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found");
        }
        List<Track> tracks = new ArrayList<>();
        if (playlistDto.getTracksIds() != null && !playlistDto.getTracksIds().isEmpty()) {
            for (Long trackId : playlistDto.getTracksIds()) {
                Track track = trackRepository.findById(trackId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Track not found"));
                track.getPlaylists().add(playlist);
                tracks.add(track);
            }
            playlist.setTracks(tracks);
        }
        if (playlistDto.getName() != null) {
            playlist.setName(playlistDto.getName());
        }
        if (playlistDto.getAuthorId() != null) {
            playlist.setAuthor(userRepository.findById(playlistDto.getAuthorId())
                .orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found")));

        }
        List<User> subscribers;
        if (playlistDto.getSubscribersIds() != null) {
            subscribers = playlistDto.getSubscribersIds().stream().map(userId ->
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            "Track not found"))).toList();
            playlist.setSubscribers(subscribers);
        }
        return playlistRepository.save(playlist);
    }

    @Override
    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }

}

