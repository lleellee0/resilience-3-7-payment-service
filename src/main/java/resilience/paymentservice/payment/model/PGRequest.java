package resilience.paymentservice.payment.model;

public class PGRequest {

    private final String orderId;
    private final Double amount;

    private PGRequest(String orderId, Double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public static PGRequest of(String orderId, Double amount) {
        return new PGRequest(orderId, amount);
    }

    public String getOrderId() {
        return orderId;
    }

    public Double getAmount() {
        return amount;
    }
}
