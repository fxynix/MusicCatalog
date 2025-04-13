package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import musiccatalog.exception.NotFoundException;
import musiccatalog.service.LogService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
                    example = "2025-03-29",
                    required = true)
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            String dateString = date.toString();
            CompletableFuture<String> future = logService.generateLogFileForDateAsync(dateString);
            return ResponseEntity.accepted().body("Задача запущена. ID: " + future.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Задача была прервана: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/{logId}/status")
    @Operation(summary = "Получить статус задачи по ID",
            description = "Возвращает статус задачи по её идентификатору")
    @ApiResponse(responseCode = "200", description = "Статус задачи получен")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<String> getTaskStatus(
            @Parameter(description = "ID задачи", required = true)
            @PathVariable String logId) {
        if (logService.isTaskCompleted(logId)) {
            return ResponseEntity.ok("Задача завершена");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Задача не найдена или ещё не завершена");
        }
    }

    @GetMapping("/{logId}/file")
    @Operation(summary = "Получить лог-файл по ID",
            description = "Возвращает лог-файл по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Файл успешно получен")
    @ApiResponse(responseCode = "404", description = "Файл не найден")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    public ResponseEntity<Resource> getLogFileById(
            @Parameter(description = "ID лог-файла", required = true)
            @PathVariable String logId) {
        try {
            String logFilePath = logService.getLogFilePath(logId);
            if (logFilePath == null) {
                throw new NotFoundException("Файл с указанным ID не найден");
            }

            Path filePath = Paths.get(logFilePath);
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + filePath.getFileName().toString())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}