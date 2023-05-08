package com.example.mybatis.controller;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {EmployeeService.class}
))
@AutoConfigureMybatis
public class CreateControllerTest {

    @Autowired
    MockMvc mvc;

    @Nested
    class addEmployee {

        final MultiValueMap<String, String> validData =
                new LinkedMultiValueMap<>() {{
                    add("employeeId", "YZ00000011");
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
        void validateCheck_idNgPatter6() throws Exception {
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
                        add("sectionId","9");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","9");
                    }};

            mvc.perform(createRequest(invalidData))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_reg"));
        }

        @Test
        @DisplayName("正常系 - バリデーションエラーなし")
        void validateCheck_ok() throws Exception {
            mvc.perform(createRequest(validData))
                    .andExpect(status().isOk())
                    .andExpect(redirectedUrl("employees/employee_result"));
        }


    }
}
