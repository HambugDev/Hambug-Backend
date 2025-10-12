package com.hambug.Hambug.global.logging;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    // Target only our REST controllers in this project to avoid noise from actuator/swagger
    @Around("within(@org.springframework.web.bind.annotation.RestController *) && execution(* com.hambug.Hambug..controller..*(..))")
    public Object logAroundController(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        ServletRequestAttributes attrs = currentRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        HttpServletResponse response = attrs != null ? attrs.getResponse() : null;

        String httpMethod = request != null ? request.getMethod() : "N/A";
        String uri = request != null ? request.getRequestURI() : handlerMethod(pjp);
        String query = request != null ? Optional.ofNullable(request.getQueryString()).orElse("") : "";
        String clientIp = request != null ? resolveClientIp(request) : "N/A";
        String referer = request != null ? headerOrEmpty(request, "Referer") : "";
        String userAgent = request != null ? headerOrEmpty(request, "User-Agent") : "";
        String userInfo = resolveUserInfo();

        String handler = handlerMethod(pjp);

        log.info("[REQ] {} {}{} ip={} user={} handler={} referer={} ua={}",
                httpMethod,
                uri,
                StringUtils.hasText(query) ? ("?" + query) : "",
                clientIp,
                userInfo,
                handler,
                safe(referer),
                safe(userAgent)
        );

        try {
            Object result = pjp.proceed();
            int status = response != null ? response.getStatus() : 200;
            long took = System.currentTimeMillis() - start;
            log.info("[RES] {} {} status={} took={}ms", httpMethod, uri, status, took);
            return result;
        } catch (Throwable ex) {
            int status = response != null ? response.getStatus() : 500;
            long took = System.currentTimeMillis() - start;
            log.error("[ERR] {} {} status={} took={}ms handler={} ex={}: {}", httpMethod, uri, status, took, handler, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    private String handlerMethod(ProceedingJoinPoint pjp) {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        return sig.getDeclaringType().getSimpleName() + "." + sig.getMethod().getName();

    }

    private ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra;
        }
        return null;
    }

    private String headerOrEmpty(HttpServletRequest request, String name) {
        String v = request.getHeader(name);
        return v == null ? "" : v;
    }

    private String safe(String s) {
        if (s == null) return "";
        // trim to avoid extremely long UA flooding logs
        return s.length() > 512 ? s.substring(0, 512) + "..." : s;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            // In case of multiple, first is original client
            String first = xff.split(",")[0].trim();
            if (StringUtils.hasText(first)) return first;
        }
        String xri = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xri)) return xri;
        return request.getRemoteAddr();
    }

    private String resolveUserInfo() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return "anonymous";
            Object principal = auth.getPrincipal();
            if (principal instanceof PrincipalDetails pd) {
                try {
                    Long userId = pd.getUserDto() != null ? pd.getUserDto().getUserId() : null;
                    String nickname = pd.getUserDto() != null ? pd.getUserDto().getNickname() : null;
                    return "userId=" + (userId != null ? userId : "?") + ", nick=" + (nickname != null ? nickname : "?");
                } catch (Exception ignored) {
                }
            }
            return auth.getName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
