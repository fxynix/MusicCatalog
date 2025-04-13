package musiccatalog.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import musiccatalog.exception.NotFoundException;
import musiccatalog.exception.ProcessingFileException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final String LOG_FILE_PATH = "logs/musiccatalog.log";
    private static final String LOGS_DIR = "logs/";

    private final Map<String, String> logFiles = new ConcurrentHashMap<>();
    private final Map<String, Boolean> taskStatus = new ConcurrentHashMap<>();

    @Async
    public CompletableFuture<String> generateLogFileForDateAsync(String date) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String existingLogId = findLogIdByDate(date);
                if (existingLogId != null) {
                    return existingLogId;
                }

                Path logPath = Paths.get(LOG_FILE_PATH);
                if (!Files.exists(logPath)) {
                    throw new FileNotFoundException("Лог-файл не найден: " + LOG_FILE_PATH);
                }

                List<String> filteredLines;
                try (var lines = Files.lines(logPath)) {
                    filteredLines = lines
                            .filter(line -> line.startsWith(date))
                            .toList();
                }

                if (filteredLines.isEmpty()) {
                    throw new NotFoundException("Нет записей в логах для указанной даты");
                }

                Files.createDirectories(Paths.get(LOGS_DIR));
                String logFileName = LOGS_DIR + "musiccatalog-" + date + ".log";
                Path logFilePath = Paths.get(logFileName);

                if (Files.exists(logFilePath)) {
                    String logId = UUID.randomUUID().toString();
                    logFiles.put(logId, logFileName);
                    taskStatus.put(logId, true);
                    return logId;
                }

                Files.write(logFilePath, filteredLines);

                String logId = UUID.randomUUID().toString();
                logFiles.put(logId, logFileName);
                taskStatus.put(logId, true);

                return logId;
            } catch (FileNotFoundException e) {
                throw new ProcessingFileException("Лог-файл не найден");
            } catch (IOException e) {
                String logId = UUID.randomUUID().toString();
                taskStatus.put(logId, false);
                throw new ProcessingFileException("Ошибка обработки файла");
            }
        });
    }

    private String findLogIdByDate(String date) {
        return logFiles.entrySet().stream()
                .filter(entry -> entry.getValue().contains(date))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public String getLogFilePath(String logId) {
        return logFiles.get(logId);
    }

    public boolean isTaskCompleted(String logId) {
        return taskStatus.getOrDefault(logId, false);
    }
}