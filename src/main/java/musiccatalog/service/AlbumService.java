package musiccatalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import musiccatalog.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final InMemoryCache cache;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository,
                        TrackRepository trackRepository, InMemoryCache cache) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.trackRepository = trackRepository;
        this.cache = cache;
    }

    public List<Album> getAllAlbums() {
        String cacheKey = "albums_all";
        if (cache.containsKey(cacheKey)) {
            return (List<Album>) cache.get(cacheKey);
        }
        List<Album> albums = albumRepository.findAll();
        cache.put(cacheKey, albums);
        return albums;
    }

    public Optional<Album> getAlbumById(long id) {
        String cacheKey = "albums_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return Optional.of((Album) cache.get(cacheKey));
        }
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден альбом с ID = " + id));
        cache.put(cacheKey, album);
        return Optional.of(album);
    }

    public List<Album> getAlbumByName(String name)  {
        List<Album> albums = albumRepository.findAlbumsByName(name);
        String cacheKey = "albums_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (List<Album>) cache.get(cacheKey);
        }
        cache.put(cacheKey, albums);
        return albums;
    }

    public List<Album> getAlbumsByGenreName(String genreName) {
        String cacheKey = "albums_genre_" + genreName;
        if (cache.containsKey(cacheKey)) {
            return (List<Album>) cache.get(cacheKey);
        }
        List<Album> albums = albumRepository.findAlbumsByGenreName(genreName);
        if (albums.isEmpty()) {
            throw new NotFoundException("Подхлдящих альбомов не найдено");
        }
        cache.put(cacheKey, albums);
        return albums;
    }

    public Album createAlbum(AlbumCreateDto albumDto) {
        Album album = new Album();
        album.setName(albumDto.getName());
        if (albumDto.getArtistsIds() != null) {
            List<Artist> artists = artistRepository.findAllById(albumDto.getArtistsIds());
            album.setArtists(artists);
        }
        cache.clear();
        return albumRepository.save(album);
    }

    public Album updateAlbum(long id, AlbumUpdateDto albumDto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден альбом с ID = " + id));
        if (albumDto.getArtistsIds() != null) {
            album.setArtists(artistRepository.findAllById(albumDto.getArtistsIds()));
        }
        if (albumDto.getTracksIds() != null) {
            album.setTracks(trackRepository.findAllById(albumDto.getTracksIds()));
        }
        if (albumDto.getName() != null) {
            album.setName(albumDto.getName());
        }
        List<Artist> artists = new ArrayList<>();
        if (albumDto.getArtistsIds() != null && !albumDto.getArtistsIds().isEmpty()) {
            for (Long artistId : albumDto.getArtistsIds()) {
                Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() -> new NotFoundException(
                                "Указанный исполнитель альбома не найден"));
                artist.getAlbums().add(album);
                artists.add(artist);
            }
            album.setArtists(artists);
        }
        cache.clear();
        return albumRepository.save(album);
    }

    public void deleteAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Не найдено альбома с ID = " + id));
        albumRepository.delete(album);
        cache.clear();
    }


}
