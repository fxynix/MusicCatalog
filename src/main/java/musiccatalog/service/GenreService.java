package musiccatalog.service;

import java.util.List;
import java.util.Optional;
import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.exception.ConflictException;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Genre;
import musiccatalog.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final InMemoryCache cache;

    @Autowired
    public GenreService(GenreRepository genreRepository, InMemoryCache cache) {
        this.genreRepository = genreRepository;
        this.cache = cache;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Optional<Genre> getGenreById(long id) {
        String cacheKey = "genres_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Optional.of(((Genre) cache.get(cacheKey))));
        }
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден жанр с ID " + id));
        cache.put(cacheKey, genre);
        return Optional.of(genre);
    }

    public Genre getGenreByName(String name)  {
        String cacheKey = "genres_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (Genre) cache.get(cacheKey);
        }
        Genre genre = genreRepository.findGenreByName(name);
        cache.put(cacheKey, genre);
        return genre;
    }

    public Genre createGenre(GenreCreateDto genreDto) {
        if (genreRepository.findGenreByName(genreDto.getName()) != null) {
            throw new ConflictException("Такой жанр уже существует");
        }
        Genre genre = new Genre();
        genre.setName(genreDto.getName());
        cache.clear();
        return genreRepository.save(genre);
    }

    public Genre updateGenre(long id, GenreUpdateDto genreDto) {
        Genre genre = getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено жанра с ID " + id));
        if (genreRepository.findGenreByName(genreDto.getName()) != null
                && genreRepository.findGenreByName(genreDto.getName()).getId() != id) {
            throw new ConflictException("Такой жанр уже существует");
        }
        if (genreDto.getName() != null) {
            genre.setName(genreDto.getName());
        }
        cache.clear();
        return genreRepository.save(genre);
    }

    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено жанра с ID " + id));
        genreRepository.delete(genre);
        cache.clear();
    }

}
