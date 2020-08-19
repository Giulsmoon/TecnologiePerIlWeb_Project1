package it.polimi.tiw.projects.controllers;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.List;
import it.polimi.tiw.projects.beans.Album;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AlbumDAO;

//@WebServlet(urlPatterns = {"/GetTopics", "/"})
@WebServlet("/GoToHomePage")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToHomePage() {
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
		try {

			String driver = servletContext.getInitParameter("dbDriver");
			String url = servletContext.getInitParameter("dbUrl");
			String user = servletContext.getInitParameter("dbUser");
			String password = servletContext.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		User u = null;
		HttpSession session = req.getSession();
		AlbumDAO aDAO = new AlbumDAO(connection);
		List<Album> albums;
		u = (User) session.getAttribute("user");
		try {
			albums = aDAO.findAllAlbums();
			String path = "ClickableAlbumList.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(req, res, servletContext, req.getLocale());
			
			//controllo che l'utente sia loggato, così da mostrare il bottone logout se lo fosse oppure il bottone 
			//login se non fosse loggato
			if(u!=null) {
				ctx.setVariable("userLogged", true);
			}else {
				ctx.setVariable("userLogged", false);
			}
			
			ctx.setVariable("albums", albums);
			templateEngine.process(path, ctx, res.getWriter());
		} catch (

		SQLException e) {
			res.sendError(500, "Database access failed");
		}
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}
