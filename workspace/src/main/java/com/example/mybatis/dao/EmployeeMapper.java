package com.example.mybatis.dao;

import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.dto.EmployeeUpdateRequest;
import com.example.mybatis.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EmployeeMapper {

    /**
     * 社員情報全検索
     * @return 検索結果
     */
    List<Employee> findAll();

    List<Employee> testFindAll();

    Employee findById(Integer id);

    Optional<Employee> findByEmployeeId(String employee_id);



    void save(EmployeeAddRequest employeeRequest);

    void update(EmployeeUpdateRequest employeeUpdateRequest);

    void delete(Integer id);
}
