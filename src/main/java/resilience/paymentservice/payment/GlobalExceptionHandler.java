package resilience.paymentservice.payment;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import resilience.paymentservice.exception.PaymentGatewayServerErrorException;
import resilience.paymentservice.exception.PaymentGatewayTimeoutException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PaymentGatewayTimeoutException.class)
    public ResponseEntity<String> handleTimeoutException(PaymentGatewayTimeoutException ex) {
        logger.error("Timeout Exception while calling PG service", ex);
        return new ResponseEntity<>("PG 요청 타임아웃 발생", HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(PaymentGatewayServerErrorException.class)
    public ResponseEntity<String> handleServerErrorException(PaymentGatewayServerErrorException ex) {
        logger.error("Server Error Exception while calling PG service", ex);
        return new ResponseEntity<>("PG 서버 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Unhandled exception occurred", ex);
        return new ResponseEntity<>("PG 서비스 처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
