package musiccatalog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
}