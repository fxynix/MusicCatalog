package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.model.User;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import musiccatalog.repository.UserRepository;
import musiccatalog.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, AlbumRepository albumRepository,
            UserRepository userRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
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
        List<Album> albums = albumRepository.findAllById(artistDto.getAlbumsIds());
        artist.setAlbums(albums);
        List<User> users = userRepository.findAllById(artistDto.getLikedByUsersIds());
        artist.setLikedByUsers(users);
        return artistRepository.save(artist);
    }

    @Override
    public Artist updateArtist(long id, ArtistUpdateDto artistDto) {
        Artist artist = getArtistById(id);
        if (artist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
        }
        if (artistDto.getAlbumsIds() != null) {
            artist.setAlbums(albumRepository.findAllById(artistDto.getAlbumsIds()));
        }
        if (artistDto.getName() != null) {
            artist.setName(artistDto.getName());
        }
        if (artistDto.getLikedByUsersIds() != null) {
            artist.setLikedByUsers(userRepository.findAllById(artistDto.getLikedByUsersIds()));
        }
        return artistRepository.save(artist);
    }

    @Override
    public void deleteArtist(Long id) {
        artistRepository.deleteById(id);
    }

}
