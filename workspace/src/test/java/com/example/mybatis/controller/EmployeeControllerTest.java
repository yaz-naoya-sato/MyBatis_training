package com.example.mybatis.controller;

import com.example.mybatis.entity.Employee;
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
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    static void initAll() {
    }

    @BeforeEach
    void init() {
        // Spring MVCのモックを設定する
        this.mvc = MockMvcBuilders.standaloneSetup(employeeController).build();

    }

    /**
     * 新規登録用 正常系パラメータ生成
     *
     * @return
     */
    private MultiValueMap<String, String> saveOkParams() {
        return new LinkedMultiValueMap<>() {{
            add("employeeId", "YZ00000001");
            add("familyName", "やじゅ");
            add("firstName", "太郎");
            add("sectionId", "1");
            add("mail", "taro_yaz@yaz.co.jp");
            add("genderId", "1");
        }};
    }

    /**
     * 更新用 正常系パラメータ生成
     *
     * @return
     */
    private MultiValueMap<String, String> updOkParams() {
        return new LinkedMultiValueMap<>() {{
            add("id", "4");
            add("employeeId", "YZ00000001");
            add("familyName", "変更");
            add("firstName", "花子");
            add("sectionId", "2");
            add("mail", "hanako_yaz@yaz.co.jp");
            add("genderId", "2");
        }};
    }

    /**
     * パラメータ変更
     *
     * @param replaceParams
     * @return
     */
    private MultiValueMap<String, String> customizeParams(MultiValueMap<String, String> replaceParams) {
        var params = saveOkParams();
        for (var e : replaceParams.entrySet()) {
            String key = e.getKey();
            params.remove(key);
            params.addAll(key, replaceParams.get(key));
        }
        return params;
    }

//    private void execXxxNg(MultiValueMap<String, String> ngParams, String... messages) throws Exception {
//        mvc.perform(post("/employees/employee_edit")                // 更新
//                        .param("employeeEdit", "employeeEdit")   // POSTリクエストパラメーター
//                        .params(customizeParams(ngParams))                     // 更新用パラメータ
//                        .accept(MediaType.TEXT_HTML))
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("validationError", new AssertionMatcher<String>() {
//                    @Override
//                    public void assertion(String actual) throws AssertionError {
//                        Assertions.assertEquals(Arrays.stream(actual.split("\n")).sorted().collect(toList())
//                                ,(Arrays.stream(messages).sorted().collect(toList())));
//                    }
//                }));
//    }

    @Nested
    @DisplayName("初期表示")
    class initEmployee {
        @Test
        @DisplayName("新規登録画面")
        public void initSave() throws Exception {

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/employees/employee_reg") // 新規登録画面をGETで取得
                    .accept(MediaType.TEXT_HTML);

            mvc.perform(requestBuilder)
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("一覧画面")
        public void initList() throws Exception {

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/employees/employee_list") // 一覧画面をGETで取得
                    .accept(MediaType.TEXT_HTML);

            List<Employee> employeeList = employeeService.findAll();

            mvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(view().name("employees/employee_list"))
                    .andExpect(model().attribute("employeelist",employeeList));

        }

        @Test
        @DisplayName("参照画面")
        public void initDetail() throws Exception {

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/employees/employee_detail") // 一覧画面をGETで取得
                    .param("id", "4")
                    .accept(MediaType.TEXT_HTML);

            mvc.perform(requestBuilder)
                    .andExpect(status().isOk())
//                    .andExpect(model().attribute("id", "4"))
            ;
        }

        @Test
        @DisplayName("編集画面")
        public void initEdit() throws Exception {

            Employee employee = new Employee();
            employee.setId(4);
            employee.setEmployeeId("YZ00000001");
            employee.setFamilyName("やじゅ");
            employee.setFirstName("太郎");
            employee.setSectionId(1);
            employee.setMail("taro_yaz@yaz.co.jp");
            employee.setGenderId(1);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post("/employees/employee_edit") // 一覧画面をGETで取得
                    // Sessionを渡す　TODO
                    .accept(MediaType.TEXT_HTML);

            mvc.perform(requestBuilder)
                    .andExpect(status().isOk());
        }

    }

    @Nested
    @DisplayName("新規登録")
    class saveEmployee {

        @Nested
        @DisplayName("異常系")
        class saveError {

            @Test
            @DisplayName("予期しないエラー")
            public void exceptionTest() throws Exception {

                // EmployeeIdに"YZ00000001"を入れた時、true(重複なし)が返ってくるように
                // Serviceモックオブジェクトのスタブ化されたisInsertDuplicationメソッドを定義
                when(employeeService.isInsertDuplication(saveOkParams().getFirst("employeeId"))).thenReturn(true);

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new RuntimeException(new Exception())).when(employeeService).save(Mockito.any());

                RequestBuilder requestBuilder = post("/employees/employee_reg")
                        .param("employeeReg", "employeeReg") // POSTリクエストパラメーター
                        .params(saveOkParams())
                        .accept(MediaType.TEXT_HTML);

                mvc.perform(requestBuilder)
                        .andExpect(status().isOk())
                        .andExpect(view().name("employees/employee_result"))
                        .andExpect(model().attribute("res", "データ登録に失敗しました"));
            }

        }

        @Nested
        @DisplayName("バリデーションエラー系")
        class saveValidate {

            @Test
            @DisplayName("社員ID未定義")
            public void saveEmployeeIdUndefined() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            //add("employeeId", "YZ00000001");
                            add("familyName", "やじゅ");
                            add("firstName", "太郎");
                            add("sectionId", "1");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                when(employeeService.isInsertDuplication(params.getFirst("employeeId"))).thenReturn(true);

                RequestBuilder requestBuilder = post("/employees/employee_reg") // 新規登録
                        .param("employeeReg", "employeeReg") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);

                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_reg")) // employee_regに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDを入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か
            }

            @Test
            @DisplayName("社員IDnull")
            public void saveEmployeeIdNull() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("employeeId", null);
                            add("familyName", "やじゅ");
                            add("firstName", "太郎");
                            add("sectionId", "1");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                when(employeeService.isInsertDuplication(params.getFirst("employeeId"))).thenReturn(true);

                RequestBuilder requestBuilder = post("/employees/employee_reg") // 新規登録
                        .param("employeeReg", "employeeReg") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);

                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_reg")) // employee_regに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDを入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か
            }

            @Test
            @DisplayName("社員IDが9桁")
            public void saveEmployeeIdLessThan() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("employeeId", "YZ0000001");
                            add("familyName", "やじゅ");
                            add("firstName", "太郎");
                            add("sectionId", "1");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                when(employeeService.isInsertDuplication(params.getFirst("employeeId"))).thenReturn(true);

                RequestBuilder requestBuilder = post("/employees/employee_reg") // 新規登録
                        .param("employeeReg", "employeeReg") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);

                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_reg")) // employee_regに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDは10文字で入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(2)); // エラー数は1件か
            }

            @Test
            @DisplayName("社員IDが10桁")
            public void saveEmployeeIdGreaterThan() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("employeeId", "YZ000000001");
                            add("familyName", "やじゅ");
                            add("firstName", "太郎");
                            add("sectionId", "1");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                when(employeeService.isInsertDuplication(params.getFirst("employeeId"))).thenReturn(true);

                RequestBuilder requestBuilder = post("/employees/employee_reg") // 新規登録
                        .param("employeeReg", "employeeReg") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);

                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_reg")) // employee_regに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDは10文字で入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(2)); // エラー数は1件か
            }

            @Test
            @DisplayName("社員IDが重複")
            public void saveEmployeeIdDuplication() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("employeeId", "YZ00000001");
                            add("familyName", "やじゅ");
                            add("firstName", "太郎");
                            add("sectionId", "1");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                // DB上、重複するIDをPOSTするが、重複チェックメソッドも明示的にFalseとなるよう設定
                when(employeeService.isInsertDuplication(params.getFirst("employeeId"))).thenReturn(false);

                RequestBuilder requestBuilder = post("/employees/employee_reg") // 新規登録
                        .param("employeeReg", "employeeReg") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);

                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_reg")) // employee_regに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDが重複しています"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か
            }
        }

        @Nested
        @DisplayName("正常系")
        class saveSuccess {

            @Test
            @DisplayName("登録可能")
            public void test() throws Exception {

                // thenメソッド系
                // when(モックオブジェクト.メソッド(任意の引数))： スタブを可能とするメソッドです。
                // 特定のメソッドを呼び出して、その返り値に特定の値を入れる時に使います。
                // thenReturn(返り値)： スタブメソッドの返り値を指定するのに使います。

                // EmployeeIdに"YZ00000001"を入れた時、true(重複なし)が返ってくるように
                // Serviceモックオブジェクトのスタブ化されたisInsertDuplicationメソッドを定義
                when(employeeService.isInsertDuplication(saveOkParams().getFirst("employeeId"))).thenReturn(true);


                RequestBuilder requestBuilder = post("/employees/employee_reg")
                        .param("employeeReg", "employeeReg") // POSTリクエストパラメーター
                        .params(saveOkParams())
                        .accept(MediaType.TEXT_HTML);

                mvc.perform(requestBuilder)
                        .andExpect(status().isOk())
                        .andExpect(view().name("employees/employee_result"))
                        .andExpect(model().attribute("res", "データを登録しました"))
                        .andExpect(model().hasNoErrors());


            }

        }
    }


    @Nested
    @DisplayName("更新")
    class updEmployee {

        @Nested
        @DisplayName("異常系")
        class updError {

            @Test
            @DisplayName("GETで呼び出し")
            public void updateGetTest() throws Exception {

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .get("/employees/employee_edit") // 更新をGETで実施
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().is4xxClientError()); // 405 METHOD_NOT_ALLOWED となることを確認


            }

            @Test
            @DisplayName("トランザクション作成エラー")
            public void updateCannotCreateTransactionException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new CannotCreateTransactionException("")).when(employeeService).update(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_edit") // 更新をGETで実施
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ更新に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }

            @Test
            @DisplayName("不正、不適切な引数エラー")
            public void updateIllegalArgumentException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new IllegalArgumentException("")).when(employeeService).update(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_edit") // 更新をGETで実施
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ更新に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }

            @Test
            @DisplayName("データアクセスエラー")
            public void updateDataAccessException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new CannotAcquireLockException("")).when(employeeService).update(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_edit") // 更新をGETで実施
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ更新に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }

            @Test
            @DisplayName("予期しないエラー")
            public void updateException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new RuntimeException(new Exception())).when(employeeService).update(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_edit") // 更新をGETで実施
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ更新に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }
        }

        @Nested
        @DisplayName("バリデーションエラー系")
        class updValidate {
            @Test
            @DisplayName("社員ID未定義")
            public void updateEmployeeIdUndefined() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            //add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDを入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員IDNulll")
            public void updateEmployeeIdNull() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", null);
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDを入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員ID空文字")
            public void updateEmployeeIdEmpty() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員IDを入力してください"))); // 想定したエラーメッセージか(リストにメッセージが含まれているか)

            }

            @Test
            @DisplayName("社員名(姓)未定義")
            public void updateFamilyNameUndefined() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
//                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(姓)を入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(姓)Null")
            public void updateFamilyNameNull() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", null);
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(姓)を入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(姓)未入力")
            public void updateFamilyNameEmpty() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(姓)を入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(姓)が全角20文字を超過")
            public void updateFamilyNameFullOver() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更３４５６７８９０１２３４５６７８９０１");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(姓)は20文字以下で入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(姓)が半角20文字を超過")
            public void updateFamilyNameHalfOver() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "henkou789012345678901");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(姓)は20文字以下で入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(姓)未定義")
            public void updateFirstNameUndefined() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
//                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(名)を入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(名)Null")
            public void updateFirstNameNull() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", null);
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(名)を入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(名)未入力")
            public void updateFirstNameEmpty() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};


                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(名)を入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(名)が全角20文字を超過")
            public void updateFirstNameFullOver() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎３４５６７８９０１２３４５６７８９０１");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(名)は20文字以下で入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("社員名(名)が半角20文字を超過")
            public void updateFirstNameHalfOver() throws Exception {

                MultiValueMap<String, String> employeeIdUndefined =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "taro56789012345678901");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(employeeIdUndefined)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("社員名(名)は20文字以下で入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("所属セクション未定義")
            public void updateSectionIdUndefined() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
//                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("所属セクションを選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("所属セクションがNull")
            public void updateSectionIdNull() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", null);
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("所属セクションを選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("所属セクションが空文字")
            public void updateSectionIdEmpty() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("所属セクションを選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("所属セクションが指定値以外")
            public void updateSectionIdOther() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "99");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("所属セクションを選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("メールアドレス未定義")
            public void updateMAilUndefined() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
//                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("メールアドレスを入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("メールアドレスがNull")
            public void updateMailNull() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", null);
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("メールアドレスを入力してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("メールアドレスが空文字")
            public void updateMailEmpty() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("メールアドレスを入力してください"))); // 想定したエラーメッセージか(リストにメッセージが含まれているか)

            }

            @Test
            @DisplayName("メールアドレスが256文字を超過")
            public void updateMailOver() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "a".repeat(257));
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("メールアドレスは256文字以下で入力してください"))); // 想定したエラーメッセージか(リストにメッセージが含まれているか)

            }

            @Test
            @DisplayName("メールアドレス書式誤り")
            public void updateMailFormat() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "a");
                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("メールアドレスを正しく入力してください"))); // 想定したエラーメッセージか(リストにメッセージが含まれているか)

            }

            @Test
            @DisplayName("性別未定義")
            public void updateGenderIdUndefined() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
//                            add("genderId", "1");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("性別を選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("性別がNull")
            public void updateGenderIdNull() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", null);
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("性別を選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("性別が空文字")
            public void updateGenderIdEmpty() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("性別を選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

            @Test
            @DisplayName("性別が指定値以外")
            public void updateGenderIdOther() throws Exception {

                MultiValueMap<String, String> params =
                        new LinkedMultiValueMap<>() {{
                            add("id", "4");
                            add("employeeId", "YZ00000001");
                            add("familyName", "変更");
                            add("firstName", "太郎");
                            add("sectionId", "2");
                            add("mail", "taro_yaz@yaz.co.jp");
                            add("genderId", "99");
                        }};

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(params)                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                ResultActions resultActions = mvc.perform(requestBuilder);
                resultActions.andExpect(status().isOk()) // レスポンスステータスは正常か
                        .andExpect(view().name("employees/employee_edit")) // employee_editに戻ってくるか
                        .andExpect(model().attribute("validationError", hasItem("性別を選択してください"))) // 想定したエラーメッセージか(リストにメッセージが含まれているか)
                        .andExpect(model().errorCount(1)); // エラー数は1件か

            }

        }

        @Nested
        @DisplayName("正常系")
        class updSuccess {
            @Test
            @DisplayName("更新可能")
            public void updateSuccess() throws Exception {

                RequestBuilder requestBuilder = post("/employees/employee_edit") // 更新画面
                        .param("employeeEdit", "employeeEdit") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk())
                        // 次画面の遷移先がemployee_resultであることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データを更新しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }
        }
    }

    @Nested
    @DisplayName("削除")
    class deleteEmployee {

        @Nested
        @DisplayName("異常系")
        class deleteError {

            @Test
            @DisplayName("GETで呼び出し")
            public void deleteGetTest() throws Exception {

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .get("/employees/employee_delete") // 更新をGETで実施
                        .param("employeeDelete", "employeeDelete") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);

                mvc.perform(requestBuilder)
                        .andExpect(status().is4xxClientError()); // 405 METHOD_NOT_ALLOWED となることを確認

            }

            @Test
            @DisplayName("トランザクション作成エラー")
            public void deleteCannotCreateTransactionException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new CannotCreateTransactionException("")).when(employeeService).delete(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_result") // 更新をGETで実施
                        .param("employeeDelete", "employeeDelete") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ削除に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }

            @Test
            @DisplayName("不正、不適切な引数エラー")
            public void deleteIllegalArgumentException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new IllegalArgumentException("")).when(employeeService).delete(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_result") // 削除をPOST
                        .param("employeeDelete", "employeeDelete") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ削除に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }

            @Test
            @DisplayName("削除対象データなしエラー")
            public void deleteOptimisticLockingFailureException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new OptimisticLockingFailureException("")).when(employeeService).delete(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_result") // 更新をGETで実施
                        .param("employeeDelete", "employeeDelete") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ削除に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }

            @Test
            @DisplayName("データアクセスエラー")
            public void deleteDataAccessException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new CannotAcquireLockException("")).when(employeeService).delete(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_result") // 更新をGETで実施
                        .param("employeeDelete", "employeeDelete") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ削除に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }

            @Test
            @DisplayName("予期しないエラー")
            public void deleteException() throws Exception {

                // 非検査例外(RuntimeException系)ならスローできる
                doThrow(new RuntimeException(new Exception())).when(employeeService).delete(Mockito.any());

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_result") // 更新をGETで実施
                        .param("employeeDelete", "employeeDelete") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 更新用パラメータ
                        .accept(MediaType.TEXT_HTML);


                mvc.perform(requestBuilder)
                        .andExpect(status().isOk()) // 405 METHOD_NOT_ALLOWED となることを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データ削除に失敗しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

            }
        }

        @Nested
        @DisplayName("正常系")
        class deleteSuccess {

            @Test
            @DisplayName("削除可能")
            public void delete() throws Exception {

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                        .post("/employees/employee_result") // 削除を実施
                        .param("employeeDelete", "employeeDelete") // POSTリクエストパラメーター
                        .params(updOkParams())                       // 削除用パラメータ
                        .accept(MediaType.TEXT_HTML);

                mvc.perform(requestBuilder)
                        .andExpect(status().isOk())
                        // 200ステータスを返却することを確認
                        .andExpect(view().name("employees/employee_result"))
                        // オブジェクトのresに設定される値が正しいことを確認
                        .andExpect(model().attribute("res", "データを削除しました"))
                        // Modelオブジェクトにエラーがないことを確認
                        .andExpect(model().hasNoErrors());

                // 削除したデータと同じIDでの抽出結果がNullであることを確認
                assertNull(employeeService.findById(4));
            }

        }

    }
}
