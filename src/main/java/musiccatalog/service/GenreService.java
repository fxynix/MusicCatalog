package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.model.Genre;
import org.springframework.stereotype.Service;

@Service
public interface GenreService {
    List<Genre> getAllGenres();

    Genre getGenreById(long id);

    Genre getGenreByName(String name);

    Genre createGenre(GenreCreateDto genreDto);

    Genre updateGenre(long id, GenreUpdateDto genreDto);

    void deleteGenre(Long id);
}