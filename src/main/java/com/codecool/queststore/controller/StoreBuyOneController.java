package com.codecool.queststore.controller;

import com.codecool.queststore.ConnectionProvider;
import com.codecool.queststore.DAO.StoreDAO;
import com.codecool.queststore.DAO.StudentDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

public class StoreBuyOneController implements HttpHandler {

    private AuthenticationController authenticationController;
    private StudentDAO studentDAO;
    private Connection connection = new ConnectionProvider().getConnection();

    public StoreBuyOneController(AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;
        this.studentDAO = new StudentDAO();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        String response = "";
        System.out.println("HERE storebyone");


        if (method.equals("GET")) {

            int userID = getUserID();
            int studentID = getStudentID(userID);

            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/store-buy-one.twig");
            JtwigModel model = JtwigModel.newModel();

            response = template.render(model);
        }
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }
    private int getUserID(){
        int userID = this.authenticationController.getUserId();
        return userID;

    }
    public int getStudentID(int user){
        int studentID = this.studentDAO.getStudentIDToStudentController(user);
        return studentID;
    }

}
