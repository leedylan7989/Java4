package ca.sheridancollege.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class Appointment {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull(message="Client name should not be empty")
	@NotEmpty(message="Client name should not be empty")
	private String client;
	@NotNull(message="Month field should not be empty")
	@Min(message="Month range is 1 - 12", value = 1)
	@Max(value = 12, message = "Month range is 1 - 12")
	private int month;
	@NotNull(message="Day field should not be empty")
	@Min(message="Day range is 1 - 30", value = 1)
	@Max(value = 30, message = "Day range is 1 - 30")
	private int day;
	@NotNull(message="Start time should not be empty")
	@Min(message="Start time range is 8 - 19", value = 8)
	@Max(value = 19, message = "Start time range is 8 - 19")
	private int start;
	
	//Message can be empty
	private String message;
	
	//This field is never null or empty
	private String trainer;
}
