package musiccatalog.controller;

import java.util.List;
import musiccatalog.model.Music;
import musiccatalog.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/music")
public class Controller {
    private final MusicService musicService;

    @Autowired
    public Controller(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("{id}")
    public Music getMusicById(@PathVariable int id) {
        Music music = musicService.getMusicById(id);
        if (music == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Music not found");
        }
        return music;
    }

    @GetMapping(params = "name")
    public List<Music> getMusicByNameParam(@RequestParam String name) {
        List<Music> result = musicService.getMusicByName(name);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No music found");
        }
        return result;
    }
}
