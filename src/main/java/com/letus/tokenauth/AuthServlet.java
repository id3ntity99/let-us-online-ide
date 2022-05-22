package com.letus.tokenauth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
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
            if(Auth.verifyIdToken(idToken)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.append("success", true);
                out.print(jsonObject);
                out.flush();
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.append("success", "false");
                out.print(jsonObject);
                out.flush();
            }
        } catch (IOException | JSONException e) {
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