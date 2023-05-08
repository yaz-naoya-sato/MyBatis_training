package com.example.mybatis.dao;

import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EmployeeMapper {

    List<Employee> findAll();

    Optional<Employee> findByEmployeeId(String employee_id);

    void save(EmployeeAddRequest employeeAddRequest);
}
