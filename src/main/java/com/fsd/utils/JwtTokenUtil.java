package com.fsd.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
public class JwtTokenUtil implements Serializable {

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final long EXPIRATION = 86400000L; // 1 day (millisecond)
  public static final long EXPIRATION_REMEMBER = 604800000L; // 7 days
  private static final long serialVersionUID = 3795255684130470783L;
  private static final String SECRET = "FSDJwtSecurit";
  private static final String ISSUSER = "FSD Jon";
  private static final String ROLE_CLAIMS = "FSDRole";

  public static String generateToken(UserDetails details, boolean isRememberMe) {
    // if click remember me，the token expiration time will be EXPIRATION_REMEMBER
    long expiration = isRememberMe ? EXPIRATION_REMEMBER:EXPIRATION;

    HashMap<String, Object> map = new HashMap<>();
    map.put(ROLE_CLAIMS, details.getAuthorities()); // roles

    return Jwts.builder().signWith(SignatureAlgorithm.HS512, SECRET) // Algorithm
               .setClaims(map) // customer info
               .setIssuer(ISSUSER) // jwt issuser
               .setSubject(details.getUsername()) // jwt user
               .setIssuedAt(new Date()) // jwt issuser date
               .setExpiration(new Date(System.currentTimeMillis() + expiration)) // expiration time for key
               .compact();
  }

  public static String generateToken(Authentication authentication, boolean isRememberMe) {
    long expiration = isRememberMe ? EXPIRATION_REMEMBER:EXPIRATION;

    HashMap<String, Object> map = new HashMap<>();
    map.put(ROLE_CLAIMS, authentication.getAuthorities());

    return Jwts.builder().signWith(SignatureAlgorithm.HS512, SECRET) // Algorithm
               .setClaims(map) // customer info
               .setIssuer(ISSUSER) // jwt issuser
               .setSubject(authentication.getName()) // jwt user
               .setIssuedAt(new Date()) // jwt issuser date
               .setExpiration(new Date(System.currentTimeMillis() + expiration)) // expiration time for key
               .compact();
  }

  public static String getUsername(String token) {
    return getTokenBody(token).getSubject();
  }

  public static Set<String> getUserRole(String token) {
    List<GrantedAuthority> userAuthorities = (List<GrantedAuthority>) getTokenBody(token).get(ROLE_CLAIMS);
    return AuthorityUtils.authorityListToSet(userAuthorities);
  }

  public static boolean isExpiration(String token) {
    return getTokenBody(token).getExpiration().before(new Date());
  }

  private static Claims getTokenBody(String token) { // parseClaimsJws is also verifying the token and will throw exception if token invalid
    return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
  }

  public static boolean validateToken(String token, UserDetails userDetails) {
    User user = (User) userDetails;
    final String tokenUsername = getUsername(token);
    return (tokenUsername.equals(user.getUsername()) && isExpiration(token) == false);
  }

}
