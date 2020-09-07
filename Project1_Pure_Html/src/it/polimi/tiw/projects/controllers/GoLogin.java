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

@WebServlet("/GoLogin")
//servlet che gestisce in modo dinamico il thymleaf della pagine index.html
public class GoLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	public GoLogin() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Boolean errorNotGuest = Boolean.parseBoolean(request.getParameter("errorNotGuest"));
		Boolean errorNotLogged = Boolean.parseBoolean(request.getParameter("errorNotLogged"));
		Boolean registrationDone = Boolean.parseBoolean(request.getParameter("registrationDone"));
		Boolean logFailed = Boolean.parseBoolean(request.getParameter("logFailed"));
		Boolean slogged = Boolean.parseBoolean(request.getParameter("slogged"));
		

		String path = "index.html";

		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		
		//setta i parametri per mostrare eventuali errori o messaggi nella index grazie ai parametri di thymeleaf
		if(errorNotGuest||errorNotLogged) {
			ctx.setVariable("Error", true);
		}
		if (errorNotGuest) {
			ctx.setVariable("ErrorNotGuest", true);
		}
		if (errorNotLogged) {
			ctx.setVariable("ErrorNotLogged", true);
		}
		if (registrationDone) {
			ctx.setVariable("RegistrationDone", true);
		}
		if (logFailed) {
			ctx.setVariable("LogFailed", true);
		}
		if (slogged) {
			ctx.setVariable("Slogged", true);
		}
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
