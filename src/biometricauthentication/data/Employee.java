package biometricauthentication.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author VakSF
 */

@Entity
@Table(name = "employees")
public class Employee implements Serializable {
    
    @Id
    private int id;
    
    @Column
    private String name;
    
    @Column (name = "fingerprint")
    private byte[] template;
    
    @Column (name = "photo")
    private byte[] photo;

    public Employee() {
    }

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }
    
    public byte[] getPhoto() {
        return this.photo;
    }
    
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "\n\tNombre: " + this.name;
    }
    
}
