package com.example.mybatis.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Data
public class EmployeeSearchRequest implements Serializable {

    //ID
    private int id;

    // 社員ID
    private String employeeId;

    // 社員名(姓)
    private String familyName;

    // 社員名(名)
    private String firstName;

}
