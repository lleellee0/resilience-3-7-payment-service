package resilience.paymentservice.payment;

import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import resilience.paymentservice.exception.PaymentGatewayTimeoutException;
import resilience.paymentservice.exception.RetryableException;
import resilience.paymentservice.payment.model.PGRequest;
import resilience.paymentservice.payment.model.PGResponse;

import java.time.Duration;

@Component
public class SomePGClient {

    private final WebClient webClient;

    public SomePGClient() {
        // Reactor Netty HttpClient에 5초 응답 타임아웃 설정
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://localhost:8082/payments") // PG 서비스는 포트 8082에서 동작
                .build();
    }

    @Retryable(
            retryFor = { RetryableException.class }, // 재시도할 예외 유형
            maxAttempts = 3,                         // 최대 재시도 횟수
            backoff = @Backoff(delay = 300)          // 재시도 간 대기 시간 (밀리초 단위)
    )
    public PGResponse callPGService(PGRequest request) {
        return webClient.post()
                .uri("/mock-pg")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                // 아래처럼 Status Code에 맞춰 핸들링도 가능
                // HTTP 500 에러 응답을 PaymentGatewayServerErrorException으로 매핑
//                .onStatus(status -> status.is5xxServerError(), clientResponse ->
//                        clientResponse.bodyToMono(String.class)
//                                .flatMap(errorBody ->
//                                        Mono.<Throwable>error(new PaymentGatewayServerErrorException("500 에러 발생: " + errorBody))
//                                )
//                )
                .bodyToMono(PGResponse.class)
                // 발생한 예외 또는 그 원인이 ReadTimeoutException인 경우 PaymentGatewayTimeoutException으로 매핑
                .onErrorMap(throwable -> {
                    if (throwable instanceof ReadTimeoutException ||
                            (throwable.getCause() != null && throwable.getCause() instanceof ReadTimeoutException)) {
                        return new PaymentGatewayTimeoutException("PG 서비스 호출 시 타임아웃 발생", throwable);
                    }
                    return throwable;
                })
                .block(); // 동기 방식 호출
    }

}
