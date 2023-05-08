package com.example.mybatis.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Data
public class EmployeeAddRequest implements Serializable {

    // 社員ID
    @NotEmpty(message = "社員IDを入力してください")
    @Length(min = 10, max = 10, message = "社員IDは10文字で入力してください")
    @Pattern(regexp="^YZ\\d{8}$", message = "社員IDを正しく入力してください")
    private String employeeId;

    // 社員名(姓)
    @NotEmpty(message = "社員名(姓)を入力してください")
    @Length(min = 0, max = 20,message = "社員名(姓)は20文字以下で入力してください")
    private String familyName;

    // 社員名(名)
    @NotEmpty(message = "社員名(名)を入力してください")
    @Length(min = 0, max = 20, message = "社員名(名)は20文字以下で入力してください")
    private String firstName;

    // 所属セクション
    @NotNull(message = "所属セクションを選択してください")
    @Range(min = 1, max = 3, message = "所属セクションを選択してください")
    private Integer sectionId;

    // メールアドレス
    @NotEmpty(message = "メールアドレスを入力してください")
    @Length(min = 0, max = 256,message = "メールアドレスは256文字以下で入力してください")
    @Pattern(regexp="^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_.-]+$", message = "メールアドレスを正しく入力してください")
    private String mail;

    // 性別
    @NotNull(message = "性別を選択してください")
    @Range(min = 1, max = 2, message = "性別を選択してください")
    private Integer genderId;
}
