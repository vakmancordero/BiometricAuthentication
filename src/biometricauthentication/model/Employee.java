package biometricauthentication.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author VakSF
 */

@Entity
@Table(name = "employee")
public class Employee implements Serializable {
    
    @Id
    @GeneratedValue
    @Column (name = "id")
    private int id;
    
    @OneToOne
    private Shift shift;
    
    @OneToOne
    private EmployeeType employeetype;
    
    @OneToOne
    private Company company;
    
    @Column
    private String name;
    
    @Column (name = "last_name")
    private String lastName; 
    
    @Column (name = "mothers_last_name")
    private String mothersLastName;
    
    @Column
    private String curp;
    
    @Column
    private String gender;
    
    @Column
    private String town;
    
    @Column
    private String cellphone;
    
    @Column (name = "fingerprint")
    private byte[] template;
    
    private int hash;
    
    public Employee() {
        
    }
    
    public Employee(String name) {
        this.name = name;
    }

    public Employee(String name, String lastName, String mothersLastName, Shift shift, Company company) {
        this.name = name;
        this.lastName = lastName;
        this.mothersLastName = mothersLastName;
        this.shift = shift;
        this.company = company;
    }
    
    public void updateEmployee(String name, String lastName, String mothersLastName, Shift shift, Company company) {
        this.name = name;
        this.lastName = lastName;
        this.mothersLastName = mothersLastName;
        this.shift = shift;
        this.company = company;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Shift getShift() {
        return shift;
    }
    
    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public EmployeeType getEmployeetype() {
        return employeetype;
    }

    public void setEmployeetype(EmployeeType employeetype) {
        this.employeetype = employeetype;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMothersLastName() {
        return mothersLastName;
    }

    public void setMothersLastName(String mothersLastName) {
        this.mothersLastName = mothersLastName;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
    
    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }
    
    @Override
    public int hashCode() {
        
        int h = hash;
        
        char[] value = this.toString().toCharArray();
        
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }
    
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Employee) {
            Employee anotherClient = (Employee) anObject;
            
            if (anotherClient.toString().equals(this.toString())) {
                return true;
            }
            
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name + " " + this.lastName + " " + this.mothersLastName;
    }
    
}
