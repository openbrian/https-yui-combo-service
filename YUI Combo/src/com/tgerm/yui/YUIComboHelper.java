package com.tgerm.yui;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Helper to do all the request response stuff for YUI Combo
 * Service(http://yui.yahooapis.com/combo?). For every inbound request this
 * class invokes the YUI Combo service and pipes the response back.
 * 
 * @author abhinav {@link abhinav@tgerm.com}
 * 
 */
public class YUIComboHelper {

	private static final String YUI_COMBO_URL = "http://yui.yahooapis.com/combo?";
	// If ON will print SOP's
	private static final Boolean DEBUG = false;

	/**
	 * Key Method thats invoked on request
	 * 
	 * @param req
	 * @param resp
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	public static void handleYuiComboRequest(HttpServletRequest req,
			HttpServletResponse resp) throws MalformedURLException,
			IOException, ProtocolException {
		// Print debugging information
		printRequestInfo(req);

		// Open connection to YUI Combo Service http://yui.yahooapis.com/combo
		HttpURLConnection conn = createConnectionToYUIComboService(req);

		// Add the required response headers.
		addResponseHeaders(resp, conn);

		// Write the actual response
		writeResponseBytes(resp, conn);
	}

	private static void addResponseHeaders(HttpServletResponse resp,
			HttpURLConnection conn) throws IOException {
		resp.setContentLength(conn.getContentLength());
		resp.setContentType(conn.getContentType());
		resp.setCharacterEncoding(conn.getContentEncoding());
		resp.setStatus(conn.getResponseCode());
		Set<Entry<String, List<String>>> hdrEntries = conn.getHeaderFields()
				.entrySet();
		for (Iterator<Entry<String, List<String>>> j = hdrEntries.iterator(); j
				.hasNext();) {
			Entry<String, List<String>> hdr = (Entry<String, List<String>>) j
					.next();
			List<String> hdrValues = hdr.getValue();
			if (hdrValues == null)
				continue;

			if (!hdrValues.isEmpty()) {
				for (Iterator<String> i = hdrValues.iterator(); i.hasNext();) {
					String hdrVal = (String) i.next();
					resp.addHeader(hdr.getKey(), hdrVal);
				}
			}

		}
		resp.setDateHeader("Last-Modified", conn.getLastModified());
		resp.setDateHeader("Expires", conn.getExpiration());
	}

	private static HttpURLConnection createConnectionToYUIComboService(
			HttpServletRequest req) throws MalformedURLException, IOException {
		String url = YUI_COMBO_URL + req.getQueryString();
		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setRequestMethod(req.getMethod());
		conn.setUseCaches(true);
		return conn;
	}

	public static void writeResponseBytes(HttpServletResponse resp,
			HttpURLConnection conn) throws IOException {
		byte[] respBytes = getByteData(conn.getInputStream());
		ServletOutputStream rout = resp.getOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(rout);
		bos.write(respBytes);
		bos.flush();
		closeSafe(bos);
		closeSafe(rout);
	}

	public static void closeSafe(InputStream is) {
		if (is != null)
			try {
				is.close();
			} catch (IOException ignore) {
				System.err.println(ignore);
			}
	}

	public static void closeSafe(OutputStream os) {
		if (os != null)
			try {
				os.close();
			} catch (IOException ignore) {
				System.err.println(ignore);
			}
	}

	public static void printRequestInfo(HttpServletRequest req) {
		if (!DEBUG)
			return;
		System.out
				.println("--------------------------------------------------------------------");
		System.out.println("QueryString : " + req.getQueryString());
		System.out.println("PathInfo : " + req.getPathInfo());
		System.out.println("Method : " + req.getMethod());
		System.out.println("ContentType : " + req.getContentType());
		String headers = "";
		Enumeration<String> headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String hdrName = (String) headerNames.nextElement();
			headers += hdrName + " : " + req.getHeader(hdrName) + "\n";
		}
		System.out.println(headers);
		System.out
				.println("--------------------------------------------------------------------");

	}

	public static byte[] getByteData(InputStream in) throws IOException {
		if (in != null) {
			int readSize = 1024;
			int bytesRead = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[readSize];
			while ((bytesRead = in.read(b)) != -1) {
				out.write(b, 0, bytesRead);
			}
			return out.toByteArray();
		}
		throw new IOException("Inputstream is null.");
	}

}
