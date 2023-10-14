package br.com.oasc.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.oasc.todolist.user.IUserRepository;
import br.com.oasc.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

	@Autowired
	private IUserRepository iUserRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var servletPath = request.getServletPath();
		if (servletPath.startsWith("/tasks/")) {
			// Pega a auth (user and password)
			var auth = request.getHeader("Authorization");

			// Pega só o hash sem a palavra "basic"
			var userCode = auth.substring("Basic".length()).trim();

			// Depois descritografa o hash
			var authString = new String(Base64.getDecoder().decode(userCode));

			// split vai dividir a string pelo :
			// ficaria user = XPTO e password = 1234
			var userDecode = authString.split(":");
			String userName = userDecode[0];
			String userPassword = userDecode[1];

			// Valida user
			UserModel user = this.iUserRepository.findByUserName(userName);

			if (user == null) {
				response.sendError(401);
			} else {
				// Valida password
				var passwordVerify = BCrypt.verifyer().verify(userPassword.toCharArray(), user.getPassword());

				if (passwordVerify.verified) {
					//aqui é enviado para a outra API pelo request o numero de IdUser.
					request.setAttribute("idUser", user.getId());
					// Segue viagem
					filterChain.doFilter(request, response);
				} else {
					response.sendError(401);
				}
			}
		}else {
			//Caminho feliz
			filterChain.doFilter(request, response);
		}
	}
}
