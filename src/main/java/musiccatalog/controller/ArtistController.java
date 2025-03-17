package musiccatalog.controller;

import java.util.List;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.get.ArtistGetDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.model.Artist;
import musiccatalog.service.ArtistService;
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
@RequestMapping("/artists")
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistGetDto>> getAllArtists() {
        List<Artist> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists.stream()
                .map(ArtistGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<ArtistGetDto> getArtistById(@PathVariable long id) {
        Artist artist = artistService.getArtistById(id);
        if (artist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
        }
        return ResponseEntity.ok(new ArtistGetDto(artist));
    }

    @GetMapping(params = "name")
    public ResponseEntity<ArtistGetDto> getArtistByName(@RequestParam String name) {
        Artist artist = artistService.getArtistByName(name);
        if (artist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist was not found");
        }
        return ResponseEntity.ok(new ArtistGetDto(artist));
    }

    @PostMapping
    public ResponseEntity<ArtistGetDto> createArtist(@RequestBody ArtistCreateDto artistDto) {
        Artist newArtist = artistService.createArtist(artistDto);
        return new ResponseEntity<>(new ArtistGetDto(newArtist), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistGetDto> updateArtist(@PathVariable Long id,
                                                     @RequestBody ArtistUpdateDto artistDto) {
        Artist updatedArtist = artistService.updateArtist(id, artistDto);
        return ResponseEntity.ok(new ArtistGetDto(updatedArtist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Artist> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}