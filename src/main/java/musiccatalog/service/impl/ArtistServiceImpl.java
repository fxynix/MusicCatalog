package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.model.Artist;
import musiccatalog.repository.ArtistRepository;
import musiccatalog.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    @Override
    public Artist getArtistById(long id) {
        return artistRepository.findAll().stream()
                .filter(artist -> artist.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Artist getArtistByName(String name)  {
        return artistRepository.findAll().stream()
                .filter(artist -> artist.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Artist createArtist(ArtistCreateDto artistDto) {
        Artist artist = new Artist();
        artist.setName(artistDto.getName());
        artist.setId(artistDto.getId());
        artist.setAlbums(artistDto.getAlbums());
        artist.setLikedByUsers(artistDto.getLikedByUsers());
        return artistRepository.save(artist);
    }

    @Override
    public Artist updateArtist(long id, ArtistUpdateDto artistDto) {
        Artist artist = getArtistById(id);
        if (artist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
        }
        if (artistDto.getAlbums() != null) {
            artist.setAlbums(artistDto.getAlbums());
        }
        if (artistDto.getName() != null) {
            artist.setName(artistDto.getName());
        }
        if (artistDto.getId() != null) {
            artist.setId(artistDto.getId());
        }
        if (artistDto.getLikedByUsers() != null) {
            artist.setLikedByUsers(artistDto.getLikedByUsers());
        }
        return artistRepository.save(artist);
    }

    @Override
    public void deleteArtist(Long id) {
        artistRepository.deleteById(id);
    }
}
