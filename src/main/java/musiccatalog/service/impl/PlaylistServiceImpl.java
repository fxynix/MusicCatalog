package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.PlaylistCreateDto;
import musiccatalog.dto.update.PlaylistUpdateDto;
import musiccatalog.model.Playlist;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Override
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    @Override
    public Playlist getPlaylistById(long id) {
        return playlistRepository.findAll().stream()
                .filter(playlist -> playlist.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Playlist> getPlaylistByName(String name)  {
        return playlistRepository.findAll().stream()
                .filter(playlist -> playlist.getName().equals(name))
                .toList();
    }

    @Override
    public Playlist createPlaylist(PlaylistCreateDto playlistDto) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistDto.getName());
        playlist.setId(playlistDto.getId());
        playlist.setTracks(playlistDto.getTracks());
        playlist.setAuthor(playlistDto.getAuthor());
        playlist.setSubscribers(playlistDto.getSubscribers());
        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist updatePlaylist(long id, PlaylistUpdateDto playlistDto) {
        Playlist playlist = getPlaylistById(id);
        if (playlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found");
        }
        if (playlistDto.getTracks() != null) {
            playlist.setTracks(playlistDto.getTracks());
        }
        if (playlistDto.getName() != null) {
            playlist.setName(playlistDto.getName());
        }
        if (playlistDto.getId() != null) {
            playlist.setId(playlistDto.getId());
        }
        if (playlistDto.getAuthor() != null) {
            playlist.setAuthor(playlistDto.getAuthor());
        }
        if (playlistDto.getSubscribers() != null) {
            playlist.setSubscribers(playlistDto.getSubscribers());
        }
        return playlistRepository.save(playlist);
    }

    @Override
    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }
}

