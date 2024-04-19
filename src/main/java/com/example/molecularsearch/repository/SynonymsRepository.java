package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.Synonyms;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SynonymsRepository extends MongoRepository<Synonyms, String> {

    @Query(value = "{ $text: { $search: ?0 }}", fields = "{ 'chemInfo' : 1 }", sort = "{ 'meta.score': { $meta: 'textScore' }}")
    List<Synonyms> findByTextSearch(String keyword, Sort sort); // Full Text Search 방식으로 검색한 후 해당 분자정보 가져오기 (검색 정확도에 따라 내림차순 정렬)
}
