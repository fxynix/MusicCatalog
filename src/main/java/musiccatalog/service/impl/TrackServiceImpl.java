package musiccatalog.service.impl;

import java.util.ArrayList;
import java.util.List;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.model.Genre;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.GenreRepository;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.repository.TrackRepository;
import musiccatalog.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final PlaylistRepository playlistRepository;

    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository,
                            AlbumRepository albumRepository, GenreRepository genreRepository,
                            PlaylistRepository playlistRepository) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
        this.playlistRepository = playlistRepository;
    }

    @Override
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    @Override
    public Track getTrackById(long id) {
        return trackRepository.findAll().stream()
                .filter(track -> track.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Track> getTrackByName(String name)  {
        return trackRepository.findAll().stream()
                .filter(track -> track.getName().equals(name))
                .toList();
    }

    @Override
    public Track createTrack(TrackCreateDto trackDto) {
        Track track = new Track();
        track.setName(trackDto.getName());
        track.setDuration(trackDto.getDuration());
        Album album = albumRepository.findById(trackDto.getAlbumId())
                .orElseThrow(()
                        -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Artist was not found"));
        track.setAlbum(album);
        List<Genre> genres = genreRepository.findAllById(trackDto.getGenresIds());
        track.setGenres(genres);

        return trackRepository.save(track);
    }

    @Override
    public Track updateTrack(long id, TrackUpdateDto trackDto) {
        Track track = getTrackById(id);
        if (track == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found");
        }
        if (trackDto.getAlbumId() != null) {
            track.setAlbum(albumRepository.findById(trackDto.getAlbumId())
                .orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found")));
        }
        if (trackDto.getName() != null) {
            track.setName(trackDto.getName());
        }
        if (trackDto.getDuration() != 0) {
            track.setDuration(trackDto.getDuration());
        }
        if (trackDto.getGenresIds() != null) {
            track.setGenres(genreRepository.findAllById(trackDto.getGenresIds()));
        }
        List<Genre> genres = new ArrayList<>();
        if (trackDto.getGenresIds() != null && !trackDto.getGenresIds().isEmpty()) {
            for (Long genreId : trackDto.getGenresIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Album not found"));
                genre.getTracks().add(track);
                genres.add(genre);
            }
            track.setGenres(genres);
        }
        List<Playlist> playlists;
        if (trackDto.getPlaylistsIds() != null) {
            playlists = trackDto.getPlaylistsIds().stream().map(playlistId ->
                    playlistRepository.findById(playlistId)
                            .orElseThrow(() ->
                                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            "Artist not found"))).toList();
            track.setPlaylists(playlists);
        }
        return trackRepository.save(track);

    }

    @Override
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
    }

}
