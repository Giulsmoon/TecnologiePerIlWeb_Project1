package it.polimi.tiw.missions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.missions.beans.Comment;
import it.polimi.tiw.missions.beans.User;
import it.polimi.tiw.missions.dao.CommentDAO;
import it.polimi.tiw.missions.utils.ConnectionHandler;

@WebServlet("/CreateComment")
@MultipartConfig
public class CreateComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateComment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		boolean isBadRequest = false;
		User user = null;
		HttpSession s = request.getSession();
		request.setCharacterEncoding("UTF-8");
		String comment = null;
		Integer imageId = null;
		user = (User) s.getAttribute("user");
		
		try {
			comment = request.getParameter("comment");
			imageId = Integer.parseInt(request.getParameter("imageId"));
			isBadRequest = comment.isEmpty() || imageId <=0;
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}

		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		} else {
			if(user==null || user.getId()<=0) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("You have to log in order to comment");
				
			}
			else {
				
				CommentDAO commentDAO = new CommentDAO(connection);
				try {
					Comment commentResponse = new Comment();
					//inserisco nel database il commento con i parametri presi dalla richiesta
					commentDAO.createComment(comment, imageId, user.getId());
					//ottengo dal database l'ultimo commento inserito per inviarlo al client per farlo visualizzare
					//senza ricaricare tutta la pagina
					commentResponse = commentDAO.findLastInsertedComment();
					commentDAO.findUsernameOfComment(commentResponse);
					response.setStatus(HttpServletResponse.SC_OK);
					Gson gson = new GsonBuilder()
							.setDateFormat("yyyy-MM-dd").create();
					String json = gson.toJson(commentResponse);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(json);
					 
					

				} catch (SQLException e) {

					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Failure of comment creation in database");
					return;
				}
			}
		}
		
		
		
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
