package com.example.employee.repository;

import com.example.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findBySalaryGreaterThan(BigDecimal amount);
    List<Employee> findByDepartmentId(Long departmentId);
    List<Employee> findByDepartmentIsNullOrderByIdAsc();
    boolean existsByEmailIgnoreCase(String email);
    Optional<Employee> findByEmailIgnoreCase(String email);
    @Query("select e from Employee e where lower(e.name) like lower(concat('%', :keyword, '%'))")
    List<Employee> searchByNameKeyword(@Param("keyword") String keyword);
    @Query(value = "select * from employees where salary between :minimum and :maximum order by salary",
           countQuery = "select count(*) from employees where salary between :minimum and :maximum", nativeQuery = true)
    Page<Employee> findBySalaryRangeNative(@Param("minimum") BigDecimal minimum,
                                            @Param("maximum") BigDecimal maximum, Pageable pageable);
}
