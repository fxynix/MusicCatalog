package musiccatalog.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final InMemoryCache cache;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository,
                           TrackRepository trackRepository, UserRepository userRepository,
                           InMemoryCache cache) {
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.cache = cache;
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = playlistRepository.findAll();
        String cacheKey = "playlists_all";
        if (cache.containsKey(cacheKey)) {
            return (List<Playlist>) cache.get(cacheKey);
        }
        cache.put(cacheKey, playlists);
        return playlists;
    }

    public Playlist getPlaylistById(long id) {
        Playlist playlist = playlistRepository.findPlaylistById(id);
        String cacheKey = "playlists_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Playlist) cache.get(cacheKey);
        }
        cache.put(cacheKey, playlist);
        return playlist;
    }

    public List<Playlist> getPlaylistByName(String name)  {
        List<Playlist> playlist = playlistRepository.findPlaylistByName(name);
        String cacheKey = "playlists_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (List<Playlist>) cache.get(cacheKey);
        }
        cache.put(cacheKey, playlist);
        return playlist;
    }

    public Playlist createPlaylist(PlaylistCreateDto playlistDto) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistDto.getName());
        List<Track> tracks = trackRepository.findAllById(playlistDto.getTracksIds());
        playlist.setTracks(tracks);
        User author = userRepository.findById(playlistDto.getAuthorId())
                .orElseThrow(()
                        -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
        playlist.setAuthor(author);
        cache.clear();
        return playlistRepository.save(playlist);
    }

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
        cache.clear();
        return playlistRepository.save(playlist);
    }

    public void deletePlaylist(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Playlist was not found"));
        playlistRepository.delete(playlist);
        cache.clear();
    }

}

