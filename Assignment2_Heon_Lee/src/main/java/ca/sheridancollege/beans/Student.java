package ca.sheridancollege.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Student {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message="Please type the student ID")
	@NotEmpty(message="Please type the student ID")
	@Pattern(regexp="[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]", message="Student ID must be 9 digit characters.")
	private String sid;
	@NotNull(message="Please type the student name")
	@NotEmpty(message="Please type the student name")
	private String sname;
	@NotNull(message="\"Exercises\" field should not be empty")
	@PositiveOrZero(message="Exercises field cannot be negative")
	private Double exercises;
	@NotNull(message="\"Assignment 1\" should not be empty")
	@PositiveOrZero(message="Assignment 1 field cannot be negative")
	private Double assignment1;
	@NotNull(message="\"Assignment 2\" should not be empty")
	@PositiveOrZero(message="Assignment 2 field cannot be negative")
	private Double assignment2;
	@NotNull(message="\"Midterm\" should not be empty")
	@PositiveOrZero(message="Midterm field cannot be negative")
	private Double midterm;
	@NotNull(message="\"Final Exam\" should not be empty")
	@PositiveOrZero(message="Final exam field cannot be negative")
	private Double finalExam;
	@NotNull(message="\"Final Project\" Should not be empty")
	@PositiveOrZero(message="Final project field cannot be negative")
	private Double finalProject;
	
}
