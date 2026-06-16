package com.coupleapp.authservice.repository;
import com.coupleapp.authservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User,UUID> { Optional<User> findByEmail(String e); boolean existsByEmail(String e); }
