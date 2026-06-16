package com.coupleapp.coupleservice.repository;
import com.coupleapp.coupleservice.domain.Couple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
public interface CoupleRepository extends JpaRepository<Couple,UUID>{
    @Query("SELECT c FROM Couple c WHERE c.userAId=:uid OR c.userBId=:uid") Optional<Couple> findByUserId(UUID uid);
    boolean existsByUserAIdOrUserBId(UUID a,UUID b);
}
