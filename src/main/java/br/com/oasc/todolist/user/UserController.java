package br.com.oasc.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private IUserRepository repository;
	
	@PostMapping("/")
	public ResponseEntity<Object> create(@RequestBody UserModel userModel) {
		if(this.repository.existsByUserName(userModel.getUserName())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario já existe.");
		}
		
		var password = BCrypt.withDefaults()
				.hashToString(12, userModel.getPassword().toCharArray());
		
		userModel.setPassword(password);
		return ResponseEntity.status(HttpStatus.CREATED).body(this.repository.save(userModel));
	}
	
	@GetMapping("/")
	public ResponseEntity<Object> listUsers() {
		return ResponseEntity.status(HttpStatus.OK).body(this.repository.findAll());
	}
}
