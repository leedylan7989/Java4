package ca.sheridancollege.beans;

import javax.persistence.Entity;
import javax.persistence.FetchType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.*;

@NoArgsConstructor
@Entity
@Data
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String username;
	private String encryptedpassword;
	
	public User(String username, String encryptedpassword) {
		this.username = username;
		this.encryptedpassword = encryptedpassword;
	}
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	List<Role> roles = new ArrayList<Role>();
}
