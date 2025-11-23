package com.sky.controller.admin;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理
 */
@RestController
@RequestMapping("admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);
        EmployeeLoginVO employeeLoginVO = employeeService.login(employeeLoginDTO);
        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    public Result logout() {
        log.info("员工退出登录");
        return Result.success();
    }

    @PostMapping
    public Result createEmp(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工:{}", employeeDTO);
        employeeService.createEmp(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<Employee>> SelectEmpByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询员工：{}", employeePageQueryDTO);
        PageResult<Employee> pageResult = employeeService.selectEmpByPage(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    public Result updateEmpStatus(@PathVariable Integer status, Long id) {
        log.info("修改员工状态：{},{}", status, id);
        employeeService.updateEmpStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Employee> selectEmpById(@PathVariable Long id) {
        log.info("根据id查询员工：{}", id);
        Employee employee = employeeService.selectEmpById(id);
        return Result.success(employee);
    }

    @PutMapping
    public Result updateEmp(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息：{}", employeeDTO);
        employeeService.updateEmp(employeeDTO);
        return Result.success();
    }

    @PutMapping("/editPassword")
    public Result updateEmpPassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改员工密码：{}", passwordEditDTO);
        employeeService.updateEmpPassword(passwordEditDTO);
        return Result.success();
    }
}
