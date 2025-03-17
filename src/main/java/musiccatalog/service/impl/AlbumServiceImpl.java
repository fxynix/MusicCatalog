package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.model.Track;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import musiccatalog.repository.TrackRepository;
import musiccatalog.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, ArtistRepository artistRepository,
                            TrackRepository trackRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    @Override
    public Album getAlbumById(long id) {
        return albumRepository.findAll().stream()
                .filter(album -> album.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Album> getAlbumByName(String name)  {
        return albumRepository.findAll().stream()
                .filter(album -> album.getName().equals(name))
                .toList();
    }

    @Override
    public Album createAlbum(AlbumCreateDto albumDto) {
        Album album = new Album();
        album.setName(albumDto.getName());
        List<Artist> artists = artistRepository.findAllById(albumDto.getArtistsIds());
        album.setArtists(artists);
        List<Track> tracks = trackRepository.findAllById(albumDto.getTracksIds());
        album.setTracks(tracks);
        return albumRepository.save(album);
    }

    @Override
    public Album updateAlbum(long id, AlbumUpdateDto albumDto) {
        Album album = getAlbumById(id);
        if (album == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found");
        }
        if (albumDto.getArtistsIds() != null) {
            album.setArtists(artistRepository.findAllById(albumDto.getArtistsIds()));
        }
        if (albumDto.getTracksIds() != null) {
            album.setTracks(trackRepository.findAllById(albumDto.getTracksIds()));
        }
        if (albumDto.getName() != null) {
            album.setName(albumDto.getName());
        }
        return albumRepository.save(album);
    }

    @Override
    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }

}
