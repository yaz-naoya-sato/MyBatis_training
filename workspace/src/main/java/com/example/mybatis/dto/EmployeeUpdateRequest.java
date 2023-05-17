package com.example.mybatis.dto;

import com.example.mybatis.entity.Employee;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class EmployeeUpdateRequest extends EmployeeAddRequest implements Serializable {

    //ID
    @NotNull
    private int id;
}
