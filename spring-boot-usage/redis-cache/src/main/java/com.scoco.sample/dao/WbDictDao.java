package com.scoco.sample.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.scoco.sample.entity.WbDict;

public interface WbDictDao extends JpaRepository<WbDict, Long> {

    @Query("select p from WbDict p where p.dictType = :dictType")
    List<WbDict> listByType(@Param("dictType") String type);
}