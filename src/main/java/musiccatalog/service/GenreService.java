package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.model.Genre;
import musiccatalog.model.Track;
import musiccatalog.repository.GenreRepository;
import musiccatalog.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final TrackRepository trackRepository;
    private final InMemoryCache cache;

    @Autowired
    public GenreService(GenreRepository genreRepository, TrackRepository trackRepository,
                        InMemoryCache cache) {
        this.genreRepository = genreRepository;
        this.trackRepository = trackRepository;
        this.cache = cache;
    }

    public List<Genre> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        String cacheKey = "genres_all";
        if (cache.containsKey(cacheKey)) {
            return (List<Genre>) cache.get(cacheKey);
        }
        cache.put(cacheKey, genres);
        return genres;
    }

    public Genre getGenreById(long id) {
        Genre genre = genreRepository.findGenreById(id);
        String cacheKey = "genres_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Genre) cache.get(cacheKey);
        }
        cache.put(cacheKey, genre);
        return genre;
    }

    public Genre getGenreByName(String name)  {
        Genre genre = genreRepository.findGenreByName(name);
        String cacheKey = "genres_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (Genre) cache.get(cacheKey);
        }
        cache.put(cacheKey, genre);
        return genre;
    }

    public Genre createGenre(GenreCreateDto genreDto) {
        if (genreRepository.findGenreByName(genreDto.getName()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Genre name already exists");
        }
        Genre genre = new Genre();
        genre.setName(genreDto.getName());
        cache.clear();
        return genreRepository.save(genre);
    }

    public Genre updateGenre(long id, GenreUpdateDto genreDto) {
        Genre genre = getGenreById(id);
        if (genreRepository.findGenreByName(genreDto.getName()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Genre name already exists");
        }
        if (genre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        if (genreDto.getTracksIds() != null) {
            genre.setTracks(trackRepository.findAllById(genreDto.getTracksIds()));
        }
        if (genreDto.getName() != null) {
            genre.setName(genreDto.getName());
        }
        List<Track> tracks;
        if (genreDto.getTracksIds() != null) {
            tracks = genreDto.getTracksIds().stream().map(trackId ->
                    trackRepository.findById(trackId)
                            .orElseThrow(() ->
                                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            "Track not found"))).toList();
            genre.setTracks(tracks);
        }
        cache.clear();
        return genreRepository.save(genre);
    }

    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Genre was not found"));
        genreRepository.delete(genre);
        cache.clear();
    }

}
