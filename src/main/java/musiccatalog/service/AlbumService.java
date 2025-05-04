package musiccatalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import musiccatalog.cache.InMemoryCache;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.model.Track;
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
        return albumRepository.findAll();
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
        String cacheKey = "albums_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (List<Album>) cache.get(cacheKey);
        }
        List<Album> albums = albumRepository.findAlbumsByName(name);
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
            throw new NotFoundException("Подходящих альбомов не найдено");
        }
        cache.put(cacheKey, albums);
        return albums;
    }

    public Album createAlbum(AlbumCreateDto albumDto) {
        List<Artist> artists = artistRepository.findAllById(albumDto.getArtistsIds());

        if (artists.size() != albumDto.getArtistsIds().size()) {
            List<Long> missingIds = new ArrayList<>(albumDto.getArtistsIds());
            artists.forEach(a -> missingIds.remove(a.getId()));
            throw new NotFoundException("Не найдены исполнители с ID: " + missingIds);
        }
        Album album = new Album();
        album.setName(albumDto.getName());
        album.setArtists(artists);
        cache.clear();
        return albumRepository.save(album);
    }

    public Album updateAlbum(long id, AlbumUpdateDto albumDto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден альбом с ID = " + id));
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
        for (Long trackId : albumDto.getTracksIds()) {
            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new NotFoundException(
                            "Указанный трек не найден"));
            track.setAlbum(album);
        }
        List<Track> oldTracks = new ArrayList<>(album.getTracks());

        List<Track> newTracks = new ArrayList<>();
        if (albumDto.getTracksIds() != null) {
            for (Long trackId : albumDto.getTracksIds()) {
                Track track = trackRepository.findById(trackId)
                        .orElseThrow(() -> new NotFoundException(
                                "Указанный трек не найден"));
                track.setAlbum(album);
                newTracks.add(track);
            }
        }
        album.setTracks(newTracks);

        List<Track> tracksToRemove = oldTracks.stream()
                .filter(oldTrack -> !newTracks.contains(oldTrack))
                .toList();

        tracksToRemove.forEach(track -> {
            track.setAlbum(null);
            trackRepository.save(track);
        });
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
