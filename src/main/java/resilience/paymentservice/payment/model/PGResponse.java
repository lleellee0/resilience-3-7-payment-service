package resilience.paymentservice.payment.model;

public class PGResponse {

    private final String status;
    private final String message;

    public PGResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
