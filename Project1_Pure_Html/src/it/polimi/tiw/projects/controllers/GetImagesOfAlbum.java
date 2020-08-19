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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.Comment;
import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.beans.User;
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

	public List<Image> findImagesToDisplay(int previousBlockNumber, List<Image> totalImagesList) {
		List<Image> imagesToDisplay = new ArrayList<Image>();
		int startingPoint = previousBlockNumber * 5;
		int endingPoint = startingPoint + 5;
		int i = startingPoint;
		while (i < endingPoint && i < totalImagesList.size()) {
			imagesToDisplay.add(totalImagesList.get(i));
			i++;
		}

		return imagesToDisplay;
	}

	public boolean checkALbumId(int albumId) {
		boolean correct = false;
		AlbumDAO albumDao = new AlbumDAO(connection);
		try {
			if (albumDao.findAlbumById(albumId).getId() != 0) {
				correct = true;
			} else {
				correct = false;
			}
		} catch (SQLException e) {
			correct = false;
		}

		return correct;
	}

	public String getAlbumTitleFromId(int albumId) {
		String albumTitle=null;
		AlbumDAO albumDao = new AlbumDAO(connection);
		try {
		albumTitle=albumDao.findAlbumTitleById(albumId);
		} catch (SQLException e) {
		}
		return albumTitle;
	}

	public boolean selectedImageInTheAlbum(String urlImageId, List<Image> images) {
		boolean correct = false;
		if (urlImageId != null) {
			int chosenImageId = Integer.parseInt(urlImageId);

			for (Image image : images) {
				if (image.getId() == chosenImageId) {
					correct = true;
					break;
				}
			}
		}
		return correct;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		User u = null;
		HttpSession s = req.getSession();
		String urlAlbumId = req.getParameter("albumId");
		String urlImageId = req.getParameter("imageId");
		String urlNextImages = req.getParameter("nextImages");
		String urlPreviousImages = req.getParameter("previousImages");
		u = (User) s.getAttribute("user");

		if (urlAlbumId != null) {
			int albumId = 0;
			try {
				albumId = Integer.parseInt(urlAlbumId);
			} catch (NumberFormatException e) {
				res.sendError(505, "Bad number format");
			}

			if (checkALbumId(albumId)) {

				ImageDAO imgDao = new ImageDAO(connection);
				List<Image> images = null;
				List<Image> imagesToDisplay;
				int numberOfBlocks = 0; // il numero di immagini dell'album diviso 5, per capire quante volte posso
										// usare i bottoni per scorrere la lista di immagini
				int nextImages = 0;
				int previousImages = 0;

				int chosenImageId = 0;
				Image selectedImage = null;

				String albumTitle=null;

				try {
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
						if (somma != (numberOfBlocks - 1) || nextImagesFromRequest < 0
								|| previousImagesFromRequest < 0) {
							nextImages = numberOfBlocks - 1;
							previousImages = 0;
						} else {
							nextImages = nextImagesFromRequest;
							previousImages = previousImagesFromRequest;
						}
					}
					imagesToDisplay = findImagesToDisplay(previousImages, images);

					// image selection (default or with id)
					if (urlImageId == null || urlImageId != null && !selectedImageInTheAlbum(urlImageId, imagesToDisplay)) {
						chosenImageId = imagesToDisplay.get(0).getId();
						selectedImage = imagesToDisplay.get(0); // Get the first as "default"
					} else {
						chosenImageId = Integer.parseInt(urlImageId);
						selectedImage = imgDao.findImagesById(chosenImageId);
					}

					CommentDAO cDao = new CommentDAO(connection);
					//trovo per ogni immagine selezionata la lista dei suoi commenti
					List<Comment> comments = cDao.findCommentsOfImage(selectedImage.getId());
					//setto l'attributo username per ogni commento della lista
					cDao.findUsernameOfComment(comments);
					albumTitle=getAlbumTitleFromId(albumId);

					String path = "ImageList.html";
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(req, res, servletContext, req.getLocale());
					if(u!=null) {
						ctx.setVariable("userLogged", true);
					}else {
						ctx.setVariable("userLogged", false);
					}

					ctx.setVariable("images", imagesToDisplay);
					ctx.setVariable("albumId", albumId);
					ctx.setVariable("chosenImageId", chosenImageId);
					ctx.setVariable("imageSelected", selectedImage);
					ctx.setVariable("comments", comments);
					ctx.setVariable("nextImages", nextImages);
					ctx.setVariable("previousImages", previousImages);
					ctx.setVariable("albumTitle", albumTitle);

					templateEngine.process(path, ctx, res.getWriter());

				} catch (

				SQLException e) {
					res.sendError(500, "Database access failed");
				}
			} else {
				String path = getServletContext().getContextPath() + "/GoToHomePage";
				res.sendRedirect(path);
			}
		} else {
			res.sendError(505, "Bad album ID");
		}
	}
}
