package com.employee.LeaveService.enums;

public enum EmployeeDesignationEnum {
    CEO,
    CTO,
    HR,
    PROJECT_MANAGER,
    TEAM_LEADER,
    SENIOR_DEVELOPER,
    JUNIOR_DEVELOPER,
    STAFF;

    // Helper method to check leave approval authority
    public boolean canApproveLeave() {
        return this == CEO || this == CTO || this == TEAM_LEADER || this == PROJECT_MANAGER || this == HR;
    }
}
