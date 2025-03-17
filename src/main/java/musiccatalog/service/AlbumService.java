package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.model.Album;
import org.springframework.stereotype.Service;

@Service
public interface AlbumService {
    List<Album> getAllAlbums();

    Album getAlbumById(long id);

    List<Album> getAlbumByName(String name);

    Album createAlbum(AlbumCreateDto albumDto);

    Album updateAlbum(long id, AlbumUpdateDto albumDto);

    void deleteAlbum(Long id);
}