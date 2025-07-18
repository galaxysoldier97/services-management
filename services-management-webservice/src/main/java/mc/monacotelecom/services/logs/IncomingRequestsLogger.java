package mc.monacotelecom.services.logs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class IncomingRequestsLogger {
    private final HttpServletRequest httpRequest;

    @Around("execution(* mc.monacotelecom.services.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String processName = httpRequest.getHeader("processName");
        if (processName == null || processName.isBlank()) {
            processName = "N/A";
        }
        String uti = httpRequest.getHeader("uti");
        if (uti == null || uti.isBlank()) {
            uti = "N/A";
        }
        MDC.put("processName", processName);
        MDC.put("uti", uti);
        String params = Arrays.deepToString(joinPoint.getArgs());
        log.info("{} {} - start - {}", httpRequest.getMethod(), httpRequest.getRequestURI(), params);
        try {
            Object result = joinPoint.proceed();
            log.info("{} {} - end", httpRequest.getMethod(), httpRequest.getRequestURI());
            return result;
        } catch (Throwable ex) {
            log.error("{} {} - error", httpRequest.getMethod(), httpRequest.getRequestURI(), ex);
            throw ex;
        } finally {
            MDC.clear();
        }
    }
}