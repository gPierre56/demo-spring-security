/**
 * 
 */
package dev.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.model.InfosAuthentification;
import dev.repository.IUtilisateurRepository;
import io.jsonwebtoken.Jwts;

/**
 * @author Guillaume
 *
 */
@RestController
public class AuthentificationCtrl {

	@Value("${jwt.expires_in}")
	private Integer EXPIRES_IN;

	@Value("${jwt.cookie}")
	private String TOKEN_COOKIE;

	@Value("${jwt.secret}")
	private String SECRET;

	private IUtilisateurRepository repository;

	private PasswordEncoder passwordEncoder;

	public AuthentificationCtrl(IUtilisateurRepository repository, PasswordEncoder passwordEncoder) {

		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping(value = "/auth")
	public ResponseEntity<?> authenticate(@RequestBody InfosAuthentification infos) {
		return this.repository.findByNomUtilisateur(infos.getNomUtilisateur())
				.filter(u -> passwordEncoder.matches(infos.getMotDePasse(), u.getMotDePasse())).map(u -> {
					Map<String, Object> infosSupplementairesToken = new HashMap<>();
					infosSupplementairesToken.put("roles", u.getRoles());

					String jetonJWT = Jwts.builder().setSubject(u.getNomUtilisateur())
							.addClaims(infosSupplementairesToken)
							.setExpiration(new Date(System.currentTimeMillis() + EXPIRES_IN * 1000))
							.signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, SECRET).compact();

					ResponseCookie tokenCookie = ResponseCookie.from(TOKEN_COOKIE, jetonJWT).httpOnly(true)
							.maxAge(EXPIRES_IN * 1000).path("/").build();

					return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookie.toString()).build();
				}).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

}
