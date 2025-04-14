package resilience.paymentservice.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resilience.paymentservice.payment.model.PGRequest;
import resilience.paymentservice.payment.model.PGResponse;
import resilience.paymentservice.payment.model.PaymentRequest;
import resilience.paymentservice.payment.model.PaymentResponse;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mock-pg")
    public ResponseEntity<PGResponse> mockPG(@RequestBody PGRequest request) throws InterruptedException {
        double chance = ThreadLocalRandom.current().nextDouble();

        if (chance < 1.0) {
            // 100% 확률: 성공 응답
            Thread.sleep(2000);
            PGResponse response = new PGResponse("SUCCESS", "Payment processed successfully");
            return ResponseEntity.ok(response);
        } else {
            // 0% 확률: 60초 지연 (SomePGClient의 타임아웃 발생 시뮬레이션)
            Thread.sleep(60000);
            PGResponse response = new PGResponse("SUCCESS", "Payment processed successfully after delay");
            return ResponseEntity.ok(response);
        }
    }
}