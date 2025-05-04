package musiccatalog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MusicCatalogApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.filename(".env.development")
				.directory(System.getProperty("user.dir"))
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

		SpringApplication.run(MusicCatalogApplication.class, args);
	}
}