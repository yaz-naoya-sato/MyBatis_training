package com.example.mybatis.controller;

import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.dto.EmployeeSearchRequest;
import com.example.mybatis.entity.Employee;
import com.example.mybatis.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

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
    @RequestMapping(value = "/employee_reg", method = RequestMethod.POST)
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
        model.addAttribute("employeeSearchRequest", new EmployeeSearchRequest());

        return "employees/employee_list";
    }


}
