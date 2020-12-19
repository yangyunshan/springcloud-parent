package com.sensorweb.datacenterlaadsservice.dao;

import com.sensorweb.datacenterlaadsservice.entity.Entry;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface EntryMapper {
    int insertData(Entry entry);
    int insertDataBatch(List<Entry> entries);

    Entry selectById(int id);
}
