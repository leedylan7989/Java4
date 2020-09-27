/*
 * 
 * Heon Lee
 * 991280638
 * 
 * Assignment 2
 * 2020-04-06
 */


package ca.sheridancollege.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import ca.sheridancollege.beans.Student;
import ca.sheridancollege.beans.User;
import ca.sheridancollege.repositories.RoleRepository;
import ca.sheridancollege.repositories.StudentRepository;
import ca.sheridancollege.repositories.UserRepository;

@Controller
public class HomeController {
	
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String goHome() {
		return "Home.html";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login.html";
	}
	
	@GetMapping("/Home")
	public String goUserHome(Authentication authentication, Model model) {
		
		
		//Retrieve the name of the logged in user
		String name = authentication.getName();
		
		//Retrieve roles
		ArrayList<String> roles = new ArrayList<String>();
		for(GrantedAuthority ga : authentication.getAuthorities()) {
			roles.add(ga.getAuthority());
		}
		
		//Check if the user is a professor
		if(roles.contains("ROLE_PROFESSOR")) {
			//Professor Home
			
			model = professorHome(model);
			
			
			return "/user/ProfessorHome.html";
		}else {
			
			//Student Home
			
			Student s = studentRepository.findBySname(name);
			
			model.addAttribute("student", s);
			
			
			ArrayList<Double> grades = weightedAverage(s);
			
			//Weighted grade calculation
			
			model.addAttribute("weightedExercises", grades.get(0));
			model.addAttribute("weightedAssignment1", grades.get(1));
			model.addAttribute("weightedAssignment2", grades.get(2));
			model.addAttribute("weightedMidterm", grades.get(3));
			model.addAttribute("weightedFinalexam", grades.get(4));
			model.addAttribute("weightedFinalproject", grades.get(5));
			
			//Letter grades
			model.addAttribute("letterExercises", getLetterGrade(s.getExercises()));
			model.addAttribute("letterAssignment1", getLetterGrade(s.getAssignment1()));
			model.addAttribute("letterAssignment2", getLetterGrade(s.getAssignment2()));
			model.addAttribute("letterMidterm", getLetterGrade(s.getMidterm()));
			model.addAttribute("letterFinalexam", getLetterGrade(s.getFinalExam()));
			model.addAttribute("letterFinalproject", getLetterGrade(s.getFinalProject()));
			
			//Weighted total
			model.addAttribute("weightedTotal", grades.get(6));
			
			//Letter grade of the weighted average
			model.addAttribute("gradeLetterTotal", getLetterGrade(grades.get(6)));
			
			
			return "/user/StudentHome.html";
		}
	}
	
	
	@GetMapping("/viewStudent/{studentid}")
	public String viewStudent(@PathVariable Long studentid, Model model) {
		
		Student s = studentRepository.findById(studentid).get();
		
		model.addAttribute("student", s);
		
		ArrayList<Double> grades = weightedAverage(s);
		
		model.addAttribute("weightedTotal", grades.get(6));
		
		model.addAttribute("gradeLetterTotal", getLetterGrade(grades.get(6)));
		
		
		
		return "/user/viewStudent.html";
	}
	
	
	@GetMapping("/editStudent/{studentid}")
	public String editStudent(@PathVariable Long studentid, Model model) {
		
		
		Student student = studentRepository.findById(studentid).get();
		
		model.addAttribute("student", student);
		
		
		return "/user/editStudent.html";
	}
	
	
	@GetMapping("/updateStudent")
	public String updateStudent(@ModelAttribute Student student, Model model) {
		
		Student s = studentRepository.findById(student.getId()).get();
		
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Student>> validationErrors = validator.validate(student);

		if (!validationErrors.isEmpty()) {
			List<String> errors = new ArrayList<String>();
			for (ConstraintViolation<Student> e : validationErrors) {
				errors.add(e.getPropertyPath() + " : " + e.getMessage());
			}
			model.addAttribute("errors", errors);

			model.addAttribute("student", student);

			return "/user/addStudent.html";
		}
		
		
		if(studentRepository.findBySid(student.getSid()) != null
				&& studentRepository.findBySid(student.getSid()).getId() != s.getId()) {
			
			// Messages
			List<String> errors = new ArrayList<String>();
			errors.add("Student name is already registered with a different student.");

			model.addAttribute("errors", errors);

			model.addAttribute("student", student);
			
			return "/user/editStudent.html";
			
		} else if (studentRepository.findBySname(student.getSname()) != null
				&& studentRepository.findBySname(student.getSname()).getId() != s.getId()) {

			// Messages
			List<String> errors = new ArrayList<String>();
			errors.add("Student ID is already registered with a different student.");

			model.addAttribute("errors", errors);

			model.addAttribute("student", student);

			return "/user/editStudent.html";
						
		}
		
		studentRepository.save(student);
		

		// Messages
		List<String> messages = new ArrayList<String>();
		messages.add("Student successfully updated");

		model.addAttribute("messages", messages);
		

		model = professorHome(model);
		
		return "/user/ProfessorHome.html";
	}
	
	
	@GetMapping("/addStudent")
	public String addStudent(Model model) {
		
		model.addAttribute("student", new Student());
		
		return "/user/addStudent.html";
	}
	
	
	@GetMapping("/saveStudent")
	public String saveStudent(@ModelAttribute Student student, Model model) {
		
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Student>> validationErrors = validator.validate(student);

		if (!validationErrors.isEmpty()) {
			List<String> errors = new ArrayList<String>();
			for (ConstraintViolation<Student> s : validationErrors) {
				errors.add(s.getPropertyPath() + " : " + s.getMessage());
			}
			model.addAttribute("errors", errors);

			model.addAttribute("student", student);

			return "/user/addStudent.html";
		} else if (studentRepository.findBySname(student.getSname()) != null) {
			// If the given student name already exists

			// Messages
			List<String> errors = new ArrayList<String>();
			errors.add("Student Name exists");

			model.addAttribute("messages", errors);

			return "/user/addStudent.html";
		} else if (studentRepository.findBySid(student.getSid()) != null) {
			// If the given student ID already exists

			// Messages
			List<String> errors = new ArrayList<String>();
			errors.add("Student ID exists");

			model.addAttribute("messages", errors);

			return "/user/addStudent.html";
		}

		studentRepository.save(student);
		
		
		
		User user = new User(student.getSname(), encodePassword(student.getSid()));
		user.getRoles().add(roleRepository.findByRolename("ROLE_STUDENT"));

		userRepository.save(user);
		
		
		model = professorHome(model);

		// Messages
		List<String> messages = new ArrayList<String>();
		messages.add("New student (" + student.getSname() + ", " + student.getSid() + ") added");

		model.addAttribute("messages", messages);
		
		return "/user/ProfessorHome.html";
	}
	
	@GetMapping("/deleteStudent/{studentid}")
	public String deleteStudent(@PathVariable Long studentid, Model model) {
		
		//Get the student
		Student s = studentRepository.findById(studentid).get();
		
		//Get the name and sid
		String sname = s.getSname();
		String sid = s.getSid();
		
		//Delete from the repository
		if (s != null) {
			studentRepository.delete(s);
		}
		
		//Perform all the necessary calculations
		model = professorHome(model);
		
		//Messages
		List<String> messages = new ArrayList<String>();
		messages.add("Student (" + sname + ", " + sid + ") deleted");
		
		model.addAttribute("messages", messages);
		
		//Delete the user account of the student
		User u = userRepository.findByUsername(sname);
		userRepository.delete(u);

		return "/user/ProfessorHome.html";
		
		
	}
	
	//Input: Grade
	//Output: Calculated weighted grade
	private double weighted(double grade, int weight) {
		
		String str = String.format("%.2f", ((grade / 100) * weight));
		return Double.parseDouble(str);
	}
	
	//Input: Student object
	//Output: Weighted Grade
	//Calculates weighted grade using data from the given Student object
	private ArrayList<Double> weightedAverage(Student s) {
		
		ArrayList<Double> array = new ArrayList<>();
		
		
		double weightedExercises = weighted(s.getExercises(), 10);
		double weightedAssign1 = weighted(s.getAssignment1(), 5);
		double weightedAssign2 = weighted(s.getAssignment2(), 5);
		double weightedMidterm = weighted(s.getMidterm(), 30);
		double weightedFinalexam = weighted(s.getFinalExam(), 35);
		double weightedFinalproject = weighted(s.getFinalProject(), 15);
		
		double weightedTotal = weightedExercises + weightedAssign1 + weightedAssign2 + weightedMidterm
				+ weightedFinalexam + weightedFinalproject;
		
		array.add(weightedExercises);
		array.add(weightedAssign1);
		array.add(weightedAssign2);
		array.add(weightedMidterm);
		array.add(weightedFinalexam);
		array.add(weightedFinalproject);
		array.add(weightedTotal);
		
		return array;
	}
	
	/*
	 * Calculations for student, grade averages, and class total
	 */
	private Model professorHome(Model model) {
		
		double exercisesTotal = 0;
		double assignment1Total = 0;
		double assignment2Total = 0;
		double midtermTotal = 0;
		double finalexamTotal = 0;
		double finalprojectTotal = 0;
		double total = 0;
		
		for (Student s : studentRepository.findAll()) {
			
			exercisesTotal += s.getExercises();
			assignment1Total += s.getAssignment1();
			assignment2Total += s.getAssignment2();
			midtermTotal += s.getMidterm();
			finalexamTotal += s.getFinalExam();
			finalprojectTotal += s.getFinalProject();
			
			ArrayList<Double> grades = weightedAverage(s);
			total += grades.get(6);

		}
		
		model.addAttribute("exercisesAverage", exercisesTotal / studentRepository.count());
		model.addAttribute("assignment1Average", assignment1Total / studentRepository.count());
		model.addAttribute("assignment2Average", assignment2Total / studentRepository.count());
		model.addAttribute("midtermAverage", midtermTotal / studentRepository.count());
		model.addAttribute("finalexamAverage", finalexamTotal / studentRepository.count());
		model.addAttribute("finalprojectAverage", finalprojectTotal / studentRepository.count());
		model.addAttribute("average", total / studentRepository.count());

		
		model.addAttribute("students", studentRepository.findAll());
		
		model.addAttribute("numStudents", studentRepository.count());
		
		return model;
	}
	
	
	//Input: Grade
	//Output: Letter Grade
	private String getLetterGrade(double grade) {
		
		if(grade >= 90) {
			return "A+";
		} else if (grade >= 80) {
			return "A";
		} else if (grade >= 75) {
			return "B+";
		} else if (grade >= 70) {
			return "B";
		} else if (grade >= 65) {
			return "C+";
		} else if (grade >= 60) {
			return "C";
		} else if (grade >= 50) {
			return "D";
		} else {
			return "F";
		}
		
		
	}
	
	private String encodePassword(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}
}
