package ca.sheridancollege.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.beans.Player;
import ca.sheridancollege.beans.Team;
import ca.sheridancollege.repositories.PlayerRepository;
import ca.sheridancollege.repositories.TeamRepository;

@Controller
public class HomeController {

	//Repositories
	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private TeamRepository teamRepository;

	//Root
	@GetMapping("/")
	public String goHome(Model model) {
		return "Home.html";
	}

	//Add players
	@GetMapping("/addPlayer")
	public String goAddPlayer(Model model) {
		// Maximum of 64 players
		if (playerRepository.count() < 64) {
			model.addAttribute("player", new Player());
			return "addPlayer.html";
		} else {
			List<String> errors = new ArrayList<String>();
			errors.add("Cannot add more than 64 players.");
			model.addAttribute("errors", errors);

			return "Home.html";
		}
	}
	
	@GetMapping("/addTeam")
	public String goAddTeam(Model model) {
		if (teamRepository.count() < 8) {
			model.addAttribute("team", new Team());
			model.addAttribute("teams", teamRepository.findAll());
			return "addTeam.html";
		} else {
			List<String> errors = new ArrayList<String>();
			errors.add("Cannot add more than 8 teams.");
			model.addAttribute("errors", errors);

			return "Home.html";
		}
	}
	
	@GetMapping("/saveTeam")
	public String saveTeam(@ModelAttribute Team team, Model model) {

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Team>> validationErrors = validator.validate(team);

		if (!validationErrors.isEmpty()) {
			List<String> errors = new ArrayList<String>();
			for (ConstraintViolation<Team> t : validationErrors) {
				errors.add(t.getPropertyPath() + " : " + t.getMessage());
			}
			model.addAttribute("errors", errors);
		} else if(teamRepository.count() == 8) {
			List<String> errors = new ArrayList<String>();
			
			errors.add("Cannot add more than 8 teams.");

			model.addAttribute("errors", errors);

		}else if(teamRepository.findByName(team.getName()) != null) {

			List<String> errors = new ArrayList<String>();

			errors.add("Team [" + team.getName() + "] already exists");
			
			model.addAttribute("errors", errors);
		}
		else {
			teamRepository.save(team);
			
			List<String> messages = new ArrayList<String>();
			messages.add("Team \""+ team.getName() + "\" added!");
			
			model.addAttribute("messages", messages);
		}
		model.addAttribute("team", new Player());
		model.addAttribute("teams", teamRepository.findAll());

		return "addTeam.html";
	}
	
	//Go to manage player page
	@GetMapping("/managePlayer")
	public String goManagePlayer(Model model) {
		model.addAttribute("players", playerRepository.findAll());
		return "managePlayer.html";
	}

	//Save newly added players into the player repository
	@GetMapping("/savePlayer")
	public String savePlayer(@ModelAttribute Player player, Model model) {

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Player>> validationErrors = validator.validate(player);
		List<String> errors = new ArrayList<String>();

		if (!validationErrors.isEmpty()) {
			for (ConstraintViolation<Player> e : validationErrors) {
				errors.add(e.getPropertyPath() + " : " + e.getMessage());
			}
		} else if (playerRepository.findByName(player.getName()) != null) {
			errors.add("Player " + player.getName() + " is already registered");
		} else if (playerRepository.findByEmail(player.getEmail()) != null) {
			errors.add(player.getEmail() + " already exists in the database");
		} else {
			playerRepository.save(player);

			List<String> messages = new ArrayList<String>();
			messages.add("Player \"" + player.getName() + "\" added!");

			model.addAttribute("messages", messages);
		}

		model.addAttribute("errors", errors);
		model.addAttribute("player", new Player());

		return "addPlayer.html";
	}

	//Move a player into a team
	@GetMapping("/movePlayer")
	public String goAddTeam(Model model, @RequestParam String team, @RequestParam String id) {
		int playerid = Integer.parseInt(id);
		Team destinationTeam = teamRepository.findById(Integer.parseInt(team));
		Team currentTeam = teamRepository.findByName(playerRepository.findById(playerid).getTeam());
		List<String> errors = new ArrayList<String>();

		if (destinationTeam.getPlayers().size() >= 8) {
			
			errors.add(destinationTeam.getName() + " is full. Please choose another team.");
			
		} else if(currentTeam != null && currentTeam.getPlayers().size() <= 5) {
			
			errors.add("The current team needs at least 5 players. Please add more players to " +
			currentTeam.getName() + " before moving this player to another team");
			
		} else if (playerRepository.count() < 20) {
			errors.add("At least 20 players must be registered before adding players to teams.");
			errors.add("Current registered players: " + playerRepository.count() + " players");

		} else {
			//Add the player to the team
			Player player = playerRepository.findById(playerid);
			if (currentTeam != null) {
				// Remove the player from the original team if the player already has a team
				currentTeam.getPlayers().remove(player);
				//Update the team to the repository
				teamRepository.save(currentTeam);
			}
			//The player is now in the new team
			destinationTeam.getPlayers().add(player);
			player.setTeam(destinationTeam.getName());
			playerRepository.save(player);
			//Update the new team to the repository
			teamRepository.save(destinationTeam);
			model.addAttribute("player", playerRepository.findById(playerid));
			model.addAttribute("teams", teamRepository.findAll());
			
			//Message
			List<String> messages = new ArrayList<String>();
			messages.add("Player [" + player.getName() + "] is moved to " + destinationTeam.getName() + "!");
			
			model.addAttribute("messages", messages);
			
			model.addAttribute("players", playerRepository.findAll());
			return "managePlayer.html";
		}
		
		//Reachable when there is an error
		model.addAttribute("player", playerRepository.findById(playerid));
		model.addAttribute("teams", teamRepository.findAll());
		model.addAttribute("errors", errors);
		return "editPlayer.html";
	}

	//Organize all players into random teams
	@GetMapping("/matchPlayers")
	public String matchPlayers(Model model) {

		if (playerRepository.count() >= 20) {
			// Remove all players from teams
			for (Team t : teamRepository.findAll()) {
				for (Player p : t.getPlayers()) {
					p.setTeam("");
				}
				t.getPlayers().clear();
			}

			int numPlayers = (int) playerRepository.count();
			int numTeams = 0;
			// Maximum of 8 teams
			if (numPlayers > 40) {
				numTeams = 8;
			} else {
				numTeams = numPlayers / 5;
			}

			// Create empty teams
			// Number of teams created will be number of required teams - already created
			// teams
			int existingTeams = (int) teamRepository.count();
			for (int i = 0; i < (numTeams - existingTeams); i++) {
				Team team = Team.builder().name("Team " + (i + 1)).players(new ArrayList<Player>()).build();
				teamRepository.save(team);
			}
			
			//Male and Female Players
			List<Player> malePlayers = new ArrayList<Player>();
			List<Player> femalePlayers = new ArrayList<Player>();
			
			for(Player p : playerRepository.findAll()) {
				if(p.getGender().equals("M") || p.getGender().equals("m")) {
					malePlayers.add(p);
				} else {
					femalePlayers.add(p);
				}
			}

			//2 Males and 2 Females for each team
			int j = 0;
			for(Team t : teamRepository.findAll()) {
				j = j + 1;
				if(j > numTeams) {
					//Breaks out if all necessary number of teams have enough male and female players
					break;
				}
				for(int i = 0; i < 2; i++) {
					Player p = malePlayers.get(0);
					p.setTeam(t.getName());
					t.getPlayers().add(p);
					malePlayers.remove(0);
				}
				for (int i = 0; i < 2; i++) {
					Player p = femalePlayers.get(0);
					p.setTeam(t.getName());
					t.getPlayers().add(p);
					femalePlayers.remove(0);
				}
				teamRepository.save(t);
			}
			
			//Combine remaining players into one team
			femalePlayers.addAll(malePlayers);
			List<Player> players = femalePlayers;

			// Randomly assign remaining players
			for (Player p : players) {
				// numPlayers%numTeams + 1 = the ID of the team
				// Players will be assigned to teams randomly
				Team team = teamRepository.findById((numPlayers % numTeams) + 1);

				// Update each player object by adding team name
				p.setTeam(team.getName());

				team.getPlayers().add(p);

				teamRepository.save(team);

				numPlayers--;
			}
		
			List<String> messages = new ArrayList<String>();
			messages.add("Players are assigned to random teams!");
			
			model.addAttribute("messages", messages);

		} else {
			List<String> errors = new ArrayList<String>();
			errors.add("At least 20 players must be registered before assigning teams to players");
			errors.add("Current registered players: " + playerRepository.count() + " players");
			model.addAttribute("errors", errors);
		}
		return "Home.html";
	}

	// Search
	@GetMapping("/searchByName")
	public String searchByName(Model model, @RequestParam String name) {
		model.addAttribute("players", playerRepository.findByNameContaining(name));
		return "managePlayer.html";
	}

	@GetMapping("/searchByAge")
	public String searchByAge(Model model, @RequestParam int age) {
		model.addAttribute("players", playerRepository.findByAge(age));
		return "managePlayer.html";
	}

	@GetMapping("/searchByGender")
	public String searchByGender(Model model, @RequestParam String gender) {
		model.addAttribute("players", playerRepository.findByGender(gender));
		return "managePlayer.html";
	}

	@GetMapping("/searchByPhone")
	public String searchByPhone(Model model, @RequestParam String phone) {
		model.addAttribute("players", playerRepository.findByPhoneContaining(phone));
		return "managePlayer.html";
	}

	@GetMapping("/searchByEmail")
	public String searchByEmail(Model model, @RequestParam String email) {
		model.addAttribute("players", playerRepository.findByEmailContaining(email));
		return "managePlayer.html";
	}

	@GetMapping("/searchByTeam")
	public String searchByTeam(Model model, @RequestParam String team) {
		model.addAttribute("players", playerRepository.findByTeamContaining(team));
		return "managePlayer.html";
	}

	// Delete
	@GetMapping("/deletePlayer/{playerid}")
	public String deletePlayer(Model model, @PathVariable int playerid) {
		Player p = playerRepository.findById(playerid);
		Team t = teamRepository.findByName(p.getTeam());

		if (t != null) {
			if (t.getPlayers().size() > 5) {
				t.getPlayers().remove(p);
				teamRepository.save(t);
				playerRepository.delete(p);
			} else {
				List<String> errors = new ArrayList<String>();
				errors.add(t.getName() + " needs at least 5 players. Please add more players to this team before" + 
				" removing " + p.getName());
				model.addAttribute("errors", errors);
			}
		} else {
			playerRepository.delete(p);
		}

		model.addAttribute("players", playerRepository.findAll());
		return "managePlayer.html";
	}

	// Edit
	@GetMapping("/editPlayer/{playerid}")
	public String editPlayer(Model model, @PathVariable int playerid) {
		if (playerRepository.findById(playerid) != null) {
			model.addAttribute("player", playerRepository.findById(playerid));
			model.addAttribute("teams", teamRepository.findAll());
			return "editPlayer.html";
		} else {
			return "redirect:/managePlayer";
		}
	}

	//Update
	@GetMapping("/updatePlayer")
	public String updatePlayer(Model model, @ModelAttribute Player player) {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Player>> validationErrors = validator.validate(player);
		List<String> errors = new ArrayList<String>();
		int playerid = player.getId();
		Player p1 = playerRepository.findById(playerid);
		
		Team team = teamRepository.findByName(player.getTeam());
		int numMale = 0, numFemale = 0;
		for(Player p : team.getPlayers()) {
			if(p.getGender().equals("m") || p.getGender().equals("M")) {
				numMale++;
			} else {
				numFemale++;
			}
		}

		if (!validationErrors.isEmpty()) {
			for (ConstraintViolation<Player> e : validationErrors) {
				errors.add(e.getPropertyPath() + " : " + e.getMessage());
			}
		} else if (!p1.getName().equals(player.getName()) 
				&& playerRepository.findByName(player.getName()) != null) {
			errors.add("Player" + player.getName() + " is already registered");
		} else if (!p1.getEmail().equals(player.getEmail()) 
				&& playerRepository.findByEmail(player.getEmail()) != null) {
			errors.add(player.getEmail() + " already exists in the database");
		} else if(!player.getGender().equalsIgnoreCase(p1.getGender())
				&& p1.getGender().equalsIgnoreCase("M") 
				&& numMale <= 2) {
			errors.add("Cannot change gender. The team needs at least 2 male players.");
		} else if(!player.getGender().equalsIgnoreCase(p1.getGender())
				&& p1.getGender().equalsIgnoreCase("F") 
				&& numFemale <= 2) {
			errors.add("Cannot change gender. The team needs at least 2 female players.");
		} 
		else {
			playerRepository.save(player);
			List<String> messages = new ArrayList<String>();
			messages.add("Player [" + player.getName() + "] updated!");

			model.addAttribute("messages", messages);

			model.addAttribute("players", playerRepository.findAll());
			return "managePlayer.html";
		}
		model.addAttribute("editErrors", errors);
		model.addAttribute("teams", teamRepository.findAll());
		return "editPlayer.html";
	}
	
	//Swap
	@GetMapping("/swapPlayers")
	public String goSwapPlayers(Model model) {
		model.addAttribute("players", playerRepository.findAll());
		return "swapPlayers.html";
	}
	
	@GetMapping("/saveSwappedPlayers")
	public String saveSwappedPlayers(Model model, @RequestParam String player1, @RequestParam String player2) {
		Player p1 = playerRepository.findById(Integer.parseInt(player1));
		Team p1Team = teamRepository.findByName(p1.getTeam());
		Player p2 = playerRepository.findById(Integer.parseInt(player2));
		Team p2Team = teamRepository.findByName(p2.getTeam());
		List<String> errors = new ArrayList<String>();
		
		
		if(player1.equals(player2)) {
			errors.add("Please select a different player for Player 2");
		} else if (p1Team == null) {
			errors.add("Player 1 is not in a team");
		} else if (p2Team == null) {
			errors.add("Player 2 is not in a team");
		}else if (p1Team.getName().equals(p2Team.getName())) {
			errors.add("Please select a player from a different team");
		} else {
			//Remove the players from their team
			p1Team.getPlayers().remove(p1);
			p2Team.getPlayers().remove(p2);
			
			//Switch teams
			p1.setTeam(p2Team.getName());
			p2.setTeam(p1Team.getName());
			
			playerRepository.save(p1);
			playerRepository.save(p2);
			
			p1Team.getPlayers().add(p2);
			p2Team.getPlayers().add(p1);
			
			teamRepository.save(p1Team);
			teamRepository.save(p2Team);
			
			//Message
			List<String> message = new ArrayList<String>();
			message.add(p1.getName() + " and " + p2.getName() + " swapped teams!");
			model.addAttribute("messages", message);
			
			model.addAttribute("players", playerRepository.findAll());
			return "managePlayer.html";
		}

		model.addAttribute("errors", errors);
		model.addAttribute("players", playerRepository.findAll());
		return "swapPlayers.html";
	}

}
