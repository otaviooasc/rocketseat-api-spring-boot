package br.com.oasc.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
	boolean existsByUserName(String username);
	UserModel findByUserName(String username);
}
