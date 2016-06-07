package de.hhn.se.erp.project.backend;

import javax.servlet.Filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CharacterEncodingFilter;

import de.hhn.se.erp.project.backend.security.User;
import de.hhn.se.erp.project.backend.security.UserRepository;
import de.hhn.se.erp.project.backend.security.UserRole;

@RestController
@SpringBootApplication
public class Application {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public InitializingBean insertDefaultUsers() {
		return new InitializingBean() {
			@Autowired
			private UserRepository userRepository;

			@Override
			public void afterPropertiesSet() {
				addUser("184352", "184352", UserRole.STUDENT);
				addUser("hoetzel", "hoetzel", UserRole.DOZENT);

			}

			private void addUser(String username, String password, UserRole role) {

				User user = new User();
				user.setUsername(username);
				user.setPassword(new BCryptPasswordEncoder().encode(password));
				user.grantRole(role);
				user.grantRole(UserRole.USER);
				user = userRepository.save(user);

			}
		};
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}

}