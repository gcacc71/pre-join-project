package com.ttn.practice.controller;


import com.ttn.practice.model.MyDocumentCloneDepartment;
import com.ttn.practice.service.MyDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/elasticsearch")
public class ElasticsearchController {
    private final MyDocumentService myDocumentService;

    public ElasticsearchController(MyDocumentService myDocumentService){
        this.myDocumentService = myDocumentService;
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<MyDocumentCloneDepartment>> getDepartmentByName(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(myDocumentService.searchByName(name));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<List<MyDocumentCloneDepartment>> getDepartmentByCode(@PathVariable String code){
        return ResponseEntity.status(HttpStatus.OK).body(myDocumentService.searchByCode(code));
    }
}
