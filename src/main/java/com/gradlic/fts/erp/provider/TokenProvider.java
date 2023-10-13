package com.gradlic.fts.erp.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gradlic.fts.erp.domain.UserPrincipal;
import com.gradlic.fts.erp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String GRADLIC_SOLUTIONS = "Gradlic Solutions Private Limited";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    public static final String AUTHORITIES = "authorities";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 3_600_000;//30_000; //3_600_000; // 1_800_000
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;//30_000;//432_000_000;
    public static final String TOKEN_CAN_NOT_BE_VERIFIED = "Token can not be verified";
    @Value("${jwt.secret}")
    private String secret;

    private final UserService userService;

    public String createAccessToken(UserPrincipal principal){
        String[] claims = getClaimsFromUser(principal);
        return JWT.create().withIssuer(GRADLIC_SOLUTIONS).withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date()).withSubject(String.valueOf(principal.getUser().getId())).withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserPrincipal principal){
        String[] claims = getClaimsFromUser(principal);
        return JWT.create().withIssuer(GRADLIC_SOLUTIONS).withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date()).withSubject(String.valueOf(principal.getUser().getId()))
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public Long getSubject(String token, HttpServletRequest request){
        try{
            return Long.valueOf(getJWTVerifier().verify(token).getSubject());
        }catch (TokenExpiredException exception){
            request.setAttribute("expiredMessage", exception.getMessage());
            throw exception;
        }catch (InvalidClaimException exception){
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        }catch (Exception exception){
            throw exception;
        }
    }

    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(Long userId, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userService.getUserByUserId(userId), null, authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

    public boolean isTokenValid(Long userId, String token){
        JWTVerifier verifier = getJWTVerifier();
        return !Objects.isNull(userId) && !isTokenExpired(verifier, token);
    }

    private boolean isTokenExpired(JWTVerifier jwtVerifier, String token){
        Date expiration = jwtVerifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try{
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(GRADLIC_SOLUTIONS).build();
        }catch(JWTVerificationException exception){
            throw new JWTVerificationException(TOKEN_CAN_NOT_BE_VERIFIED);
        }
        return verifier;
    }

    private String[] getClaimsFromUser(UserPrincipal principal) {
        return principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }
}
