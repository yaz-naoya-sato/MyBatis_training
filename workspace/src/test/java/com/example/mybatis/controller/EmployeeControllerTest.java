package com.example.mybatis.controller;

import com.example.mybatis.service.EmployeeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // @Orderアノテーションででテスト順序を指定したい場合
@ExtendWith(MockitoExtension.class) // JUnit+Mockitoの場合
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // デフォルトはPER_METHOD
public class EmployeeControllerTest {

    // Spring MVCのモック
    @Autowired
    private MockMvc mvc;

    // ServiceをMockにする
    @Mock
    private EmployeeService employeeService;

    // MockServiceを注入してControllerを構成
    @Spy
    @InjectMocks
    private EmployeeController employeeController;

    @BeforeAll
    static void initAll() {}

    @BeforeEach
    void init() {
        // Spring MVCのモックを設定する
        this.mvc = MockMvcBuilders.standaloneSetup(employeeController).build();

    }

        @Test
        public void test() {

            final MultiValueMap<String, String> validData =
                    new LinkedMultiValueMap<>() {{
                        add("employeeId", "YZ00000001");
                        add("familyName","やじゅ");
                        add("firstName","太郎");
                        add("sectionId","1");
                        add("mail","taro_yaz@yaz.co.jp");
                        add("genderId","1");
                    }};

            // スタブ化されたserviceのisInsertDuplicationメソッドは、初期状態では必ずFALSEを返す
            assertFalse(employeeService.isInsertDuplication(validData.getFirst("employeeId")));

            // thenメソッド系
            // when(モックオブジェクト.メソッド(任意の引数))： スタブを可能とするメソッドです。
            // 特定のメソッドを呼び出して、その返り値に特定の値を入れる時に使います。
            // thenReturn(返り値)： スタブメソッドの返り値を指定するのに使います。

            // EmployeeIdに"YZ00000001"を入れた時、true(重複なし)が返ってくるように
            // Serviceモックオブジェクトのスタブ化されたisInsertDuplicationメソッドを定義
            when(employeeService.isInsertDuplication(validData.getFirst("employeeId"))).thenReturn(true);

            // when後はTrueを返却するようになる
            assertTrue(employeeService.isInsertDuplication(validData.getFirst("employeeId")));

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post("/employees/employee_reg")
                    .params(validData)
                    .accept(MediaType.TEXT_HTML);

            try {
                this.mvc.perform(requestBuilder).andExpect(status().isOk());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

    @Test
    public void exceptionTest() {

        final MultiValueMap<String, String> validData =
                new LinkedMultiValueMap<>() {{
                    add("employeeId", "YZ00000001");
                    add("familyName","やじゅ");
                    add("firstName","太郎");
                    add("sectionId","1");
                    add("mail","taro_yaz@yaz.co.jp");
                    add("genderId","1");
                }};

        // EmployeeIdに"YZ00000001"を入れた時、true(重複なし)が返ってくるように
        // Serviceモックオブジェクトのスタブ化されたisInsertDuplicationメソッドを定義
        when(employeeService.isInsertDuplication(validData.getFirst("employeeId"))).thenReturn(true);


        // 非検索例外(RuntimeException系)ならスローできる
        doThrow(new RuntimeException(new Exception())).when(employeeService).save(Mockito.any());


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/employees/employee_reg")
                .params(validData)
                .accept(MediaType.TEXT_HTML);

        try {
            this.mvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_result"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
