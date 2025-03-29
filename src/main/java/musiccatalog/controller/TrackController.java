package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.get.TrackGetDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Track;
import musiccatalog.service.TrackService;
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
@Tag(name = "Track Controller", description = "API для управления треками")
@RequestMapping("/tracks")
public class TrackController {
    private final TrackService trackService;

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    @Operation(summary = "Получить все треки",
            description = "Возвращает все треки")
    @ApiResponse(responseCode = "200", description = "Треки найдены успешно")
    public ResponseEntity<List<TrackGetDto>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();
        return ResponseEntity.ok(tracks.stream()
                .map(TrackGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить трек по ID",
            description = "Возвращает трек по указанному ID в базе данных")
    @ApiResponse(responseCode = "200", description = "Трек найден успешно")
    @ApiResponse(responseCode = "404", description = "Трек не найден")
    public ResponseEntity<TrackGetDto> getTrackById(
            @Parameter(description = "ID искомого трека", example = "1")
            @PathVariable long id) {
        Track track = trackService.getTrackById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено трека с ID = " + id));
        return ResponseEntity.ok(new TrackGetDto(track));
    }

    @GetMapping(params = "name")
    @Operation(summary = "Получить трек по имени",
            description = "Возвращает трек по указанному имени")
    @ApiResponse(responseCode = "200", description = "Трек найден успешно")
    @ApiResponse(responseCode = "404", description = "Трек не найден")
    public ResponseEntity<List<TrackGetDto>> getTrackByName(
            @Parameter(description = "Имя искомого трека", example = "Дурак и молния")
            @RequestParam String name) {
        List<Track> tracks = trackService.getTrackByName(name);
        if (tracks.isEmpty()) {
            throw new NotFoundException("Трек с указанным именем не найден");
        }
        return ResponseEntity.ok(tracks.stream()
                .map(TrackGetDto::new)
                .toList());
    }

    @GetMapping("/filter")
    @Operation(summary = "Получить треки по исполнителю",
            description = "Возвращает треки указанномого исполнителя")
    @ApiResponse(responseCode = "200", description = "Треки найдены успешно")
    @ApiResponse(responseCode = "404", description = "Треки не найдены")
    public ResponseEntity<List<TrackGetDto>> getTracksByArtistName(
            @Parameter(description = "Имя исполнителя искомоых треков", example = "Король и шут")
            @RequestParam String artistName) {
        List<Track> tracks = trackService.getTracksByArtistName(artistName);
        if (tracks.isEmpty()) {
            throw new NotFoundException("Не найдено треков указанного исполнителя");
        }
        return ResponseEntity.ok(tracks.stream()
                .map(TrackGetDto::new)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Создать новый трек",
            description = "Создаёт новый трек")
    @ApiResponse(responseCode = "200", description = "Трек создан успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    public ResponseEntity<TrackGetDto> createTrack(@Valid @RequestBody TrackCreateDto trackDto) {
        Track newTrack = trackService.createTrack(trackDto);
        return new ResponseEntity<>(new TrackGetDto(newTrack), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить трек",
            description = "Обновляет информацию о существующем треке")
    @ApiResponse(responseCode = "200", description = "Трек обновлён успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    @ApiResponse(responseCode = "404", description = "Трек не найден")
    public ResponseEntity<TrackGetDto> updateTrack(
            @Parameter(description = "ID обновляемого трека", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TrackUpdateDto trackDto) {
        Track updatedTrack = trackService.updateTrack(id, trackDto);
        return ResponseEntity.ok(new TrackGetDto(updatedTrack));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить трек",
            description = "Удаляет трек по его ID")
    @ApiResponse(responseCode = "204", description = "Трек удалён успешно")
    @ApiResponse(responseCode = "404", description = "Трек не найден")
    public ResponseEntity<Track> deleteTrack(
            @Parameter(description = "ID удаляемого трека", example = "1")
            @PathVariable Long id) {
        trackService.deleteTrack(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
