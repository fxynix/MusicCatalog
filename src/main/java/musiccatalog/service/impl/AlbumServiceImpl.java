package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
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
        album.setId(albumDto.getId());
        album.setArtists(albumDto.getArtists());
        album.setTracks(albumDto.getTracks());
        return albumRepository.save(album);
    }

    @Override
    public Album updateAlbum(long id, AlbumUpdateDto albumDto) {
        Album album = getAlbumById(id);
        if (album == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found");
        }
        if (albumDto.getArtists() != null) {
            album.setArtists(albumDto.getArtists());
        }
        if (albumDto.getTracks() != null) {
            album.setTracks(albumDto.getTracks());
        }
        if (albumDto.getName() != null) {
            album.setName(albumDto.getName());
        }
        if (albumDto.getId() != null) {
            album.setId(albumDto.getId());
        }
        return albumRepository.save(album);
    }

    @Override
    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }
}
