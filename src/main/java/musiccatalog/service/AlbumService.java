package musiccatalog.service;

import java.util.ArrayList;
import java.util.List;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import musiccatalog.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        List<Album> albums = albumRepository.findAll();
        String cacheKey = "albums_all";
        if (cache.containsKey(cacheKey)) {
            return (List<Album>) cache.get(cacheKey);
        }
        cache.put(cacheKey, albums);
        return albums;
    }

    public Album getAlbumById(long id) {
        Album album = albumRepository.findAlbumById(id);
        String cacheKey = "albums_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Album) cache.get(cacheKey);
        }
        cache.put(cacheKey, album);
        return album;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
        List<Artist> artists = new ArrayList<>();
        if (albumDto.getArtistsIds() != null && !albumDto.getArtistsIds().isEmpty()) {
            for (Long artistId : albumDto.getArtistsIds()) {
                Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Artist not found"));
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Album was not found"));
        albumRepository.delete(album);
        cache.clear();
    }


}
