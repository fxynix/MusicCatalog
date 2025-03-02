package musiccatalog.service;

import java.util.List;
import musiccatalog.model.Music;
import org.springframework.stereotype.Service;

@Service
public interface MusicService {
    Music getMusicById(int id);

    List<Music> getMusicByName(String name);
}