package com.letus.tokenauth;

import com.google.gson.Gson;
import com.letus.user.UserInfo;
import org.json.JSONObject;

import java.io.*;
import java.security.GeneralSecurityException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "authTokenServlet", value = "/auth")
public class AuthServlet extends HttpServlet {

    @Override
    public void init() {

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter out = response.getWriter();
            BufferedReader reqReader = request.getReader();
            String idToken = readRequestBody(reqReader).getString("idToken");
            UserInfo userInfo = Auth.getInfo(idToken);
            String userInfoString = new Gson().toJson(userInfo);
            out.print(userInfoString);
            out.flush();
        } catch (IOException | GeneralSecurityException e) {
            System.out.println(e);
        }
    }

    private JSONObject readRequestBody(BufferedReader buffReader) {
        StringBuilder stringBuff = new StringBuilder();
        String line;
        try {
            while ((line = buffReader.readLine()) != null) {
                stringBuff.append(line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new JSONObject(stringBuff.toString());
    }

    @Override
    public void destroy() {
    }
}