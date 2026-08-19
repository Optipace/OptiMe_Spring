package com.employee.EmployeeProfileService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="employee_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocument {
    @Id
    @Column(name="employee_id")
    private Long employeeId;

    @OneToOne
    @MapsId
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Column(name="document_no")
    private String documentNo;

    @Column(name="sslc_certificate")
    private String sslcCertificate;

    @Column(name="sslc_document_no")
    private String sslcDocumentNo;

    @Column(name="pan_card")
    private String panCard;

    @Column(name="pan_card_document_no")
    private String panCardDocumentNo;

    @Column(name="aadhar_card")
    private String aadharCard;

    @Column(name="aadhar_card_document_no")
    private String aadharCardDocumentNo;

    @Column(name="path")
    private String path;
}
