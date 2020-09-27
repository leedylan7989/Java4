/*
 * 
 * Add appointment
 * POST - Body - raw
 *     {
        "id": 2,
        "client": "Client2",
        "month": 4,
        "day": 9,
        "start": 11,
        "message": "Upper Body",
        "trainer": "Trainer1"
    }
 * 
 * localhost:8080/appointments
 * 
 * PUT - Body - raw
 * ID should not exist in the database. ID must be a new ID for a new record.
 * [
    {
        "id": 3,
        "client": "Client3",
        "month": 4,
        "day": 9,
        "start": 12,
        "message": "Core",
        "trainer": "Trainer1"
    }
]
 * localhost:8080/appointments/3 
 * 
 * Get appointments matching a day and month given - GET
 * localhost:8080/appointments/4/9
 * 
 * Get appointments matching a trainer's name - GET
 * localhost:8080/appointments/Trainer1
 */

package ca.sheridancollege.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.sheridancollege.beans.Appointment;
import ca.sheridancollege.repositories.AppointmentRepository;

@RestController
public class AppointmentController {
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	//Get
	@GetMapping("/appointments")
	public List<Appointment> getAppointments(){
		return appointmentRepository.findAll();
	}
	
	@GetMapping("/appointments/{trainer}")
	public List<Appointment> getAppointmentByTrainer(@PathVariable String trainer) {
		return appointmentRepository.findByTrainer(trainer);
	}
	
	@GetMapping("/appointments/{month}/{day}")
	public List<Appointment> getAppointmentByDate(@PathVariable int day, @PathVariable int month){
		return appointmentRepository.findByMonthAndDay(month, day);
	}
	
	
	// Post
	@PostMapping(value = "/appointments", headers = { "Content-type=application/json" })
	public Integer postAppointment(@RequestBody Appointment appointment) {
		Appointment b = appointmentRepository.save(appointment);
		
		return b.getId();
	}

	// Put

	@PutMapping(value = "/appointments", headers = { "Content-type=application/json" })
	public String putBooks(@RequestBody List<Appointment> appointments) {
		appointmentRepository.saveAll(appointments);

		return "Appointments added:" + appointmentRepository.count() + " appointments";
	}

	@PutMapping(value = "/appointments/{id}", headers = { "Content-type=application/json" })
	public String putBook(@PathVariable int id, @RequestBody List<Appointment> appointments) {

		Appointment a = appointments.get(0);
		appointmentRepository.save(a);

		return "Appointment updated - ID: " + a.getId();
	}

}
