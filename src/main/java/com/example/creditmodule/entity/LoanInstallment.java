package com.example.creditmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private Double paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Boolean isPaid;
    @ManyToOne
    @JoinColumn(name = "loanId")
    private Loan loan;


}
