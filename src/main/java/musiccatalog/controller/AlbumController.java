package musiccatalog.controller;

import java.util.List;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.get.AlbumGetDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.service.AlbumService;
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
@RequestMapping("/albums")
public class AlbumController {
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<AlbumGetDto>> getAllAlbums() {
        List<Album> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums.stream()
                .map(AlbumGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<AlbumGetDto> getAlbumById(@PathVariable long id) {
        Album album = albumService.getAlbumById(id);
        if (album == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found");
        }
        return ResponseEntity.ok(new AlbumGetDto(album));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<AlbumGetDto>> getAlbumByName(@RequestParam String name) {
        List<Album> albums = albumService.getAlbumByName(name);
        if (albums.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No album found");
        }
        return ResponseEntity.ok(albums.stream()
                .map(AlbumGetDto::new)
                .toList());
    }

    @PostMapping
    public ResponseEntity<AlbumGetDto> createAlbum(@RequestBody AlbumCreateDto albumDto) {
        Album newAlbum = albumService.createAlbum(albumDto);
        return new ResponseEntity<>(new AlbumGetDto(newAlbum), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumGetDto> updateAlbum(@PathVariable Long id,
                                                   @RequestBody AlbumUpdateDto albumDto) {
        Album updatedAlbum = albumService.updateAlbum(id, albumDto);
        return ResponseEntity.ok(new AlbumGetDto(updatedAlbum));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Album> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
