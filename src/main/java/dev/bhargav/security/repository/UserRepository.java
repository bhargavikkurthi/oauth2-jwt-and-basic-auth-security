package dev.bhargav.security.repository;

import dev.bhargav.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    void deleteByUsername(String username);
}
