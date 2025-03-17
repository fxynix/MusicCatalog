package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.PlaylistCreateDto;
import musiccatalog.dto.update.PlaylistUpdateDto;
import musiccatalog.model.Playlist;
import org.springframework.stereotype.Service;

@Service
public interface PlaylistService {
    List<Playlist> getAllPlaylists();

    Playlist getPlaylistById(long id);

    List<Playlist> getPlaylistByName(String name);

    Playlist createPlaylist(PlaylistCreateDto playlistDto);

    Playlist updatePlaylist(long id, PlaylistUpdateDto playlistDto);

    void deletePlaylist(Long id);
}