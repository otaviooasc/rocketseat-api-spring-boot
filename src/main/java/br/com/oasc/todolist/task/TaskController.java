package br.com.oasc.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.oasc.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

	@Autowired
	private ITaskRepository repository;

	@PostMapping("/")
	public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

		var currentDate = LocalDateTime.now();
		// verifica se a data atual é maior q a data de inserção.
		if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("A data de início / data de término deve ser maior do que a data atual.");
		}

		if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("A data de início deve ser menor que a data de término.");
		}

		taskModel.setIdUser((UUID) request.getAttribute("idUser"));
		return ResponseEntity.status(HttpStatus.CREATED).body(this.repository.save(taskModel));
	}

	@GetMapping("/")
	public ResponseEntity<Object> list(HttpServletRequest request) {
		var idUser = (UUID) request.getAttribute("idUser");
		return ResponseEntity.ok().body(this.repository.findByIdUser((UUID) idUser));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody TaskModel taskModel, @PathVariable UUID id,
			HttpServletRequest request) {
		var task = this.repository.findById(id).orElse(null);
		
		if(task == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Tarefa não encontrada.");
		}
		
		var idUser = (UUID) request.getAttribute("idUser");
		
		if(!task.getIdUser().equals(idUser)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Usuário não tem permissão para alterar essa tarefa.");
		}

		Utils.copyNonNullProperties(taskModel, task);
		return ResponseEntity.ok().body(this.repository.save(task));
	}
}
