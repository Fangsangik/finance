package zerobase.finiance.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //controller 요청 들어올 때 -> filter -> servlet -> interceptor -> aop layer -> controller
    //나갈때는 반대
    //OncePerRequestFilter -> 한 요청당 한번 나간다

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer"; // 인증 타입 (Jwt의 경우 Bearer를 붙인다)
    private final TokenProvider cacheTokenProvider; //유효성 검증


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveTokenFromRequest(request);

        if (StringUtils.hasText(token) && cacheTokenProvider.validateToken(token)) {
            //토큰 유효성 검증
            Authentication auth = cacheTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            //어떤 사용자가 어떤 로그에 접근 했는지
            log.info(String.format("%s -> " + cacheTokenProvider.getUsername(token), request.getRequestURI()));
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER);

        if (ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)){
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
