package cz.cuni.mff.ufal.textan.server.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author Petr Fanta
 */
public class TextanWelcomePage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();

        writer.println("<!DOCTYPE html>");
        writer.println("<html>\n\t<head>");
        writer.println("<title>TextAn - welcome page</title>");
        writer.println("\t</head>\n\t<body>");
        writer.println("<h1>TextAn</h1>");
        writer.println("<p>Welcome to TextAn Server!  More info about TextAn is available on <a href=\"https://github.com/PreXident/TextAn\">GitHub</a>.</p>");
        writer.println("<p>Web Services are available <a href=\"/soap\">here</a>.</p>");
        writer.println("\t</body>\n</html>");
    }
}
