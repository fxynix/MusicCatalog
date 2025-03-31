package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import musiccatalog.exception.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@Tag(name = "Log Controller", description = "API для работы с лог-файлами")
public class LogController {

    private static final String LOG_FILE_PATH = "logs/musiccatalog"; // Путь к лог-файлу

    @GetMapping()
    @Operation(summary = "Скачать сегоднящний лог-файл",
            description = "Возвращает сегодняшний лог-файл")
    @ApiResponse(responseCode = "200", description = "Лог-файл успешно отправлен")
    @ApiResponse(responseCode = "404", description = "Лог-файл не найден")
    public ResponseEntity<Resource> getLogFile() throws IOException {
        Path logFilePath = Paths.get(LOG_FILE_PATH + ".log");
        File logFile = logFilePath.toFile();

        if (!logFile.exists()) {
            throw new NotFoundException("Логи не найдены");
        }

        Resource resource = new UrlResource(logFilePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping(params = "date")
    @Operation(summary = "Скачать логи за указанную дату",
            description = "Возвращает файл .log с записями по введённой дате")
    @ApiResponse(responseCode = "200", description = "Лог-файл успешно отправлен")
    @ApiResponse(responseCode = "400", description = "Некорректный формат даты")
    @ApiResponse(responseCode = "404", description = "Лог-файл не найден")
    public ResponseEntity<Resource> getLogsByDate(
            @Parameter(description = "Дата для поиска логов в формате yyyy-MM-dd",
                    example = "2025-03-29",
                    required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date)
            throws IOException {
        String dateString = date.toString();
        Path logPath;
        if (dateString.equals(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
            logPath = Paths.get(LOG_FILE_PATH + ".log");
        } else {
            logPath = Paths.get(LOG_FILE_PATH + "." + dateString + ".log");
        }

        if (!Files.exists(logPath)) {
            throw new NotFoundException("Логи по указанной дате не найдены");
        }

        Resource resource = new UrlResource(logPath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + "musiccatalog." + dateString + "\"")
                .body(resource);
    }

}