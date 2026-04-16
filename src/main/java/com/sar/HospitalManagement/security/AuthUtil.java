package com.sar.HospitalManagement.security;

import com.sar.HospitalManagement.entity.User;
import com.sar.HospitalManagement.entity.type.AuthProviderType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecurity;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecurity.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getUsername())    /* Adding subject to jwt*/
                .claim("userId",user.getUsername().toString())  /*Adding claims to jwt*/ /*claims*/ /*Mostly add role*/
                .issuedAt(new Date())   /*Created at*/
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10)) /*Expire at*/
                .signWith(getSecretKey())   /*Assign secret key*/
                .compact(); /*Convert into string jwt format like  subject.payload.secretKey  */
    }

    public String getUsernameFromToken(String token) {
//        try{
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
//        }catch (ExpiredJwtException e) {
//            throw new RuntimeException("Token expired");
//        } catch (Exception e) {
//            throw new RuntimeException("Invalid token");
//        }
    }

    public AuthProviderType getProviderTypeFromRegistration(String registrationId){
        if (registrationId == null) {
            throw new IllegalArgumentException("registrationId cannot be null");
        }
        return switch (registrationId.toLowerCase()){
            case "google" -> AuthProviderType.GOOGLE;
            case "github" -> AuthProviderType.GITHUB;
            default ->  throw new IllegalArgumentException(("Unsupported OAuth2 provider: "+registrationId));
        };
    }

    public String determineProviderIdFromOAuth2User(OAuth2User oAuth2User, String registrationId){
        /*
        * Google porvides "sub"(subject) which is globally unique in JSON format (String type).
        * Github provides "id" which an Integer type mostly.
        * */
        if (registrationId == null) {
            throw new IllegalArgumentException("registrationId cannot be null");
        }
        String providerId = switch(registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> {
               Object github = oAuth2User.getAttribute("id");
               yield github != null ? github.toString() : null;
            }
            default -> {
                log.error("Unsupported OAuth2 Provider: {}",registrationId);
                throw new IllegalArgumentException("Unspported OAuth2 provider: "+registrationId);
            }
        };

        if(providerId == null || providerId.isBlank()){
            log.error("Unable to determine providerId for provider: {}",registrationId);
            throw new IllegalArgumentException("Unable to determine providerId for OAuth2 login");
        }
        return providerId;
    }

    public String determineUsernameFromOAuth2User(OAuth2User oAuth2User, String registrationId, String providerId){
        String email = oAuth2User.getAttribute("email");
        if(email != null && !email.isBlank()){
            return email;
        }
        return switch(registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");    /*Unique across google*/
            case "github" -> oAuth2User.getAttribute("login");  /* login Unique across guthub*/
            default -> providerId;
        };
    }
}
