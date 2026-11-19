package com.campus.emergency.repository;
    import com.campus.emergency.model.AppUser;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.*;
    public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
}
