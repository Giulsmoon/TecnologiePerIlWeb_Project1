package it.polimi.tiw.missions.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.missions.beans.User;

/**
 * Servlet Filter implementation class GuestCheck
 */
public class GuestCheck implements Filter {

	/**
	 * Default constructor.
	 */
	public GuestCheck() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		System.out.print("GuestUser checking permission ...\n");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		
		if (s.getAttribute("user") != null) {
			s.invalidate();
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Permission Denied!: Slogged server side");
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
