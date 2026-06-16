package com.coupleapp.feelingservice.repository;
import com.coupleapp.feelingservice.domain.Feeling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.*;
public interface FeelingRepository extends JpaRepository<Feeling,UUID>{
    @Query("SELECT f FROM Feeling f WHERE f.userId=:uid AND f.createdAt>=:since ORDER BY f.createdAt DESC") List<Feeling> findTodayByUser(UUID uid,Instant since);
    @Query("SELECT f FROM Feeling f WHERE f.coupleId=:cid AND f.createdAt>=:since ORDER BY f.createdAt DESC") List<Feeling> findByCoupleIdSince(UUID cid,Instant since);
}
