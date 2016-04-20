package de.hhn.se.embedded.zigbee.backend.room;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.hhn.se.embedded.zigbee.backend.security.User;
import de.hhn.se.embedded.zigbee.backend.security.UserRepository;

@RestController
public class RoomController {

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	UserRepository userRepository;

	@RequestMapping(value = "/api/rooms/register", method = RequestMethod.POST)
	public Room addUser(@RequestBody final Room room, HttpServletRequest req,
			HttpServletResponse res, Principal currentUser) {
		
		User user = this.userRepository.findByUsername(currentUser.getName());
		room.setUser(user);
		this.roomRepository.save(room);
		return room;
	}

}
