package com.example.creditmodule.repository;

import com.example.creditmodule.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment,Long> {
}
