package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * Servlet implementation class OpenRegistrationForm
 */
@WebServlet("/OpenRegistrationForm")
public class OpenRegistrationForm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OpenRegistrationForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");	
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Boolean characterDosentMatch = Boolean.parseBoolean(request.getParameter("characterDosentMatch"));
		Boolean wrongPasswords = Boolean.parseBoolean(request.getParameter("wrongPasswords"));
		Boolean usernameExist = Boolean.parseBoolean(request.getParameter("usernameExist"));
		String path = "index.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		ctx.setVariable("RegistrationForm", true); // deve comparire la form per la registrazione

		if (characterDosentMatch) {
			ctx.setVariable("CharacterDosentMatch", true); // le stringe username e password devono essere
															// alpha-numeriche
		}
		if (wrongPasswords) {
			ctx.setVariable("WrongPasswords", true); // le due password inserite sono sbagliate
		}
		if (usernameExist) {
			ctx.setVariable("UsernameExist", true); // lo username inserito è già esistente nel database
		}

		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
