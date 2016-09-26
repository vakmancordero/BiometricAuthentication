package biometricauthentication.utils;

/**
 *
 * @author VakSF
 */
public class Information {
    
    private String operation;
    private String verification;

    public Information() {
    }
    
    public Information(String operation, String verification) {
        this.operation = operation;
        this.verification = verification;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    @Override
    public String toString() {
        return this.operation + " : " + this.verification;
    }
    
}
