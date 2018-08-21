package com.user.message.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * Handler for exceptions thrown from an API
 */
@Component
@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

	@Context
	private HttpServletRequest request;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Response toResponse(Throwable ex) {

		// Handles client errors
		if (ex instanceof ClientErrorException) {
			ClientErrorException clientError = (ClientErrorException) ex;
			return Response.status(clientError.getResponse().getStatus()).entity(new ErrorMessage(clientError)).build();
		} else {
			// Otherwise it's a internal error, need to log
			ErrorMessage errorMessage = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR,
					"Internal server error");
			StringWriter errorStackTrace = new StringWriter();
			ex.printStackTrace(new PrintWriter(errorStackTrace));

			logger.error(ex.getMessage() + "\n" + buildRequestUrl(), ex);

			return Response.status(errorMessage.getStatusCode()).entity(errorMessage).type(MediaType.APPLICATION_JSON)
					.build();
		}
	}

	/**
	 * @return a request url
	 */
	public String buildRequestUrl() {
		StringBuilder message = new StringBuilder();
		String method = request.getMethod();
		message.append("API causing internal exception:\n");
		message.append("URL: ").append(getOriginalURL(request)).append("\n");
		message.append("Method: ").append(request.getMethod()).append("\n");
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			message.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
		}
		if (method == HttpMethod.POST || method == HttpMethod.PUT) {
			try {
				String body = ByteSource.wrap(ByteStreams.toByteArray(request.getInputStream()))
						.asCharSource(Charsets.UTF_8).read();
				message.append("Body: ").append(body);
			} catch (Exception e) {
				message.append("Body: Error " + e.getClass().toString());
			}
		}

		return message.toString();
	}

	private String getOriginalURL(HttpServletRequest req) {
		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();
		String queryString = req.getQueryString();

		// Reconstruct original requesting URL
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);
		url.append(":").append(serverPort);
		url.append(contextPath).append(servletPath);

		if (pathInfo != null) {
			url.append(pathInfo);
		}

		if (queryString != null) {
			url.append("?").append(queryString);
		}

		return url.toString();
	}

}