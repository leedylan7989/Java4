package ca.sheridancollege.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private LoginAccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		
		//Must be deleted after the development
		
		http.csrf().disable();
		
		http.headers().frameOptions().disable();
		
		http.authorizeRequests()
		.antMatchers("/").permitAll()
		.antMatchers("/searchTrainer").hasAnyRole("TRAINER", "CLIENT")
		.antMatchers("/addAppointment").hasAnyRole("TRAINER", "CLIENT")
		.antMatchers("/addTrainer").hasRole("TRAINER")
		.antMatchers("/checkTrainer").hasRole("TRAINER")
		.antMatchers("/logout").permitAll()
		.antMatchers("/register").permitAll()
		.antMatchers("/appointments").permitAll()
		.antMatchers("/appointments/**").permitAll()
		.antMatchers("/h2-console/**").permitAll()
		.anyRequest().authenticated()
		.and()
			.formLogin()
			.permitAll()
		.and()
			.logout()
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/")
			.permitAll()
		.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler);
			
	}
	
	//BCryptPassword Encoder instantiation
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
}
