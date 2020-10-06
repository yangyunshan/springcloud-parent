package com.sensorweb.sossensorservice.dao;

import com.sensorweb.sossensorservice.entity.sos.Address;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Mapper
public interface AddressMapper {
    int insertData(Address address);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    Address selectById(int id);
    List<Address> selectByCity(String city);
    List<Address> selectByAdministrativeArea(String administrativeArea);
    List<Address> selectByCountry(String country);
}
