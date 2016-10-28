package biometricauthentication.admin.dialog.report.beans;

import biometricauthentication.model.Employee;
import biometricauthentication.model.EmployeeType;

/**
 *
 * @author VakSF
 */
public class ReportRecord {
    
    private Employee employee;
    private EmployeeType employeeType;
    private int assistance;
    private int deelays;
    private int lacks;
    private int justifications;

    public ReportRecord() {
        
    }
    
    public ReportRecord(Employee employee, EmployeeType employeeType, int assistance, int deelays, int lacks, int justifications) {
        this.employee = employee;
        this.employeeType = employeeType;
        this.assistance = assistance;
        this.deelays = deelays;
        this.lacks = lacks;
        this.justifications = justifications;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public int getAssistance() {
        return assistance;
    }

    public void setAssistance(int assistance) {
        this.assistance = assistance;
    }

    public int getDeelays() {
        return deelays;
    }

    public void setDeelays(int deelays) {
        this.deelays = deelays;
    }

    public int getLacks() {
        return lacks;
    }

    public void setLacks(int lacks) {
        this.lacks = lacks;
    }

    public int getJustifications() {
        return justifications;
    }

    public void setJustifications(int justifications) {
        this.justifications = justifications;
    }
    
}
