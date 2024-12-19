package com.example.creditmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
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
