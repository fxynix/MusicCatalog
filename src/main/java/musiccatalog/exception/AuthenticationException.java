package musiccatalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(final String message) {
        super(message);
    }

}
