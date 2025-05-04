package musiccatalog.service;

import musiccatalog.dto.AuthRequest;
import musiccatalog.dto.AuthResponse;
import musiccatalog.exception.AuthenticationException;
import musiccatalog.model.User;
import musiccatalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        User user = userRepository.findUserByEmail(authRequest.getEmail());
        if (user == null) {
            throw (new AuthenticationException("Invalid credentials"));
        }

        if (!user.getPassword().equals(authRequest.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getName());
        response.setEmail(user.getEmail());

        response.setToken("dummy-token");
        return response;
    }
}