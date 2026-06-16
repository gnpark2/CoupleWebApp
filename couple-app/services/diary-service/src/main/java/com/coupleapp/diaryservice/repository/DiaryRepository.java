package com.coupleapp.diaryservice.repository;

import com.coupleapp.diaryservice.domain.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DiaryRepository extends JpaRepository<DiaryEntry, UUID> {

    @Query("SELECT d FROM DiaryEntry d WHERE d.coupleId = :coupleId ORDER BY d.entryDate DESC, d.createdAt DESC")
    List<DiaryEntry> findByCoupleIdOrderByDate(UUID coupleId);

    @Query("SELECT d FROM DiaryEntry d WHERE d.coupleId = :coupleId AND d.entryType = :type ORDER BY d.entryDate DESC")
    List<DiaryEntry> findByCoupleIdAndType(UUID coupleId, String type);
}
