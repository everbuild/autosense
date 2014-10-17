package be.everbuild.autosense.server.rest.model;

public class Test {
    private String message;

    public Test() {
    }

    public Test(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
