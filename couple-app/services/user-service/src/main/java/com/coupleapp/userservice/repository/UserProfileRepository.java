package com.coupleapp.userservice.repository;
import com.coupleapp.userservice.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserProfileRepository extends JpaRepository<UserProfile,UUID>{Optional<UserProfile> findByCoupleId(UUID coupleId);}
