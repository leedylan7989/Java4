package ca.sheridancollege.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.beans.Appointment;

public interface AppointmentRepository extends CrudRepository<Appointment, Integer> {

	List<Appointment> findByTrainer(String trainer);
	List<Appointment> findByMonth(int month);
	List<Appointment> findAll();
	List<Appointment> findByMonthAndDay(int month, int day);
}
