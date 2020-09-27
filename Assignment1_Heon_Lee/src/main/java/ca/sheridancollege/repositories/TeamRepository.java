package ca.sheridancollege.repositories;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.beans.Team;

public interface TeamRepository extends CrudRepository<Team, Integer> {
	
	Team findById(int id);
	Team findByName(String name);
}
