package com.ttn.practice.service;

import com.ttn.practice.model.MyDocumentCloneDepartment;

import java.util.List;

public interface MyDocumentService {
    List<MyDocumentCloneDepartment> searchByName(String name);

    List<MyDocumentCloneDepartment> searchByCode(String code);
}
