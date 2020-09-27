package ca.sheridancollege.repositories;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.beans.Trainer;

public interface TrainerRepository extends CrudRepository<Trainer, Integer> {

	Trainer findByName(String name);
}
