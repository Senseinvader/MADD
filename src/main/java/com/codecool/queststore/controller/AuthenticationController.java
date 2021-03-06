package com.codecool.queststore.controller;

import com.codecool.queststore.DAO.AuthenticationDAO;
import com.codecool.queststore.DAO.UserDAO;
import com.codecool.queststore.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationController implements HttpHandler {

    private Map<String, String> formData;
    private AuthenticationDAO authDAO;
    private Integer userId;


    public AuthenticationController()  {
        authDAO = new AuthenticationDAO();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String response = "";


        if (method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "UTF8");
            BufferedReader br = new BufferedReader(isr);
            String inputs = br.readLine();

            parseInputs(inputs);
            userId =  authDAO.getUserIdByInputs(formData);

            User user = new UserDAO(this).getUserById();
            System.out.println("role: " + user.getRole());

            switch (user.getRole()) {
                case "admin":
                    System.out.println("were in admin");
                    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/mentormain.twig");
                    JtwigModel model = JtwigModel.newModel();
                    response = template.render(model);
                    System.out.println("were in admin");
                    httpRedirectTo("/admin/mentors", httpExchange);
                    httpExchange.sendResponseHeaders(200, response.length());
                    break;
                case "mentor":
                    template = JtwigTemplate.classpathTemplate("/templates/mentorstudents.twig");
                    model = JtwigModel.newModel();
                    response = template.render(model);
                    httpRedirectTo("/mentors", httpExchange);
                    httpExchange.sendResponseHeaders(200, response.length());
                    break;
                case "student":
                    template = JtwigTemplate.classpathTemplate("templates/student/codecooler.twig");
                    model = JtwigModel.newModel();
                    response = template.render(model);
                    httpRedirectTo("/codecooler", httpExchange);
                    httpExchange.sendResponseHeaders(200, response.length());
                    break;
            }
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        if (method.equals("GET")) {
            JtwigTemplate template = JtwigTemplate.classpathTemplate("/templates/login.twig");
            JtwigModel model = JtwigModel.newModel();
            response = template.render(model);
            httpExchange.sendResponseHeaders(200, response.length());
        }
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void parseInputs(String inputs) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String [] pairs = inputs.split("&");
        for (String element : pairs) {
            String [] keyValue = element.split("=");
            String value = URLDecoder.decode(keyValue[1], "UTF8");
            map.put(keyValue[0], value);
        }
        formData = map;
    }

    public Map<String, String> getInputs() {
        System.out.println(formData + " map");
        return formData;
    }

    public Integer getUserId() {
        return userId;
    }

    private void httpRedirectTo(String dest, HttpExchange httpExchange) throws IOException {
        String hostPort = httpExchange.getRequestHeaders().get("host").get(0);
        httpExchange.getResponseHeaders().set("Location", "http://" + hostPort + dest);
        httpExchange.sendResponseHeaders(301, -1);
    }
}

