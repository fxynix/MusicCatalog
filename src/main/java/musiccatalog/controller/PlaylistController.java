package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.PlaylistCreateDto;
import musiccatalog.dto.get.PlaylistGetDto;
import musiccatalog.dto.update.PlaylistUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Playlist;
import musiccatalog.service.PlaylistService;
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
@RequestMapping("/playlists")
@Tag(name = "Playlist Controller", description = "API для управления плейлистами")
public class PlaylistController {
    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все плейлиста",
            description = "Возвращает все плейлисты")
    @ApiResponse(responseCode = "200", description = "Плейлисты найдены успешно")
    public ResponseEntity<List<PlaylistGetDto>> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(playlists.stream()
                .map(PlaylistGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить плейлист по ID",
            description = "Возвращает плейлист по указанному ID в базе данных")
    @ApiResponse(responseCode = "200", description = "Плейлист найден успешно")
    @ApiResponse(responseCode = "404", description = "Плейлист не найден")
    public ResponseEntity<PlaylistGetDto> getPlaylistById(
            @Parameter(description = "ID искомого плейлиста", example = "1")
            @PathVariable long id) {
        Playlist playlist = playlistService.getPlaylistById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено плейлиста с ID " + id));
        return ResponseEntity.ok(new PlaylistGetDto(playlist));
    }

    @GetMapping(params = "name")
    @Operation(summary = "Получить плейлист по имени",
            description = "Возвращает плейлист по указанному имени")
    @ApiResponse(responseCode = "200", description = "Плейлист найден успешно")
    @ApiResponse(responseCode = "404", description = "Плейлист не найден")
    public ResponseEntity<List<PlaylistGetDto>> getPlaylistByName(
            @Parameter(description = "Имя искомого плейлиста", example = "MyPlaylist")
            @RequestParam String name) {
        List<Playlist> playlists = playlistService.getPlaylistByName(name);
        if (playlists.isEmpty()) {
            throw new NotFoundException("Плейлиста с указанным именем не найдено");
        }
        return ResponseEntity.ok(playlists.stream()
                .map(PlaylistGetDto::new)
                .toList());
    }

    @GetMapping(params = "authorId")
    @Operation(summary = "Получить плейлист по id автора",
            description = "Возвращает плейлист по id автора")
    @ApiResponse(responseCode = "200", description = "Плейлист найден успешно")
    @ApiResponse(responseCode = "404", description = "Плейлистов не найдено")
    public ResponseEntity<List<PlaylistGetDto>> getPlaylistByAuthor(
            @Parameter(description = "ID автора плейлиста", example = "1")
            @RequestParam Long authorId) {
        List<Playlist> playlists = playlistService.getPlaylistByAuthor(authorId);
        if (playlists.isEmpty()) {
            throw new NotFoundException("Плейлистов указанного автора не найдено");
        }
        return ResponseEntity.ok(playlists.stream()
                .map(PlaylistGetDto::new)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Создать новый плейлист",
            description = "Создаёт новый плейлист")
    @ApiResponse(responseCode = "201", description = "Плейлист создан успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    public ResponseEntity<PlaylistGetDto> createPlaylist(
            @Valid @RequestBody PlaylistCreateDto playlistDto) {
        Playlist newPlaylist = playlistService.createPlaylist(playlistDto);
        return new ResponseEntity<>(new PlaylistGetDto(newPlaylist), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить плейлист",
            description = "Обновляет информацию о существующем плейлисте")
    @ApiResponse(responseCode = "200", description = "Плейлист обновлён успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    @ApiResponse(responseCode = "404", description = "Плейлист не найден")
    public ResponseEntity<PlaylistGetDto> updatePlaylist(
            @Parameter(description = "ID обновляемого плейлиста", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PlaylistUpdateDto playlistDto) {
        Playlist updatedPlaylist = playlistService.updatePlaylist(id, playlistDto);
        return ResponseEntity.ok(new PlaylistGetDto(updatedPlaylist));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить плейлист",
            description = "Удаляет плейлист")
    @ApiResponse(responseCode = "204", description = "Плейлист удалён успешно")
    @ApiResponse(responseCode = "404", description = "Плейлист не найден")
    public ResponseEntity<Playlist> deletePlaylist(
            @Parameter(description = "ID удаляемого плейлиста", example = "1")
            @PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
