package resilience.paymentservice.payment;

import org.springframework.stereotype.Service;
import resilience.paymentservice.payment.model.PGRequest;
import resilience.paymentservice.payment.model.PGResponse;
import resilience.paymentservice.payment.model.PaymentRequest;
import resilience.paymentservice.payment.model.PaymentResponse;

@Service
public class PaymentService {

    private final SomePGClient pgClient;

    public PaymentService(SomePGClient pgClient) {
        this.pgClient = pgClient;
    }

    // 이번에는 pgClient.callPGService 내부에서 재시도하도록 처리. 왜 이렇게 하는지에 대해선 다음 영상에서 다룸.
    public PaymentResponse processPayment(PaymentRequest request) {
        // PaymentRequest를 PGRequest로 매핑 (정적 팩토리 메서드 사용)
        PGRequest pgRequest = PGRequest.of(request.getOrderId(), request.getAmount());

        // PG 호출 (타임아웃은 PGClient 내부에서 5초로 설정)
        PGResponse pgResponse = pgClient.callPGService(pgRequest);

        // PGResponse를 PaymentResponse로 매핑 (정적 팩토리 메서드 사용)
        return PaymentResponse.of(pgResponse.getStatus(), pgResponse.getMessage());
    }
}
