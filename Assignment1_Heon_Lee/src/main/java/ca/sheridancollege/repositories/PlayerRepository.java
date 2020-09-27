package ca.sheridancollege.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.beans.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

	Player findById(int id);
	Player findByName(String name);
	Player findByEmail(String email);
	List<Player> findByNameContaining(String name);
	List<Player> findByAge(int age);
	List<Player> findByGender(String gender);
	List<Player> findByPhoneContaining(String phone);
	List<Player> findByEmailContaining(String email);
	List<Player> findByTeamContaining(String team);
}
