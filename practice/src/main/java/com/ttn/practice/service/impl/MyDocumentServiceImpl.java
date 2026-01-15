package com.ttn.practice.service.impl;

import com.ttn.practice.model.MyDocumentCloneDepartment;
//import com.ttn.practice.repository.MyDocumentRepository;
import com.ttn.practice.service.MyDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class MyDocumentServiceImpl implements MyDocumentService {
    private final ElasticsearchOperations operations;

    @Override
    public List<MyDocumentCloneDepartment> searchByName(String name) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("name")
                                .query(name)
                        )
                )
                .build();

        return operations.search(query, MyDocumentCloneDepartment.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Override
    public List<MyDocumentCloneDepartment> searchByCode(String code) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .term(m -> m
                                .field("code")
                                .value(code)
                        )
                )
                .build();

        return operations.search(query, MyDocumentCloneDepartment.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
