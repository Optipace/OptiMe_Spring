package com.employee.EmployeeProfileService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Column(name="account_number",nullable = false)
    private String accountNumber;

    @Column(name="ifsc_code",nullable = false,length = 11)
    private String ifscCode;

    @Column(name="bank_name",nullable = false)
    private String bankName;

    @Column(name="branch_name")
    private String branchName;

    @Column(name="is_active")
    private boolean active;
}
