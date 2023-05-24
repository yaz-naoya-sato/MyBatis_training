package com.example.mybatis.service;

import com.example.mybatis.dao.EmployeeMapper;
import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.dto.EmployeeUpdateRequest;
import com.example.mybatis.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        @DisplayName("新規登録")
        void save() {

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

        @Test
        @DisplayName("社員ID重複チェック")
        void isInsertDuplication() {

            Boolean bool =  employeeService.isInsertDuplication("YZ00000001");

            assertTrue(bool);

        }

    }

    @Nested
    @DisplayName("参照")
    class getEmployee {

        @Test
        @DisplayName("全量取得")
        void findAll() {

            // EmployeeMapperのfindAll()に仮の戻り値を設定
            Employee employee = new Employee();
            employee.setId(4);
            employee.setEmployeeId("YZ00000001");
            employee.setFamilyName("やじゅ");
            employee.setFirstName("太郎");
            employee.setSectionId(1);
            employee.setMail("taro_yaz@yaz.co.jp");
            employee.setGenderId(1);

            when(employeeMapper.findAll()).thenReturn(List.of(
                    employee
            ));

            // テスト対象のメソッドを実行
            List<Employee> list = employeeService.findAll();

            // テスト対象の戻り値を検証
            assertEquals(1, list.size());

            // EmployeeMapperのfindAll()が1回呼ばれていることをチェック
            verify(employeeMapper, times(1)).findAll();

        }

        @Test
        @DisplayName("単一取得")
        void findById() {

            // EmployeeMapperのfindById()に仮の戻り値を設定
            Employee employee = new Employee();
            employee.setId(4);
            employee.setEmployeeId("YZ00000001");
            employee.setFamilyName("やじゅ");
            employee.setFirstName("太郎");
            employee.setSectionId(1);
            employee.setMail("taro_yaz@yaz.co.jp");
            employee.setGenderId(1);

            when(employeeMapper.findById(employee.getId())).thenReturn(
                    employee
            );

            // テスト対象のメソッドを実行
            Employee test = employeeService.findById(employee.getId());

            // テスト対象の戻り値を検証
            assertEquals(test.getId(), employee.getId());
            assertEquals(test.getEmployeeId(), employee.getEmployeeId());
            assertEquals(test.getFamilyName(), employee.getFamilyName());
            assertEquals(test.getFirstName(), employee.getFirstName());
            assertEquals(test.getSectionId(), employee.getSectionId());
            assertEquals(test.getMail(), employee.getMail());
            assertEquals(test.getGenderId(), employee.getGenderId());

            // EmployeeMapperのfindAll()が1回呼ばれていることをチェック
            verify(employeeMapper, times(1)).findById(employee.getId());

        }
    }

    @Nested
    @DisplayName("更新")
    class updEmployee {

        @Nested
        @DisplayName("正常系")
        class updSuccess {

            @Test
            @DisplayName("更新可能")
            public void update() {

                EmployeeUpdateRequest upd = new EmployeeUpdateRequest();
                upd.setId(4);
                upd.setEmployeeId("YZ00000001");
                upd.setFamilyName("やじゅ");
                upd.setFirstName("てすと");
                upd.setSectionId(1);
                upd.setMail("test_yaz@yaz.co.jp");
                upd.setGenderId(1);

                doNothing().when(employeeMapper).update(upd);

                employeeService.update(upd);

                verify(employeeMapper, times(1)).update(upd);


            }

            @Test
            @DisplayName("削除可能")
            public void delete() {

                Employee upd = new Employee();
                upd.setId(4);
                upd.setEmployeeId("YZ00000001");
                upd.setFamilyName("やじゅ");
                upd.setFirstName("てすと");
                upd.setSectionId(1);
                upd.setMail("test_yaz@yaz.co.jp");
                upd.setGenderId(1);

                doNothing().when(employeeMapper).delete(4);

                employeeService.delete(upd);

                verify(employeeMapper, times(1)).delete(4);


            }

        }

    }


}
