package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.dao.ImageDAO;


@WebServlet("/GetImagesOfAlbum")
public class GetImagesOfAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GetImagesOfAlbum() {
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

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String id = req.getParameter("albumId");
		if (id != null) {
			int albumId = 0;
			try {
				albumId = Integer.parseInt(id);
			} catch (NumberFormatException e) {
				res.sendError(505, "Bad number format");
			}
			ImageDAO imgDao = new ImageDAO(connection);
			List<Image> images;
			try {
				images = imgDao.findImagesByAlbum(albumId);
				String path =  "ImageList.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(req, res, servletContext, req.getLocale());
				ctx.setVariable("images", images);
				templateEngine.process(path, ctx, res.getWriter());

			} catch (

			SQLException e) {
				res.sendError(500, "Database access failed");
			}
		} else {
			res.sendError(505, "Bad topic ID");
		}
	}
}