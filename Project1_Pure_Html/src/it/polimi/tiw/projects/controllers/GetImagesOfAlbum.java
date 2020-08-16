package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

import it.polimi.tiw.projects.beans.Comment;
import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.dao.AlbumDAO;
import it.polimi.tiw.projects.dao.CommentDAO;
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

	
	public List<Image> findImagesToDisplay(int startingPoint, List<Image> totalImagesList){
		List<Image> imagesToDisplay = new ArrayList<Image>();
		int i= startingPoint*5;
		while(i< startingPoint+5 || i<totalImagesList.size()) {
			imagesToDisplay.add(totalImagesList.get(i));
			i++;
		}
		
		return imagesToDisplay;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String id = req.getParameter("albumId");
		String urlImageId = req.getParameter("imageId");
		String urlNextImages = req.getParameter("next");
		String urlPreviousImages = req.getParameter("previous");

		if (id != null) {
			int albumId = 0;
			try {
				albumId = Integer.parseInt(id);
			} catch (NumberFormatException e) {
				res.sendError(505, "Bad number format");
			}
			ImageDAO imgDao = new ImageDAO(connection);
			List<Image> images;
			List<Image> imagesToDisplay;
			int numberOfBlocks = 0; // il numero di immagini dell'album diviso 5, per capire quante volte posso
									// usare i bottoni per scorrere la lista di immagini
			int nextImages = 0;
			int previousImages = 0;
			AlbumDAO albumDao = new AlbumDAO(connection);
			List<Comment> comments = null;
			CommentDAO cDao = new CommentDAO(connection);
			int chosenImageId = 0;
			Image selectedImage = null;
			try {
				if (urlImageId == null) {
					chosenImageId = albumDao.findDefaultImage(albumId).getId();
					selectedImage = albumDao.findDefaultImage(albumId);
				} else {
					chosenImageId = Integer.parseInt(urlImageId);
					selectedImage = imgDao.findImagesById(chosenImageId);
				}
				images = imgDao.findImagesByAlbum(albumId);
				if (images.size() % 5 == 0) {
					numberOfBlocks = Math.floorDiv(images.size(), 5);
				} else {
					numberOfBlocks = Math.floorDiv(images.size(), 5) + 1;
				}
				// Se questi due valori dalla request sono null vuol dire
				// che sono nel caso in cui la pagina � stata appena aperta
				// e quindi setto dei valori di default
				if (urlNextImages == null || urlPreviousImages == null) {
					nextImages = numberOfBlocks - 1;
					previousImages = 0;
				} else {
					int nextImagesFromRequest = 0;
					int previousImagesFromRequest = 0;
					try {
						nextImagesFromRequest = Integer.parseInt(urlNextImages);
						previousImagesFromRequest = Integer.parseInt(urlPreviousImages);
					} catch (NumberFormatException e) {
						res.sendError(505, "Bad number format");
					}
					int somma = 0;
					somma = nextImagesFromRequest + previousImagesFromRequest;
					if (somma != (numberOfBlocks - 1)) {
						nextImages = numberOfBlocks - 1;
						previousImages = 0;
					}else {
						nextImages = nextImagesFromRequest;
						previousImages = previousImagesFromRequest;
					}
				}

				imagesToDisplay = findImagesToDisplay(previousImages, images);
				comments = cDao.findCommentsOfImage(selectedImage.getId());
				String path = "ImageList.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(req, res, servletContext, req.getLocale());
				ctx.setVariable("images", imagesToDisplay);
				ctx.setVariable("albumId", albumId);
				ctx.setVariable("chosenImageId", chosenImageId);
				ctx.setVariable("imageSelected", selectedImage);
				ctx.setVariable("comments", comments);
				ctx.setVariable("nextImages", nextImages);
				ctx.setVariable("previousImages", previousImages);
				templateEngine.process(path, ctx, res.getWriter());

			} catch (

			SQLException e) {
				res.sendError(500, "Database access failed");
			}
		} else {
			res.sendError(505, "Bad album ID");
		}

	}
}
