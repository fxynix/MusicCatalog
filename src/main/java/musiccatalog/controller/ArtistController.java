package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.get.ArtistGetDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Artist;
import musiccatalog.service.ArtistService;
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
@Tag(name = "Artist Controller", description = "API для управления исполнителями")
@RequestMapping("/artists")
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    @Operation(summary = "Получить всех исполнителей",
            description = "Возвращает всех исполнителей")
    @ApiResponse(responseCode = "200", description = "Исполнители найдены успешно")
    public ResponseEntity<List<ArtistGetDto>> getAllArtists() {
        List<Artist> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists.stream()
                .map(ArtistGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить исполнителя по ID",
            description = "Возвращает исполнителя по указанному ID в базе данных")
    @ApiResponse(responseCode = "200", description = "Исполнитель найден успешно")
    @ApiResponse(responseCode = "404", description = "Исполнитель не найден")
    public ResponseEntity<ArtistGetDto> getArtistById(
            @Parameter(description = "ID искомого исполнителя", example = "1")
            @PathVariable long id) {
        Artist artist = artistService.getArtistById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено исполнителя с ID " + id));
        return ResponseEntity.ok(new ArtistGetDto(artist));
    }

    @GetMapping(params = "name")
    @Operation(summary = "Получить исполнителя по имени",
            description = "Возвращает исполнителя по указанному имени")
    @ApiResponse(responseCode = "200", description = "Исполнитель найден успешно")
    @ApiResponse(responseCode = "404", description = "Исполнитель не найден")
    public ResponseEntity<ArtistGetDto> getArtistByName(
            @Parameter(description = "Имя искомого исполнителя", example = "Король и шут")
            @RequestParam String name) {
        Artist artist = artistService.getArtistByName(name);
        if (artist == null) {
            throw new NotFoundException("Исполнителя с указанным именем не найдено");
        }
        return ResponseEntity.ok(new ArtistGetDto(artist));
    }

    @PostMapping
    @Operation(summary = "Создать нового исполнителя",
            description = "Создаёт нового исполнителя")
    @ApiResponse(responseCode = "200", description = "Исполнитель создан успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    public ResponseEntity<ArtistGetDto> createArtist(
            @Valid @RequestBody ArtistCreateDto artistDto) {
        Artist newArtist = artistService.createArtist(artistDto);
        return new ResponseEntity<>(new ArtistGetDto(newArtist), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить существующего исполнителя",
            description = "Обновляет информацию об исполнителе по его ID")
    @ApiResponse(responseCode = "200", description = "Исполнитель обновлён успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    @ApiResponse(responseCode = "404", description = "Исполнитель не найден")
    public ResponseEntity<ArtistGetDto> updateArtist(
            @Parameter(description = "ID обновляемого исполнителя", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ArtistUpdateDto artistDto) {
        Artist updatedArtist = artistService.updateArtist(id, artistDto);
        return ResponseEntity.ok(new ArtistGetDto(updatedArtist));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить исполнителя",
            description = "Удаляет исполнителя по его ID")
    @ApiResponse(responseCode = "204", description = "Исполнитель удалён успешно")
    @ApiResponse(responseCode = "404", description = "Исполнитель не найден")
    public ResponseEntity<Artist> deleteArtist(
            @Parameter(description = "ID удаляемого исполнителя", example = "1")
            @PathVariable Long id) {
        artistService.deleteArtist(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}