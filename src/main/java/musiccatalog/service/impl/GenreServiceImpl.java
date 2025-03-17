package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.model.Genre;
import musiccatalog.repository.GenreRepository;
import musiccatalog.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
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
        genre.setId(genreDto.getId());
        genre.setTracks(genreDto.getTracks());
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(long id, GenreUpdateDto genreDto) {
        Genre genre = getGenreById(id);
        if (genre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        if (genreDto.getTracks() != null) {
            genre.setTracks(genreDto.getTracks());
        }
        if (genreDto.getName() != null) {
            genre.setName(genreDto.getName());
        }
        if (genreDto.getId() != null) {
            genre.setId(genreDto.getId());
        }
        return genreRepository.save(genre);
    }

    @Override
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
