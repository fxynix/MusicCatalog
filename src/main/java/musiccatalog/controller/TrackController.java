package musiccatalog.controller;

import java.util.List;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.get.TrackGetDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.model.Track;
import musiccatalog.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/tracks")
public class TrackController {
    private final TrackService trackService;

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public ResponseEntity<List<TrackGetDto>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();
        return ResponseEntity.ok(tracks.stream()
                .map(TrackGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<TrackGetDto> getTrackById(@PathVariable long id) {
        Track track = trackService.getTrackById(id);
        if (track == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found");
        }
        return ResponseEntity.ok(new TrackGetDto(track));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<TrackGetDto>> getTrackByName(@RequestParam String name) {
        List<Track> tracks = trackService.getTrackByName(name);
        if (tracks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No track found");
        }
        return ResponseEntity.ok(tracks.stream()
                .map(TrackGetDto::new)
                .toList());
    }

    @PostMapping
    public ResponseEntity<TrackGetDto> createTrack(@RequestBody TrackCreateDto trackDto) {
        Track newTrack = trackService.createTrack(trackDto);
        return new ResponseEntity<>(new TrackGetDto(newTrack), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackGetDto> updateTrack(@PathVariable Long id,
                                                   @RequestBody TrackUpdateDto trackDto) {
        Track updatedTrack = trackService.updateTrack(id, trackDto);
        return ResponseEntity.ok(new TrackGetDto(updatedTrack));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Track> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
