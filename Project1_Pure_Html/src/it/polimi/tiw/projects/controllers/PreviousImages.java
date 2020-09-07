package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CheckGuest
 */
@WebServlet("/PreviousImages")
public class PreviousImages extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PreviousImages() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String next = request.getParameter("nextImages");
		String previous = request.getParameter("previousImages");
		String albumId = request.getParameter("albumId");

		if (next == null || previous == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter ");
			return;
		}
		int nextNum = 0;
		int previousNum = 0;
		int alId = 0;
		try {
			nextNum = Integer.parseInt(next) + 1;
			previousNum = Integer.parseInt(previous) - 1;
			alId = Integer.parseInt(albumId);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter ");
			return;
		}
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetImagesOfAlbum?albumId=" + alId + "&nextImages=" + nextNum + "&previousImages=" + previousNum;
		response.sendRedirect(path);
		}
		
	}
