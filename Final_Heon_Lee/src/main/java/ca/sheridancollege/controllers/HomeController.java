/*
 * 
 * 
 * Heon Lee
 * 991280638
 * 
 * Final Exam
 * 2020-04-09
 * 
 */
package ca.sheridancollege.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.beans.Appointment;
import ca.sheridancollege.beans.Trainer;
import ca.sheridancollege.beans.User;
import ca.sheridancollege.email.EmailServiceImpl;
import ca.sheridancollege.repositories.AppointmentRepository;
import ca.sheridancollege.repositories.RoleRepository;
import ca.sheridancollege.repositories.TrainerRepository;
import ca.sheridancollege.repositories.UserRepository;

@Controller
public class HomeController {
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private TrainerRepository trainerRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailServiceImpl esi;
	
	@GetMapping("/")
	public String goHome() {
		
		return "Home.html";
	}
	
	@GetMapping("/addTrainer")
	public String addTrainer(Model model) {
		
		model.addAttribute("trainer", new Trainer());
		
		return "addTrainer.html";
	}
	
	@GetMapping("/saveTrainer")
	public String saveTrainer(Model model, @ModelAttribute Trainer trainer) {
		
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Trainer>> validationErrors = validator.validate(trainer);

		List<String> messages = new ArrayList<String>();

		if (!validationErrors.isEmpty()) {
			
			for (ConstraintViolation<Trainer> e : validationErrors) {
				messages.add(e.getPropertyPath() + " : " + e.getMessage());
			}
			
		} else {
			if(trainerRepository.findByName(trainer.getName()) != null) {
				messages.add("The trainer name already exists");
			} else {
				trainerRepository.save(trainer);
				messages.add("Trainer successfully added");
			}
		}

		model.addAttribute("messages", messages);
		
		model.addAttribute("trainer", new Trainer());
		
		return "addTrainer.html";
	}
	
	@GetMapping("/addAppointment")
	public String addAppointment(Model model) {
		
		model.addAttribute("trainers", trainerRepository.findAll());
		
		model.addAttribute("appointment", new Appointment());
		
		
		return "addAppointment.html";
	}
	
	@GetMapping("/saveAppointment")
	public String saveAppointment(Model model, @ModelAttribute Appointment appointment, @RequestParam String email) {
		
		boolean booked = false;
		
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Appointment>> validationErrors = validator.validate(appointment);
		
		for(Appointment a : appointmentRepository.findByMonth(appointment.getMonth())) {
			if(a.getDay() == appointment.getDay() && a.getStart() == appointment.getStart()
					&& a.getTrainer().equals(appointment.getTrainer())) {
				booked = true;
			}
		}

		List<String> messages = new ArrayList<String>();
		
		if (!validationErrors.isEmpty()) {
			
			for (ConstraintViolation<Appointment> e : validationErrors) {
				messages.add(e.getPropertyPath() + " : " + e.getMessage());
			}
			
		} else {
			if (!booked) {
				
				appointmentRepository.save(appointment);
				
				messages.add("Appointment successfully added");
				
				if(email != null && !email.trim().equals("")) {
					//Send email
					String msg = "Your appointment is booked!\n"
							+ "\nClient name: " + appointment.getClient()
							+ "\nMonth: " + appointment.getMonth()
							+ "\nDay: " + appointment.getDay()
							+ "\nStart time: " + appointment.getStart() + ":00"
							+ "\nMessage: " + appointment.getMessage()
							+ "\nTrainer: " + appointment.getTrainer();
					try {
						esi.sendMailWithInline(email, "Appointment booked", "Final_Heon_Lee", msg, "Heon Lee 991280638");
						messages.add("Email sent");
					} catch(MessagingException e) {
						System.out.println(e);
					}
				}
				
			} else {
				
				messages.add("That timeslot is already booked. Please choose another timeslot.");
				
			}
		}

		model.addAttribute("messages", messages);
		
		model.addAttribute("appointment", new Appointment());
		
		model.addAttribute("trainers", trainerRepository.findAll());
		
		return "addAppointment.html";
		
	}
	
	@GetMapping("/checkTrainer")
	public String checkTrainer(Model model) {
		
		model.addAttribute("trainers", trainerRepository.findAll());
		
		List<Integer> months = new ArrayList<>();
		List<Integer> days = new ArrayList<>();
		
		for(int i = 1; i <= 12; i++) {
			months.add(i);
		}
		
		for(int i = 1; i <= 30; i++) {
			days.add(i);
		}
		
		model.addAttribute("months", months);
		model.addAttribute("days", days);
		
		
		
		return "checkTrainer.html";
	}
	
	//Need to fix
	//ArrayList part
	@GetMapping("/checkTrainerSchedule")
	public String checkTrainerSchedule(Model model, @RequestParam int month, @RequestParam int day,
			@RequestParam int trainerid) {
		
		Trainer trainer = trainerRepository.findById(trainerid).get();
		
		//8 - 20
		List<Appointment> appointments = new ArrayList<>();
		List<Appointment> schedule = new ArrayList<>();
		
		for(Appointment a : appointmentRepository.findByTrainer(trainer.getName())) {
			if(a.getMonth() == month && a.getDay() == day) {
					appointments.add(a);
			}
		}
		
		int size = schedule.size();
		
		for(int i = 0; i < 12; i++) {
			for(Appointment a : appointments) {
				if(a.getStart() == (i + 8)) {
					schedule.add(a);
					size = schedule.size();
				}
			}
			
			if(schedule.size() == size) {
				schedule.add(Appointment.builder().start(i+8).client("").message("").trainer("").build());
				size = schedule.size();
			}
		}
		
		model.addAttribute("appointments", schedule);
		
		model.addAttribute("trainerName", trainer.getName());
		
		model.addAttribute("month", month);
		model.addAttribute("day", day);
		
		
		
		model.addAttribute("trainers", trainerRepository.findAll());

		List<Integer> months = new ArrayList<>();
		List<Integer> days = new ArrayList<>();

		for (int i = 1; i <= 12; i++) {
			months.add(i);
		}

		for (int i = 1; i <= 30; i++) {
			days.add(i);
		}

		model.addAttribute("months", months);
		model.addAttribute("days", days);
		
		
		
		return "checkTrainer.html";
	}
	
	@GetMapping("/searchTrainer")
	public String searchTrainer(Model model) {
		
		return "searchTrainer.html";
	}
	
	@GetMapping("/searchAvailableTrainer")
	public String searchAvailableTrainer(Model model, @RequestParam String month, @RequestParam String day,
			@RequestParam String time) {
		
		int monthInt = Integer.parseInt(month);
		int dayInt = Integer.parseInt(day);
		int timeInt = Integer.parseInt(time);
	
		List<Trainer> trainers = new ArrayList<>();
		
		List<String> unavailable = new ArrayList<>();
		
		for(Appointment a : appointmentRepository.findAll()) {
			if(a.getStart() == timeInt && a.getDay() == dayInt && a.getMonth() == monthInt) {
				unavailable.add(a.getTrainer());
			}
		}
		
		for(Trainer t : trainerRepository.findAll()) {
			
			if(!unavailable.contains(t.getName())) {
				trainers.add(t);
			}
		}
		
		model.addAttribute("trainers", trainers);
		
		
		
		return "searchTrainer.html";
		
	}
	
	@GetMapping("/register")
	public String goRegistration(Model model) {
		
		model.addAttribute("roles", roleRepository.findAll());
		
		return "registerUser.html";
	}
	
	@PostMapping("/register")
	public String registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
		
		User user = new User(username, encodePassword(password));
		
		if (role.equals("ROLE_TRAINER")) {
			// Trainer
			user.getRoles().add(roleRepository.findByRolename("ROLE_TRAINER"));
		} else {
			// Client
			user.getRoles().add(roleRepository.findByRolename("ROLE_CLIENT"));
		}

		userRepository.save(user);
		
		return "redirect:/";
	}
	
	@GetMapping("/access-denied")
	public String goaccessdenied() {
		
		return "access-denied.html";
		
	}
	
	@GetMapping("/logout")
	public String logout() {
		return "redirect:/logout";
	}
	
	private String encodePassword(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}
}
