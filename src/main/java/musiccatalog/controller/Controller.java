package musiccatalog.controller;

import java.util.List;
import musiccatalog.model.Music;
import musiccatalog.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music")
public class Controller {
    private final MusicService musicService;

    @Autowired
    public Controller(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping(params = "id")
    public Music getMusicByIdParam(@RequestParam int id) {
        return musicService.getMusicById(id);
    }

    @GetMapping("{id}")
    public Music getMusicById(@PathVariable int id) {
        return musicService.getMusicById(id);
    }

    @GetMapping(params = "name")
    public List<Music> getMusicByNameParam(@RequestParam String name) {
        return musicService.getMusicByName(name);
    }
}
