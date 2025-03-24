package musiccatalog.service;

import java.util.ArrayList;
import java.util.List;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.model.Genre;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.GenreRepository;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final PlaylistRepository playlistRepository;
    private final InMemoryCache cache;

    @Autowired
    public TrackService(TrackRepository trackRepository,
                        AlbumRepository albumRepository, GenreRepository genreRepository,
                        PlaylistRepository playlistRepository, InMemoryCache cache) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
        this.playlistRepository = playlistRepository;
        this.cache = cache;
    }

    public List<Track> getAllTracks() {
        List<Track> tracks = trackRepository.findAll();
        String cacheKey = "tracks_all";
        if (cache.containsKey(cacheKey)) {
            return (List<Track>) cache.get(cacheKey);
        }
        cache.put(cacheKey, tracks);
        return tracks;
    }

    public Track getTrackById(long id) {
        Track track = trackRepository.findTrackById(id);
        String cacheKey = "tracks_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Track) cache.get(cacheKey);
        }
        cache.put(cacheKey, track);
        return track;
    }

    public List<Track> getTrackByName(String name)  {
        List<Track> tracks = trackRepository.findTracksByName(name);
        String cacheKey = "tracks_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (List<Track>) cache.get(cacheKey);
        }
        cache.put(cacheKey, tracks);
        return tracks;
    }

    public List<Track> getTracksByArtistName(String artistName) {
        String cacheKey = "tracks_artist_" + artistName;
        if (cache.containsKey(cacheKey)) {
            return (List<Track>) cache.get(cacheKey);
        }
        List<Track> tracks = trackRepository.findTracksByArtistName(artistName);
        cache.put(cacheKey, tracks);
        return tracks;
    }

    public Track createTrack(TrackCreateDto trackDto) {
        Track track = new Track();
        track.setName(trackDto.getName());
        track.setDuration(trackDto.getDuration());
        Album album = albumRepository.findById(trackDto.getAlbumId())
                .orElseThrow(()
                        -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Artist was not found"));
        track.setAlbum(album);
        List<Genre> genres = genreRepository.findAllById(trackDto.getGenresIds());
        track.setGenres(genres);
        cache.clear();
        return trackRepository.save(track);
    }

    public Track updateTrack(long id, TrackUpdateDto trackDto) {
        Track track = getTrackById(id);
        if (track == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found");
        }
        if (trackDto.getAlbumId() != null) {
            track.setAlbum(albumRepository.findById(trackDto.getAlbumId())
                .orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found")));
        }
        if (trackDto.getName() != null) {
            track.setName(trackDto.getName());
        }
        if (trackDto.getDuration() != 0) {
            track.setDuration(trackDto.getDuration());
        }
        if (trackDto.getGenresIds() != null) {
            track.setGenres(genreRepository.findAllById(trackDto.getGenresIds()));
        }
        List<Genre> genres = new ArrayList<>();
        if (trackDto.getGenresIds() != null && !trackDto.getGenresIds().isEmpty()) {
            for (Long genreId : trackDto.getGenresIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Album not found"));
                genre.getTracks().add(track);
                genres.add(genre);
            }
            track.setGenres(genres);
        }
        List<Playlist> playlists;
        if (trackDto.getPlaylistsIds() != null) {
            playlists = trackDto.getPlaylistsIds().stream().map(playlistId ->
                    playlistRepository.findById(playlistId)
                            .orElseThrow(() ->
                                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            "Artist not found"))).toList();
            track.setPlaylists(playlists);
        }
        cache.clear();
        return trackRepository.save(track);

    }

    public void deleteTrack(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Track was not found"));
        trackRepository.delete(track);
        cache.clear();
    }

}
