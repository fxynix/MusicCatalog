package musiccatalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.model.Genre;
import musiccatalog.model.Track;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.GenreRepository;
import musiccatalog.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final InMemoryCache cache;

    @Autowired
    public TrackService(TrackRepository trackRepository,
                        AlbumRepository albumRepository, GenreRepository genreRepository,
                        InMemoryCache cache) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
        this.cache = cache;
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    public Optional<Track> getTrackById(long id) {
        String cacheKey = "tracks_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Optional.of(((Track) cache.get(cacheKey))));
        }
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено трека с ID = " + id));
        cache.put(cacheKey, track);
        return Optional.of(track);
    }

    public List<Track> getTrackByName(String name)  {
        String cacheKey = "tracks_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (List<Track>) cache.get(cacheKey);
        }
        List<Track> tracks = trackRepository.findTracksByName(name);
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
                .orElseThrow(() ->
                        new NotFoundException("Не найдено альбома, к которому принадлежит трек"));
        track.setAlbum(album);
        List<Genre> genres = genreRepository.findAllById(trackDto.getGenresIds());
        track.setGenres(genres);
        cache.clear();
        return trackRepository.save(track);
    }

    public Track updateTrack(long id, TrackUpdateDto trackDto) {
        Track track = getTrackById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено трека с ID = " + id));
        if (trackDto.getAlbumId() != null) {
            track.setAlbum(albumRepository.findById(trackDto.getAlbumId())
                    .orElseThrow(() ->
                        new NotFoundException("Не найдено альбома, к которому принадлежит трек")));
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
                        .orElseThrow(() -> new NotFoundException("Указанный жанр не найден"));
                genre.getTracks().add(track);
                genres.add(genre);
            }
            track.setGenres(genres);
        }
        cache.clear();
        return trackRepository.save(track);

    }

    public void deleteTrack(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден трек с ID = " + id));
        trackRepository.delete(track);
        cache.clear();
    }

}
