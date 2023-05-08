package com.example.mybatis.dao;

import com.example.mybatis.dto.EmployeeAddRequest;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeMapperTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;

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

        assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
    }

//    @DisplayName("異常系 - 社員IDが空文字")
//    @Test
//    void validateCheck_empty () {
//
//        EmployeeAddRequest newAdd = new EmployeeAddRequest();
//        newAdd.setEmployeeId("");
//        newAdd.setFamilyName("やじゅ");
//        newAdd.setFirstName("てすと");
//        newAdd.setSectionId(1);
//        newAdd.setMail("test_yaz@yaz.co.jp");
//        newAdd.setGenderId(1);
//
//        assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
//    }
//
//    @DisplayName("異常系 - 社員IDが9桁")
//    @Test
//    void validateCheck_lessThan () {
//
//        EmployeeAddRequest newAdd = new EmployeeAddRequest();
//        newAdd.setEmployeeId("YZ0000001");
//        newAdd.setFamilyName("やじゅ");
//        newAdd.setFirstName("てすと");
//        newAdd.setSectionId(1);
//        newAdd.setMail("test_yaz@yaz.co.jp");
//        newAdd.setGenderId(1);
//
//        assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
//    }

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

        assertThrows(Exception.class, () -> employeeMapper.save(newAdd));
    }

    // Bean Validateはcontroller側でテスト

}
