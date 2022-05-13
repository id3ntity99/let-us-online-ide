package com.letus;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(name = "startUpServlet", value = "/startup", loadOnStartup = 1)
public class StartUpServlet extends HttpServlet {
    @Override
    public void init() {
        System.out.println("Initializing while starting up");
    }
}
