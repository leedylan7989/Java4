package ca.sheridancollege.repositories;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.beans.Professor;

public interface ProfessorRepository extends CrudRepository<Professor, Long> {

}
