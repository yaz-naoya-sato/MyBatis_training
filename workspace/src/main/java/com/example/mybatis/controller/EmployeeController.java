package com.example.mybatis.controller;

import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.dto.EmployeeUpdateRequest;
import com.example.mybatis.entity.Employee;
import com.example.mybatis.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employees")
@SessionAttributes(types = Employee.class)
public class EmployeeController {
    // LOGGER 共通化実施予定 TODO
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    @Autowired
    private EmployeeService employeeService;

    /**
     * セッション作成
     * @return
     */
    @ModelAttribute("employee")
    private Employee setEmployee() {
        return new Employee();
    }

    /**
     * 社員情報登録画面の呼び出し
     * @return 社員情報登録画面
     */
    @GetMapping("/employee_reg")
    public String displayReg(Employee employee, Model model) {
        model.addAttribute("employeeAddRequest",new EmployeeAddRequest());
        // 社員情報登録画面の返却
        return "employees/employee_reg";
    }

    /**
     * ユーザー新規登録
     * @param employeeAddRequest リクエストデータ
     * @param model Model
     * @return ユーザー情報一覧画面
     */
    @PostMapping(value = "/employee_reg", params="employeeReg")
    @Transactional
    public String create(@Validated @ModelAttribute EmployeeAddRequest employeeAddRequest,
                         BindingResult result,
                         Model model) {

        // 社員ID重複チェック(更新時のBeanValidationを考慮)
        if (!employeeService.isInsertDuplication(employeeAddRequest.getEmployeeId())){
            FieldError fieldError = new FieldError(result.getObjectName(), "employeeId", "社員IDが重複しています");

            result.addError(fieldError);
        }

        if (result.hasErrors()) {
            // 入力チェックエラーの場合
            List<String> errorList = new ArrayList<String>();
            for (ObjectError error : result.getAllErrors()) {
                errorList.add(error.getDefaultMessage());
            }
            model.addAttribute("validationError", errorList);
            model.addAttribute("employeeAddRequest", employeeAddRequest);
            return "employees/employee_reg";
        }

        try{
            // ユーザー情報の登録
            employeeService.save(employeeAddRequest);
        } catch (Exception ex) {
            model.addAttribute("res","データ登録に失敗しました");
            return "employees/employee_result";
        }

        model.addAttribute("res", "データを登録しました");
        return "employees/employee_result";
    }

    /**
     * 社員情報一覧画面の呼び出し＆会員一覧表示
     * @param model
     * @return
     */
    //社員一覧
    @GetMapping("/employee_list")
    public String displayList(Model model) {
        List<Employee> employeeList = employeeService.findAll();

        // 変数名employeesに値をセット
        model.addAttribute("employeelist", employeeList);

        return "employees/employee_list";
    }

    /**
     * 社員情報参照画面の表示
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/employee_detail")
    public String displayDetail(@RequestParam("id") Integer id, Model model){

        // IDによるEmployeeテーブルの検索
        Employee employee = employeeService.findById(id);

        model.addAttribute("employee",employee);

        // 社員情報詳細画面の返却
        return "employees/employee_detail";
    }

    /**
     * 社員情報編集画面の表示
     * (社員情報参照画面の情報をsessionで持つ)
     * @return
     */
    @PostMapping("/employee_edit")
    public String initEdit(@ModelAttribute("employee") Employee employeeSession, Model model) {

        // employeeテーブルの存在チェック
        if(null == employeeService.findById(employeeSession.getId())){
            model.addAttribute("res","データ更新対象が存在しません");
            return "employees/employee_result";
        }

        EmployeeUpdateRequest employeeUpd = new EmployeeUpdateRequest();
        employeeUpd.setId(employeeSession.getId());
        employeeUpd.setEmployeeId(employeeSession.getEmployeeId());
        employeeUpd.setFamilyName(employeeSession.getFamilyName());
        employeeUpd.setFirstName(employeeSession.getFirstName());
        employeeUpd.setSectionId(employeeSession.getSectionId());
        employeeUpd.setMail(employeeSession.getMail());
        employeeUpd.setGenderId(employeeSession.getGenderId());

        model.addAttribute("employeeUpdateRequest", employeeUpd);

        // employeeをSessionで保持しているため、modelへのadd不要
        return "employees/employee_edit";

    }

    /**
     * Postされた社員情報のDB編集
     * @param employeeUpdateRequest 更新リクエストデータ
     * @param bindingResult バリデーション結果を表すI/F
     * @param model モデル属性を定義
     * @return 社員情報登録_結果画面
     */
    @PostMapping(value="/employee_edit", params="employeeEdit")
    public String employeeEdit(@ModelAttribute @Validated EmployeeUpdateRequest employeeUpdateRequest,
                               BindingResult bindingResult,
                               Model model) {

        logger.debug("POSTされた社員情報のバリデーションを実施します。");
        // 入力チェック判定

        if (bindingResult.hasErrors()){
            logger.warn("入力チェックエラーが発生しました。");

            // 入力チェックエラーの場合
            List<String> errorList = new ArrayList<String>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorList.add(error.getDefaultMessage());
            }
            model.addAttribute("validationError", errorList);
            model.addAttribute("employeeUpdateRequest", employeeUpdateRequest);

            return "employees/employee_edit";
        }

        try {
            logger.debug("社員情報更新メソッド(service)の呼び出しを実施します。");

            // employeeテーブルの存在チェック
            if(null == employeeService.findById(employeeUpdateRequest.getId())){
                model.addAttribute("res","データ更新対象が存在しません");
            } else {
                // update
                employeeService.update(employeeUpdateRequest);

                // DBコミットが成功した場合は、成功メッセージを表示
                model.addAttribute("res", "データを更新しました");
            }


            // catchが冗長のため共通化対応 TODO
        } catch (CannotCreateTransactionException ex) {
            // トランザクションを作成できない場合は、失敗メッセージを表示
            logger.error("トランザクションの作成に失敗しました。\r\n" + ex);
            model.addAttribute("res","データ更新に失敗しました");
        } catch (IllegalArgumentException ex) {
            // 不正、不適切な引数エラー
            logger.error("不正な引数が渡されました。\r\n" + ex);
            model.addAttribute("res","データ更新に失敗しました");
        } catch (DataAccessException ex) {
            // データアクセス例外
            logger.error("データアクセス例外が発生しました。\r\n" + ex);
            model.addAttribute("res","データ更新に失敗しました");
        } catch (Exception ex) {
            // DBコミットが失敗した場合は、失敗メッセージを表示
            logger.error("予期しないエラーが発生しました。\r\n" + ex);
            model.addAttribute("res","データ更新に失敗しました");
        }
        // employeeに入力フォームの内容が格納されているため初期化
        model.addAttribute("employee", new Employee());

        // 社員情報登録_結果画面の返却
        return "employees/employee_result";
    }

    /**
     * Postされた社員情報の削除
     * @param employee 削除対象の社員情報
     * @param model
     * @return 社員情報削除_結果画面
     */
    @PostMapping(value="/employee_result", params="employeeDelete")
    public String employeeDelete(@ModelAttribute Employee employee,
                                 Model model) {

        try {

            // employeeテーブルの存在チェック
            if(null == employeeService.findById(employee.getId())){
                model.addAttribute("res","データ削除対象が存在しません");
            } else {
                // update
                employeeService.delete(employee);

                // DBコミットが成功した場合は、成功メッセージを表示
                model.addAttribute("res", "データを削除しました");
            }

            // catchが冗長のため共通化対応 TODO
        } catch (CannotCreateTransactionException ex) {
            // トランザクションを作成できない場合は、失敗メッセージを表示
            model.addAttribute("res","データ削除に失敗しました");
        } catch (IllegalArgumentException ex) {
            // 不正、不適切な引数エラー
            model.addAttribute("res","データ削除に失敗しました");
        } catch (OptimisticLockingFailureException ex) {
            // 対象データなしエラー
            model.addAttribute("res","データ削除に失敗しました");
        } catch (DataAccessException ex) {
            // データアクセス例外
            model.addAttribute("res","データ削除に失敗しました");
        } catch (Exception ex) {
            // DBコミットが失敗した場合は、失敗メッセージを表示
            model.addAttribute("res","データ削除に失敗しました");
        }
        // 社員情報登録_結果画面の返却
        return "employees/employee_result";
    }


}
