package br.com.oasc.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb-users")
public class UserModel {
	
	@Id
	@GeneratedValue(generator = "UUID")
	private UUID id;
	
	@Column(name = "user-name", unique = true)
	private String userName;
	private String name;
	private String password;
	
	@CreationTimestamp
	private LocalDateTime creatAt;
}
