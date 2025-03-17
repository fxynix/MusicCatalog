package musiccatalog.controller;

import java.util.List;
import musiccatalog.dto.create.PlaylistCreateDto;
import musiccatalog.dto.get.PlaylistGetDto;
import musiccatalog.dto.update.PlaylistUpdateDto;
import musiccatalog.model.Playlist;
import musiccatalog.service.PlaylistService;
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
@RequestMapping("/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<List<PlaylistGetDto>> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(playlists.stream()
                .map(PlaylistGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<PlaylistGetDto> getPlaylistById(@PathVariable long id) {
        Playlist playlist = playlistService.getPlaylistById(id);
        if (playlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found");
        }
        return ResponseEntity.ok(new PlaylistGetDto(playlist));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<PlaylistGetDto>> getPlaylistByName(@RequestParam String name) {
        List<Playlist> playlists = playlistService.getPlaylistByName(name);
        if (playlists.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No playlist found");
        }
        return ResponseEntity.ok(playlists.stream()
                .map(PlaylistGetDto::new)
                .toList());
    }

    @PostMapping
    public ResponseEntity<PlaylistGetDto> createPlaylist(
            @RequestBody PlaylistCreateDto playlistDto) {
        Playlist newPlaylist = playlistService.createPlaylist(playlistDto);
        return new ResponseEntity<>(new PlaylistGetDto(newPlaylist), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistGetDto> updatePlaylist(@PathVariable Long id,
                                                      @RequestBody PlaylistUpdateDto playlistDto) {
        Playlist updatedPlaylist = playlistService.updatePlaylist(id, playlistDto);
        return ResponseEntity.ok(new PlaylistGetDto(updatedPlaylist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Playlist> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
