package musiccatalog.repository;

import java.util.List;
import musiccatalog.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT DISTINCT a FROM Album a "
            + "JOIN a.tracks t "
            + "JOIN t.genres g "
            +  "WHERE g.name = :genreName")
    List<Album> findAlbumsByGenreName(@Param("genreName") String genreName);

    List<Album> findAlbumsByName(String albumName);
}
