package musiccatalog.controller;

import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.get.GenreGetDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.model.Genre;
import musiccatalog.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<GenreGetDto>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres.stream()
                .map(GenreGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<GenreGetDto> getGenreById(@PathVariable long id) {
        Genre genre = genreService.getGenreById(id);
        if (genre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        return ResponseEntity.ok(new GenreGetDto(genre));
    }

    @GetMapping(params = "name")
    public ResponseEntity<GenreGetDto> getGenreByName(@RequestParam String name) {
        Genre genre = genreService.getGenreByName(name);
        if (genre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre was not found");
        }
        return ResponseEntity.ok(new GenreGetDto(genre));
    }

    @PostMapping
    public ResponseEntity<GenreGetDto> createGenre(@Valid @RequestBody GenreCreateDto genreDto) {
        Genre newGenre = genreService.createGenre(genreDto);
        return new ResponseEntity<>(new GenreGetDto(newGenre), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GenreGetDto> updateGenre(@PathVariable Long id,
                                                   @Valid @RequestBody GenreUpdateDto genreDto) {
        Genre updatedGenre = genreService.updateGenre(id, genreDto);
        return ResponseEntity.ok(new GenreGetDto(updatedGenre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Genre> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}