package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.get.GenreGetDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.exception.NotFoundException;
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

@RestController
@RequestMapping("/genres")
@Tag(name = "Genre Controller", description = "API для управления жанрами")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все жанры",
            description = "Возвращает все жанры")
    @ApiResponse(responseCode = "200", description = "Жанры найдены успешно")
    public ResponseEntity<List<GenreGetDto>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres.stream()
                .map(GenreGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить жанр по ID",
            description = "Возвращает жанр по указанному ID в базе данных")
    @ApiResponse(responseCode = "200", description = "Жанр найден успешно")
    @ApiResponse(responseCode = "404", description = "Жанр не найден")
    public ResponseEntity<GenreGetDto> getGenreById(
            @Parameter(description = "ID искомого жанра", example = "1")
            @PathVariable long id) {
        Genre genre = genreService.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено жанра с ID " + id));
        return ResponseEntity.ok(new GenreGetDto(genre));
    }

    @GetMapping(params = "name")
    @Operation(summary = "Получить жанр по имени",
            description = "Возвращает жанр по указанному имени")
    @ApiResponse(responseCode = "200", description = "Жанр найден успешно")
    @ApiResponse(responseCode = "404", description = "Жанр не найден")
    public ResponseEntity<GenreGetDto> getGenreByName(
            @Parameter(description = "Имя искомого жанра", example = "Рок")
            @RequestParam String name) {
        Genre genre = genreService.getGenreByName(name);
        if (genre == null) {
            throw new NotFoundException("Не найдено такого жанра");
        }
        return ResponseEntity.ok(new GenreGetDto(genre));
    }

    @PostMapping
    @Operation(summary = "Создать нового жанра",
            description = "Создаёт новый жанр")
    @ApiResponse(responseCode = "200", description = "Жанр создан успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    public ResponseEntity<GenreGetDto> createGenre(@Valid @RequestBody GenreCreateDto genreDto) {
        Genre newGenre = genreService.createGenre(genreDto);
        return new ResponseEntity<>(new GenreGetDto(newGenre), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить жанр",
            description = "Обновить информацию о существующем жанре по его ID")
    @ApiResponse(responseCode = "200", description = "Жанр обновлён успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    @ApiResponse(responseCode = "404", description = "Жанр не найжен")
    public ResponseEntity<GenreGetDto> updateGenre(
            @Parameter(description = "ID обновляемого жанра", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody GenreUpdateDto genreDto) {
        Genre updatedGenre = genreService.updateGenre(id, genreDto);
        return ResponseEntity.ok(new GenreGetDto(updatedGenre));
    }

    @Operation(summary = "Удалить жанр",
            description = "Удалить жанр по его ID")
    @ApiResponse(responseCode = "204", description = "Жанр удалён успешно")
    @ApiResponse(responseCode = "404", description = "Жанр не найжен")
    @DeleteMapping("/{id}")
    public ResponseEntity<Genre> deleteGenre(
            @Parameter(description = "ID удаляемого жанра", example = "1")
            @PathVariable Long id) {
        genreService.deleteGenre(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}