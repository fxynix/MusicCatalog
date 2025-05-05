package musiccatalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(final String message) {
        super(message);
    }

}
