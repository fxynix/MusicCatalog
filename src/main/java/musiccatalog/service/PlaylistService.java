package musiccatalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import musiccatalog.dto.create.PlaylistCreateDto;
import musiccatalog.dto.update.PlaylistUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;
import musiccatalog.model.User;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.repository.TrackRepository;
import musiccatalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return playlistRepository.findAll();
    }

    public Optional<Playlist> getPlaylistById(long id) {
        String cacheKey = "playlists_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Optional.of(((Playlist) cache.get(cacheKey))));
        }
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено плейлиста с ID " + id));
        cache.put(cacheKey, playlist);
        return Optional.of(playlist);
    }

    public List<Playlist> getPlaylistByName(String name)  {
        String cacheKey = "playlists_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (List<Playlist>) cache.get(cacheKey);
        }
        List<Playlist> playlist = playlistRepository.findPlaylistByName(name);
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
                        -> new NotFoundException("Создатель плейлиста не найден"));
        playlist.setAuthor(author);
        cache.clear();
        return playlistRepository.save(playlist);
    }

    public Playlist updatePlaylist(long id, PlaylistUpdateDto playlistDto) {
        Playlist playlist = getPlaylistById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено плейлиста с ID " + id));
        List<Track> tracks = new ArrayList<>();
        if (playlistDto.getTracksIds() != null && !playlistDto.getTracksIds().isEmpty()) {
            for (Long trackId : playlistDto.getTracksIds()) {
                Track track = trackRepository.findById(trackId)
                        .orElseThrow(() -> new NotFoundException("Трек в плейлисте не найден"));
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
                    -> new NotFoundException("Создатель плейлиста не найден")));

        }
        cache.clear();
        return playlistRepository.save(playlist);
    }

    public void deletePlaylist(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден плейлист с ID = " + id));
        playlistRepository.delete(playlist);
        cache.clear();
    }

}

