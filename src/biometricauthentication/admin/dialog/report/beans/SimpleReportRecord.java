package biometricauthentication.admin.dialog.report.beans;

/**
 *
 * @author VakSF
 */
public class SimpleReportRecord {
    
    private String employee;
    private int assistance;
    private int deelays;
    private int lacks;
    private int justifications;

    public SimpleReportRecord() {
    }

    public SimpleReportRecord(ReportRecord reportRecord) {
        this.employee = reportRecord.getEmployee().toString();
        this.assistance = reportRecord.getAssistance();
        this.deelays = reportRecord.getDeelays();
        this.lacks = reportRecord.getLacks();
        this.justifications = reportRecord.getJustifications();
    }
    
    public SimpleReportRecord(String employee, int assistance, int deelays, int lacks, int justifications) {
        this.employee = employee;
        this.assistance = assistance;
        this.deelays = deelays;
        this.lacks = lacks;
        this.justifications = justifications;
    }


    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public int getAssistance() {
        return assistance;
    }

    public void setAssistance(int assistance) {
        this.assistance = assistance;
    }
    
    public void addAssistance() {
        this.assistance++;
    }

    public int getDeelays() {
        return deelays;
    }

    public void setDeelays(int deelays) {
        this.deelays = deelays;
    }
    
    public void addDeelay() {
        this.deelays++;
    }

    public int getLacks() {
        return lacks;
    }

    public void setLacks(int lacks) {
        this.lacks = lacks;
    }
    
    public void addLack() {
        this.lacks++;
    }

    public int getJustifications() {
        return justifications;
    }

    public void setJustifications(int justifications) {
        this.justifications = justifications;
    }
    
    public void addJustification() {
        this.justifications++;
    }
    
}
