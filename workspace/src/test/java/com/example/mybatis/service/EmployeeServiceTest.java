package com.example.mybatis.service;

import com.example.mybatis.dao.EmployeeMapper;
import com.example.mybatis.dto.EmployeeAddRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

// Mockを有効化
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    // MapperをMockにする
    @Mock
    EmployeeMapper employeeMapper;

    // MockMapperを注入してServiceを構成
    @InjectMocks
    EmployeeService employeeService;

    /**
     * 新規登録
     */
    @Nested
    @DisplayName("新規登録")
    class addEmployee {

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


}
