package com.example.creditmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double loanAmount;
    private Integer numberOfInstallment;
    private LocalDate createDate;
    private Boolean isPaid;
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

}
