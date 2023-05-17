package com.example.mybatis.controller;

import com.example.mybatis.entity.Employee;
import com.example.mybatis.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMybatis
@Transactional
public class EmployeeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    /**
     * 初期表示
     */
    @Nested
    class initEmployee {

        MockHttpServletRequestBuilder displayCreateRequest() {
            return get("/employees/employee_reg")
                    .accept(MediaType.TEXT_HTML);
        }

        MockHttpServletRequestBuilder displayDetailRequest() {
            return get("/employees/employee_detail?id=2")
                    .accept(MediaType.TEXT_HTML);
        }

        @Test
        void findAllTest() throws Exception {
            mvc.perform(displayCreateRequest())
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void detail() throws Exception {
            mvc.perform(displayDetailRequest())
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
//                    .andExpect(view().name("employees/employee_detail"));
        }

    }

    /**
     * 新規登録
     */
    @Nested
    @DisplayName("新規登録")
    class addEmployee {

        final MultiValueMap<String, String> validData =
                new LinkedMultiValueMap<>() {{
                    add("employeeId", "YZ00000001");
                    add("familyName","やじゅ");
                    add("firstName","太郎");
                    add("sectionId","1");
                    add("mail","taro_yaz@yaz.co.jp");
                    add("genderId","1");
                }};

        MockHttpServletRequestBuilder createRequest(MultiValueMap<String, String> formData) {
            return post("/employees/employee_reg")
                    .params(formData)
                    .accept(MediaType.TEXT_HTML);
        }

        @Test
        @DisplayName("異常系 - 社員IDを送信しない")
        void validateCheck_idUndefine() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
//                        add("employeeId", "YZ00000011");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDをNULLで送信")
        void validateCheck_idNull() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", null);
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを空で送信")
        void validateCheck_idEmpty() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを9桁で送信")
        void validateCheck_idLessThan() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ0000001");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを11桁で送信")
        void validateCheck_idGreaterThan() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ000000001");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを書式エラーで送信(AB12345678)")
        void validateCheck_idNgPatter1() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "AB12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを書式エラーで送信(YZABCDEFGH)")
        void validateCheck_idNgPatter2() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZABCDEFGH");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを書式エラーで送信(YZ１２３４５６７８)")
        void validateCheck_idNgPatter3() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ１２３４５６７８");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを書式エラーで送信(ＹＺ12345678)")
        void validateCheck_idNgPatter4() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "ＹＺ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員IDを書式エラーで送信(yz12345678)")
        void validateCheck_idNgPatter5() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "yz12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 登録済みIDと重複する社員IDを送信")
        void validateCheck_duplication() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ00000001");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(姓)を送信しない")
        void validateCheck_familyNameUndefined() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        //add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(姓)をNULLで送信")
        void validateCheck_familyNameNull() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName",null);
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(姓)を空で送信")
        void validateCheck_familyNameEmpty() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(姓)を全角21桁で送信")
        void validateCheck_familyNameFullGreaterThan() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ４５６７８９０１２３４５６７８９０１");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(姓)を半角21桁で送信")
        void validateCheck_familyNameHalfGreaterThan() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","yaz456789012345678901");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(名)を送信しない")
        void validateCheck_firstNameUndefined() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        //add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(名)をNULLで送信")
        void validateCheck_firstNameNull() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName",null);
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - 社員名(名)を空で送信")
        void validateCheck_firstNameEmpty() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_firstNameFullGreaterThan() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎３４５６７８９０１２３４５６７８９０１");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_firstNameHalfGreaterThan() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","taro56789012345678901");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_sectionIdUndefined() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        //add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_sectionIdNull() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","たろう");
                        add("sectionId",null);
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_sectionIdEmpty() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_sectionIdUnexpected() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","9");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_mailUndefined() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        //add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_mailNull() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","たろう");
                        add("sectionId","1");
                        add("mail",null);
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_mailEmpty() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","");
                        add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @Transactional
        void validateCheck_genderIdUndefined() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        //add("genderId","1");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_genderIdNull() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","たろう");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId",null);
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_genderIdEmpty() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        void validateCheck_genderIdUnexpected() throws Exception {
            MultiValueMap<String, String> invalidData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ12345678");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","9");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("異常系 - Exception発生")
        void validateCheck_err() throws Exception {

            // PostgreSQLをロック
            // List<Employee> employeeList = employeeService.testFindAll();

            mvc.perform(createRequest(validData))
                    .andExpect(status().isExpectationFailed())
                    .andExpect(view().name("employees/employee_result"));
        }

        @Test
        @DisplayName("正常系 - バリデーションエラーなし")
        void validateCheck_ok() throws Exception {

            mvc.perform(createRequest(validData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_result"));
        }


    }

    /**
     * 一覧表示
     */
    @Nested
    class findEmployee {

        MockHttpServletRequestBuilder listRequest() {
            return get("/employees/employee_list")
                    .accept(MediaType.TEXT_HTML);
        }

        @Test
        void findAllTest() throws Exception {
            mvc.perform(listRequest())
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_list"));
        }

        // 本来は内容比較などの確認も実施する
        @Test
        void findAllOfOne() throws Exception {
            mvc.perform(listRequest())
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_list"));
        }

    }

    /**
     * 詳細表示
     */
    @Nested
    class getEmployee {
        MockHttpServletRequestBuilder displayDetailRequest() {
            return get("/employees/employee_detail?id=2")
                    .accept(MediaType.TEXT_HTML);
        }

        @Test
        void detail() throws Exception {
            mvc.perform(displayDetailRequest())
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
//                    .andExpect(view().name("employees/employee_detail"));
        }
    }
}
