package ca.sheridancollege.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Player {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull(message="Please type the name")
	@NotEmpty(message="Please type the name")
	private String name;
	@NotNull(message="Please type the age")
	private Integer age;
	@NotNull(message="Please type the gender")
	@NotEmpty(message="Please type the gender")
	@Pattern(regexp="[MFmf]", message="Gender must be 'M' or 'F'")
	private String gender;
	@NotNull(message="Please type the phone number")
	@NotEmpty(message="Please type the phone number")
	@Pattern(regexp="\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}", message="Please type a Canadian phone number")
	private String phone;
	@NotNull(message="Please type the email")
	@NotEmpty(message="Please type the email")
	@Pattern(regexp=".*@[A-Za-z0-9-_.]*\\.[A-Za-z0-9-_.]*", message="Wrong email format")
	private String email;
	
	private String team;
}
