package com.example.mybatis.service;

import com.example.mybatis.common.LogUtil;
import com.example.mybatis.dto.EmployeeAddRequest;
import com.example.mybatis.dao.EmployeeMapper;
import com.example.mybatis.dto.EmployeeUpdateRequest;
import com.example.mybatis.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {



    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 社員重複確認
     * @param employeeId
     * @return
     */
    public boolean isInsertDuplication(String employeeId) {

        Employee employee = employeeMapper.findByEmployeeId(employeeId).orElse(null);
        return employee == null;

    }

    /**
     * 社員情報の登録・更新
     * @param employeeAddRequest
     */
    public void save(EmployeeAddRequest employeeAddRequest) {
        employeeMapper.save(employeeAddRequest);
    }

    /**
     * 全社員の取得
     * @return
     */
    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    /**
     * IDで社員検索
     */
    public Employee findById(Integer id) {

        return employeeMapper.findById(id);

    }

    /**
     * 社員情報更新サービスメソッド
     *
     * @param employee 社員情報
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class) // メソッド開始時にトランザクションを開始、終了時にコミットする
    public void update(EmployeeUpdateRequest employee) throws IllegalArgumentException, DataAccessException {

        try {
            LogUtil.getLogger();

            // employeeエンティティの中身をDBに登録
            employeeMapper.update(employee);

        } catch (DuplicateKeyException ex) {
            // データ整合性エラー
            throw ex;
        } catch (IllegalArgumentException ex) {
            // 不正、不適切な引数エラー
            throw ex;
        } catch (DataAccessException ex) {
            // データアクセス例外
            throw ex;
        }

    }

    @Transactional(rollbackFor = Exception.class) // メソッド開始時にトランザクションを開始、終了時にコミットする
    public void delete(Employee employee) throws IllegalArgumentException, DataAccessException {

        try {

            // JpaRepositoryから継承したdeleteメソッドを使用
            // employeeエンティティの中身をDBから削除
            employeeMapper.delete(employee.getId());


        } catch (IllegalArgumentException ex) {
            // 不正、不適切な引数エラー
            throw ex;
        } catch (OptimisticLockingFailureException ex) {
            // 対象データなしエラー
            throw ex;
        } catch (DataAccessException ex) {
            // データアクセス例外
            throw ex;
        }

    }
}
