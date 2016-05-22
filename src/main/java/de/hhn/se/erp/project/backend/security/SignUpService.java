package de.hhn.se.erp.project.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

	@Autowired
	private UserRepository userRepository;

	public User execute(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(new BCryptPasswordEncoder().encode(password));
		user.grantRole(username.equals("admin") ? UserRole.ADMIN
				: UserRole.STUDENT);
		return userRepository.save(user);
	}

}
