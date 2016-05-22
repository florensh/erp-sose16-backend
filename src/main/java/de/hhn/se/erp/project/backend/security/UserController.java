package de.hhn.se.erp.project.backend.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	SignUpService signUpService;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@RequestMapping(value = "/api/users/current", method = RequestMethod.GET)
	public User getCurrent() {
		final Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		if (authentication instanceof UserAuthentication) {
			return ((UserAuthentication) authentication).getDetails();
		}
		return new User(authentication.getName()); // anonymous user support
	}

	@RequestMapping(value = "/api/register", method = RequestMethod.POST)
	public ResponseEntity<User> addUser(@RequestBody final User user,
			HttpServletRequest req, HttpServletResponse res) {

		// if (user.getPassword() == null || user.getPassword().length() < 4) {
		// return new ResponseEntity<ResponseMessage>(new
		// ResponseMessage("password to short"),
		// HttpStatus.UNPROCESSABLE_ENTITY);
		// }

		User newUser = this.signUpService.execute(user.getUsername(),
				user.getPassword());

		UserAuthentication authentication = new UserAuthentication(newUser);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		tokenAuthenticationService.addAuthentication(res, authentication);

		// return ((UserAuthentication) authentication).getDetails();
		return new ResponseEntity<User>(newUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/users/current", method = RequestMethod.PATCH)
	public ResponseEntity<String> changePassword(@RequestBody final User user) {
		final Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		final User currentUser = userRepository.findByUsername(authentication
				.getName());

		if (user.getNewPassword() == null || user.getNewPassword().length() < 4) {
			return new ResponseEntity<String>("new password to short",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		final BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();
		if (!pwEncoder.matches(user.getPassword(), currentUser.getPassword())) {
			return new ResponseEntity<String>("old password mismatch",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		currentUser.setPassword(pwEncoder.encode(user.getNewPassword()));
		userRepository.saveAndFlush(currentUser);
		return new ResponseEntity<String>("password changed", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/users/{user}/grant/role/{role}", method = RequestMethod.POST)
	public ResponseEntity<String> grantRole(@PathVariable User user,
			@PathVariable UserRole role) {
		if (user == null) {
			return new ResponseEntity<String>("invalid user id",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		user.grantRole(role);
		userRepository.saveAndFlush(user);
		return new ResponseEntity<String>("role granted", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/users/{user}/revoke/role/{role}", method = RequestMethod.POST)
	public ResponseEntity<String> revokeRole(@PathVariable User user,
			@PathVariable UserRole role) {
		if (user == null) {
			return new ResponseEntity<String>("invalid user id",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		user.revokeRole(role);
		userRepository.saveAndFlush(user);
		return new ResponseEntity<String>("role revoked", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/users", method = RequestMethod.GET)
	public List<User> list() {
		return userRepository.findAll();
	}
}
