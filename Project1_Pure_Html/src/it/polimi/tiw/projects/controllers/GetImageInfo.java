package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.beans.Comment;
import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.beans.Project;
import it.polimi.tiw.projects.dao.CommentDAO;
import it.polimi.tiw.projects.dao.ImageDAO;
import it.polimi.tiw.projects.dao.ProjectDAO;

@WebServlet("/GetImageInfo")
public class GetImageInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GetImageInfo() {
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
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String urlImageId = request.getParameter("imageId");
		ImageDAO imageDao = new ImageDAO(connection);
		int chosenImageId = 0;
		List<Comment> comments = null;
		Image selectedImage;
		try {
			if (urlImageId == null) {
				chosenImageId = imageDao.findDefaultImage().getId();
				
 			} else {
				chosenImageId = Integer.parseInt(urlImageId);
			}
			CommentDAO cDao = new CommentDAO(connection);
			selectedImage = imageDao.findDefaultImage();
			comments = cDao.findCommentsOfImage(chosenImageId);
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in admin's project database extraction");
		}
		String path = "ImageList.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("chosenImage", urlImageId);
		ctx.setVariable("comments", comments);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
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
