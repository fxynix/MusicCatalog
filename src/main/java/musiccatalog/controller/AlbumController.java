package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.get.AlbumGetDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.service.AlbumService;
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
@Tag(name = "Album Controller", description = "API для управления альбомами")
@RequestMapping("/albums")
public class AlbumController {
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    @Operation(summary = "Получить все альбомы",
            description = "Возвращает все альбомы")
    @ApiResponse(responseCode = "200", description = "Альбомы найдены успешно")
    public ResponseEntity<List<AlbumGetDto>> getAllAlbums() {
        List<Album> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums.stream()
                .map(AlbumGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить альбом по ID",
            description = "Возвращает альбом по указанному ID в базе данных")
    @ApiResponse(responseCode = "200", description = "Альбом найден успешно")
    @ApiResponse(responseCode = "404", description = "Альбом не найден")
    public ResponseEntity<AlbumGetDto> getAlbumById(
            @Parameter(description = "ID искомого альбома", example = "1")
            @PathVariable long id) {
        Album album = albumService.getAlbumById(id)
                .orElseThrow(() -> new NotFoundException("Не найден альбом с ID " + id));
        return ResponseEntity.ok(new AlbumGetDto(album));
    }

    @GetMapping(params = "name")
    @Operation(summary = "Получить альбомы по имени",
            description = "Возвращает альбомы с указанномым именем")
    @ApiResponse(responseCode = "200", description = "Альбом(-ы) найден успешно")
    @ApiResponse(responseCode = "404", description = "Альбом(-ы) не найден")
    public ResponseEntity<List<AlbumGetDto>> getAlbumByName(
            @Parameter(description = "Имя искомого альбома", example = "Камнем по голове")
            @RequestParam String name) {
        List<Album> albums = albumService.getAlbumByName(name);
        if (albums.isEmpty()) {
            throw new NotFoundException("Не найден альбом с именем: " + name);
        }
        return ResponseEntity.ok(albums.stream()
                .map(AlbumGetDto::new)
                .toList());
    }

    @GetMapping("/filter")
    @Operation(summary = "Получить альбомы по жанру",
            description = "Возвращает альбомы с указанным жанром")
    @ApiResponse(responseCode = "200", description = "Альбом(-ы) найден успешно")
    @ApiResponse(responseCode = "404", description = "Не найдено альбомов с указанным жанром")
    public ResponseEntity<List<AlbumGetDto>> getAlbumByGenreName(
            @Parameter(description = "Имя жанра", example = "Рок")
            @RequestParam String genreName) {
        List<Album> albums = albumService.getAlbumsByGenreName(genreName);
        if (albums.isEmpty()) {
            throw new NotFoundException("Не найдено альбомом с жанром: " + genreName);
        }
        return ResponseEntity.ok(albums.stream()
                .map(AlbumGetDto::new)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Создать альбом", description = "Создаёт новый альбом")
    @ApiResponse(responseCode = "200", description = "Альбом успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    public ResponseEntity<AlbumGetDto> createAlbum(@Valid @RequestBody AlbumCreateDto albumDto) {
        Album newAlbum = albumService.createAlbum(albumDto);
        return new ResponseEntity<>(new AlbumGetDto(newAlbum), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить альбом", description = "Обновляет существующий альбом по ID")
    @ApiResponse(responseCode = "200", description = "Альбом успешно обновлен")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    @ApiResponse(responseCode = "404", description = "Альбом не найден")
    public ResponseEntity<AlbumGetDto> updateAlbum(
            @Parameter(description = "ID обновляемого альбома", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AlbumUpdateDto albumDto) {
        Album updatedAlbum = albumService.updateAlbum(id, albumDto);
        return ResponseEntity.ok(new AlbumGetDto(updatedAlbum));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить альбом", description = "Удаляет альбом по ID")
    @ApiResponse(responseCode = "204", description = "Альбом успешно удален")
    @ApiResponse(responseCode = "404", description = "Альбом не найден")
    public ResponseEntity<Album> deleteAlbum(
            @Parameter(description = "ID удаляемого альбома", example = "1")
            @PathVariable Long id) {
        albumService.deleteAlbum(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
