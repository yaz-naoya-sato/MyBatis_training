package com.example.mybatis.dao;

import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.entity.Employee;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeMapperTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @DisplayName("異常系 - 社員IDを送信しない")
    @Test
    void validateCheck_undefine() {

        EmployeeAddRequest newAdd = new EmployeeAddRequest();
        //newAdd.setEmployeeId("YZ00000011");
        newAdd.setFamilyName("やじゅ");
        newAdd.setFirstName("てすと");
        newAdd.setSectionId(1);
        newAdd.setMail("test_yaz@yaz.co.jp");
        newAdd.setGenderId(1);

        // バリデーションテスト
        Set<ConstraintViolation<EmployeeAddRequest>> violations = validator.validate(newAdd);

        // バリデーションエラーがないことを確認
        assertFalse(violations.isEmpty());

        assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
    }

    @DisplayName("異常系 - 社員IDがnull")
    @Test
    void validateCheck_null () {

        EmployeeAddRequest newAdd = new EmployeeAddRequest();
        newAdd.setEmployeeId(null);
        newAdd.setFamilyName("やじゅ");
        newAdd.setFirstName("てすと");
        newAdd.setSectionId(1);
        newAdd.setMail("test_yaz@yaz.co.jp");
        newAdd.setGenderId(1);

        Set<ConstraintViolation<EmployeeAddRequest>> violations = validator.validate(newAdd);

        assertFalse(violations.isEmpty());

        assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
    }

    @DisplayName("異常系 - 社員IDが空文字")
    @Test
    void validateCheck_empty () {

        EmployeeAddRequest newAdd = new EmployeeAddRequest();
        newAdd.setEmployeeId("");
        newAdd.setFamilyName("やじゅ");
        newAdd.setFirstName("てすと");
        newAdd.setSectionId(1);
        newAdd.setMail("test_yaz@yaz.co.jp");
        newAdd.setGenderId(1);

        Set<ConstraintViolation<EmployeeAddRequest>> violations = validator.validate(newAdd);

        assertFalse(violations.isEmpty());

        //assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
    }

    @DisplayName("異常系 - 社員IDが9桁")
    @Test
    void validateCheck_lessThan () {

        EmployeeAddRequest newAdd = new EmployeeAddRequest();
        newAdd.setEmployeeId("YZ0000001");
        newAdd.setFamilyName("やじゅ");
        newAdd.setFirstName("てすと");
        newAdd.setSectionId(1);
        newAdd.setMail("test_yaz@yaz.co.jp");
        newAdd.setGenderId(1);

        Set<ConstraintViolation<EmployeeAddRequest>> violations = validator.validate(newAdd);

        assertFalse(violations.isEmpty());

        //assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
    }

    @DisplayName("異常系 - 社員IDが11桁")
    @Test
    void validateCheck_greaterThan () {

        EmployeeAddRequest newAdd = new EmployeeAddRequest();
        newAdd.setEmployeeId("YZ000000001");
        newAdd.setFamilyName("やじゅ");
        newAdd.setFirstName("てすと");
        newAdd.setSectionId(1);
        newAdd.setMail("test_yaz@yaz.co.jp");
        newAdd.setGenderId(1);

        Set<ConstraintViolation<EmployeeAddRequest>> violations = validator.validate(newAdd);

        assertFalse(violations.isEmpty());

        assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
    }

    // Bean Validateはcontroller側でテスト

    /**
     * 一覧表示
     */
    @Test
    void findAllTest() {
        List<Employee> testList = employeeMapper.findAll();
        assertNotNull(testList);
        assertEquals(testList.get(0).getEmployeeId(), "YZ00000001");
    }

}
