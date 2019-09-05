/**
 * 
 */
package dev.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.model.Utilisateur;

/**
 * @author Guillaume
 *
 */
public interface IUtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

	public Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

}
