package ca.sheridancollege.repositories;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.beans.Student;

public interface StudentRepository extends CrudRepository<Student, Long> {

	Student findBySname(String sname);
	
	Student findBySid(String sid);
	
}
