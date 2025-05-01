package com.turismea.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

	private static final Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	private static final long JWT_EXPIRATION_IN_MS = 5400000;
	private static final long REFRESH_TOKEN_EXPIRATION_MSEC = 10800000;

	@Autowired
	private UserDetailsService userDetailsService;

	// Helper para convertir el secret en una clave segura
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			LOG.debug("Invalid JWT: {}", ex.getMessage());
		}
		return false;
	}

	public Token generateToken(UserDetails user) {

		Claims claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("auth", user.getAuthorities().stream()
				.map(a -> a.getAuthority())
				.collect(Collectors.toList()));

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_IN_MS);

		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(getSigningKey(), SignatureAlgorithm.HS512)
				.compact();

		return new Token(Token.TokenType.ACCESS, token, expiryDate.getTime(),
				LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
	}

	public Token generateRefreshToken(UserDetails user) {

		Claims claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("auth", user.getAuthorities().stream()
				.map(a -> a.getAuthority())
				.collect(Collectors.toList()));

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MSEC);

		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(getSigningKey(), SignatureAlgorithm.HS512)
				.compact();

		return new Token(Token.TokenType.REFRESH, token, expiryDate.getTime(),
				LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
	}
}
