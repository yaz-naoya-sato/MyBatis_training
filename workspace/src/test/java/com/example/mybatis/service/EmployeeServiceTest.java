package com.example.mybatis.service;

import com.example.mybatis.dao.EmployeeMapper;
import com.example.mybatis.dto.EmployeeAddRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestEntityManager
@Transactional
public class EmployeeServiceTest {

    @InjectMocks
    EmployeeService employeeService;
    @Mock
    EmployeeMapper employeeMapper;

    @BeforeEach
    void init() {
        employeeMapper = mock(EmployeeMapper.class);
        employeeService = new EmployeeService();
    }

    @Test
    @DisplayName("")
    void test() {

        EmployeeAddRequest newAdd = new EmployeeAddRequest();
        newAdd.setEmployeeId("YZ00000001");
        newAdd.setFamilyName("やじゅ");
        newAdd.setFirstName("てすと");
        newAdd.setSectionId(1);
        newAdd.setMail("test_yaz@yaz.co.jp");
        newAdd.setGenderId(1);

        doNothing().when(employeeMapper).save(newAdd);

        employeeService.save(newAdd);

        verify(employeeMapper, times(1)).save(newAdd);

    }
}
