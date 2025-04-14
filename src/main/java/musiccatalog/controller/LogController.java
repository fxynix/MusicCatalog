package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import musiccatalog.exception.NotFoundException;
import musiccatalog.exception.TooQuicklyException;
import musiccatalog.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@Tag(name = "Log Controller", description = "API для работы с лог-файлами")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/{date}")
    @Operation(summary = "Сгенерировать лог-файл для указанной даты",
            description = "Запускает асинхронную генерацию лог-файла для указанной даты")
    @ApiResponse(responseCode = "202", description = "Задача успешно запущена")
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    public ResponseEntity<String> generateLogsByDate(
            @Parameter(description = "Дата для генерации логов в формате yyyy-MM-dd",
                    example = "2025-04-13",
                    required = true)
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        CompletableFuture<String> future = logService.generateLogFileForDateAsync(date.toString());
        String taskId = future.join();
        return new ResponseEntity<>("Task ID: " + taskId, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{taskId}/status")
    @Operation(summary = "Получить статус задачи по ID",
            description = "Возвращает статус задачи по её идентификатору")
    @ApiResponse(responseCode = "200", description = "Статус задачи получен")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<String> getTaskStatus(@PathVariable String taskId) {
        return ResponseEntity.ok("Status: " + logService.getTaskStatus(taskId));
    }

    @GetMapping("/{taskId}/file")
    @Operation(summary = "Получить лог-файл по ID",
            description = "Возвращает лог-файл по ID задачи")
    @ApiResponse(responseCode = "200", description = "Файл успешно получен")
    @ApiResponse(responseCode = "404", description = "Файл не найден")
    @ApiResponse(responseCode = "425", description = "Слишком быстро. Задача ещё обрабатывается")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    public ResponseEntity<Resource> getLogFileById(
            @Parameter(description = "ID лог-файла", required = true)
            @PathVariable String taskId) {
        String status = logService.getTaskStatus(taskId);
        if (status.equals("PROCESSING")) {
            throw new TooQuicklyException("Задача ещё не завершена");
        }
        if (status.startsWith("FAILED")) {
            throw new NotFoundException("Задача провалена и лог-файл не был создан");
        }
        try {
            String filePath = logService.getLogFilePath(taskId);
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new NotFoundException("Файл логов не найден");
            }
            Resource resource = new InputStreamResource(Files.newInputStream(path));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=logs-" + taskId + ".log")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}