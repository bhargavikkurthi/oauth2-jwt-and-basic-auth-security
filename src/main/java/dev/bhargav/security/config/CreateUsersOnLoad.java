package dev.bhargav.security.config;

import dev.bhargav.security.entity.User;
import dev.bhargav.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class CreateUsersOnLoad implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup || userRepository.findAll().iterator().hasNext()) {
            return;
        }

        /* Create User */
        createUserIfNotFound("user1@example.com", passwordEncoder.encode("user1password"));
        createUserIfNotFound("user2@example.com", passwordEncoder.encode("user2password"));
    }

    private void createUserIfNotFound(String userName, String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(userName));
        if (user.isEmpty()) {
            User newUserEntity = User.builder()
                    .username(userName)
                    .password(password)
                    .build();
            userRepository.save(newUserEntity);
        }

    }
}
