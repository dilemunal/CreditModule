package com.example.creditmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
