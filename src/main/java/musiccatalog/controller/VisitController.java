package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import musiccatalog.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@Tag(name = "Visit Controller", description = "API для контроля посещений")
public class VisitController {

    private final VisitService visitService;

    @Autowired
    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/count")
    @Operation(summary = "Получить количество всех запросов на данный URL",
            description = "Возвращает количество запросов, которые "
                    + "были сделаны на данынй URL с момента запуска")
    @ApiResponse(responseCode = "200", description = "Запрос обработан успешно")
    public ResponseEntity<Integer> getVisitCount(@RequestParam String url) {
        int count = visitService.getVisitCount(url);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/total")
    @Operation(summary = "Получить общее количество всех запросов на сайт",
            description = "Возвращает общее количество всех запросов на сайт с момента запуска")
    public ResponseEntity<Map<String, Integer>> getTotalVisitCount() {
        Map<String, Integer> totalCount = new HashMap<>();
        totalCount.put("Общее число запросов", visitService.getTotalVisitCount());
        return ResponseEntity.ok(totalCount);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить количество всех запросов на каждый URL сайта",
            description = "Возвращает URL, на которые были сделаны запросы "
                    + "с момента запуска, и их количество")
    public ResponseEntity<Map<String, Integer>> getAllVisitCounts() {
        Map<String, Integer> allCounts = visitService.getAllVisitCounts();
        return ResponseEntity.ok(allCounts);
    }
}