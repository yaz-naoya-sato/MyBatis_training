package com.example.mybatis.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {

    //ID
    private int id;

    // 社員ID
    private String employeeId;

    // 社員名(姓)
    private String familyName;

    // 社員名(名)
    private String firstName;

    // 所属セクション
    private Integer sectionId;

    // メールアドレス
    private String mail;

    // 性別
    private Integer genderId;

}
