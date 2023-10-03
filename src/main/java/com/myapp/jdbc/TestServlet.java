package com.myapp.jdbc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/com.myapp.jdbc.TestServlet")
public class TestServlet  extends HttpServlet {
    private static final long serialVersionID=1L;
    private BasicDataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
       // super.doGet(req, resp);
        // Define datasource connection pool for resource injection
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/web_student_tracker");
        dataSource.setUsername("root");
        dataSource.setPassword("Password@1");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // step 1: setup the printwriter
        PrintWriter out=resp.getWriter();
        resp.setContentType("text/plain");
        //step 2: Get a connection to the database
        Connection myCon=null;
        Statement myStmt=null;
        ResultSet myRs=null;
        try {
            myCon =dataSource.getConnection();

            // step 3: create a sql statement
            String sql="select*from student";
            myStmt = myCon.createStatement();
            // step 4: Execute Sql query
            myRs=myStmt.executeQuery(sql);
            // step 5:process the result set
            while (myRs.next())
            {
                String email=myRs.getString("email");
                out.println(email);
            }
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
    }
}
