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
import java.util.List;
import java.util.stream.Stream;
import musiccatalog.exception.NotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private static final String LOG_FILE_PATH = "logs/musiccatalog.log"; // Путь к лог-файлу

    @GetMapping()
    @Operation(summary = "Скачать лог-файл",
            description = "Возвращает лог-файл в виде файла для скачивания.")
    @ApiResponse(responseCode = "200", description = "Лог-файл успешно отправлен")
    @ApiResponse(responseCode = "404", description = "Лог-файл не найден")
    public ResponseEntity<Resource> getLogFile() throws IOException {
        Path logFilePath = Paths.get(LOG_FILE_PATH);
        File logFile = logFilePath.toFile();

        if (!logFile.exists()) {
            return ResponseEntity.notFound().build();
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
    @ApiResponse(responseCode = "500", description = "Ошибка обработки лог-файла")
    public ResponseEntity<Resource> getLogsByDate(
            @Parameter(description = "Дата для фильтрации логов в формате yyyy-MM-dd",
                    example = "2025-03-29",
                    required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            String dateString = date.toString();
            Path logPath = Paths.get(LOG_FILE_PATH);

            if (!Files.exists(logPath)) {
                throw new NotFoundException("Основные логи не найдены");
            }

            List<String> filteredLines;
            try (Stream<String> lines = Files.lines(logPath)) {
                filteredLines = lines
                        .filter(line -> line.startsWith(dateString))
                        .toList();
            }

            if (filteredLines.isEmpty()) {
                throw new NotFoundException("Не найдено логов по дате: " + dateString);
            }

            String fileContent = String.join(System.lineSeparator(), filteredLines);
            ByteArrayResource resource = new ByteArrayResource(fileContent.getBytes());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=logs-" + dateString + ".log")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(fileContent.getBytes().length)
                    .body(resource);


        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource(e.getMessage().getBytes()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource("Ошибка обработки логов".getBytes()));
        }
    }

}