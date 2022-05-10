package com.letus.websocket;

import com.letus.tokenauth.Auth;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "authTokenServlet", value = "/auth")
public class AuthServlet extends HttpServlet {
    private String message;
    private Auth auth;

    public void init() {
        auth = new Auth();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}