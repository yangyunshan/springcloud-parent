package com.sensorweb.sossensorservice.dao;

import com.sensorweb.sossensorservice.entity.sos.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ContactMapper {
    int insertData(Contact contact);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    String selectById(int id);
    List<String> selectByIndividualName(String individualName);
    List<String> selectByPositionName(String positionName);
    List<String> selectByOrganizationName(String organizationName);
    List<Contact> selectByRole(String role);
}
