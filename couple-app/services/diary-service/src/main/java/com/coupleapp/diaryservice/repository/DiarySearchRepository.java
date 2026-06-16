package com.coupleapp.diaryservice.repository;

import com.coupleapp.diaryservice.domain.DiaryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface DiarySearchRepository extends ElasticsearchRepository<DiaryDocument, String> {

    List<DiaryDocument> findByCoupleIdAndTitleContainingOrCoupleIdAndContentContaining(
            String coupleId1, String title,
            String coupleId2, String content);
}
