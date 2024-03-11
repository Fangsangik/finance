package zerobase.finiance.security;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zerobase.finiance.service.MemberServiceImpl;

import java.util.Date;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간을 의미
    private final MemberServiceImpl memberService;


    @Value("{spring.jwt.secret}")
    private String secretKey;


    //토큰을 생성하는 메소드
    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        Jwts.builder().setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expireDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .compact();
        return null;
    }

    public String getUsername(String token){
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token){
        if (!StringUtils.hasText(token))
            return false;

        var claims = parseClaims(token);
        return claims.getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public Authentication getAuthentication(String jwt){
        UserDetails userDetails = memberService.loadUserByUsername(getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
