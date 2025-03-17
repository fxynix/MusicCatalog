package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.model.Genre;
import musiccatalog.model.Track;
import musiccatalog.repository.GenreRepository;
import musiccatalog.repository.TrackRepository;
import musiccatalog.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository, TrackRepository trackRepository) {
        this.genreRepository = genreRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Genre getGenreById(long id) {
        return genreRepository.findAll().stream()
                .filter(genre -> genre.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Genre getGenreByName(String name)  {
        return genreRepository.findAll().stream()
                .filter(genre -> genre.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Genre createGenre(GenreCreateDto genreDto) {
        Genre genre = new Genre();
        genre.setName(genreDto.getName());
        List<Track> tracks = trackRepository.findAllById(genreDto.getTracksIds());
        genre.setTracks(tracks);
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(long id, GenreUpdateDto genreDto) {
        Genre genre = getGenreById(id);
        if (genre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        if (genreDto.getTracksIds() != null) {
            genre.setTracks(trackRepository.findAllById(genreDto.getTracksIds()));
        }
        if (genreDto.getName() != null) {
            genre.setName(genreDto.getName());
        }
        return genreRepository.save(genre);
    }

    @Override
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }

}
