package mc.monacotelecom.services.logs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class IncomingRequestsLogger {
    private final HttpServletRequest httpRequest;

    @Before("execution(* mc.monacotelecom.services.controller..*(..))")
    public void logBeforeEachRequest(JoinPoint joinPoint) {
        log.info("{} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.error("Method call: {}.{}() with argument[s] = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.deepToString(joinPoint.getArgs())
        );
    }
}