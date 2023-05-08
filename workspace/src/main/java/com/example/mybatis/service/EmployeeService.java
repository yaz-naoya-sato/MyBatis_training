package com.example.mybatis.service;

import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.dao.EmployeeMapper;
import com.example.mybatis.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public boolean isInsertDuplication(String employeeId) {

        Employee employee = employeeMapper.findByEmployeeId(employeeId).orElse(null);
        return employee == null;

    }

    public void save(EmployeeAddRequest employeeAddRequest) {
        employeeMapper.save(employeeAddRequest);
    }

    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }
}
