package musiccatalog.repository;

import java.util.List;
import musiccatalog.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findPlaylistByName(String name);

    List<Playlist> findPlaylistByAuthorId(Long authorId);

}
