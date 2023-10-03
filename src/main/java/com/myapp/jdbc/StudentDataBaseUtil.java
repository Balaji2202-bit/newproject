package com.myapp.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//import static jdk.internal.net.http.common.Utils.close;

public class StudentDataBaseUtil {
    private BasicDataSource basicDataSource;

    public StudentDataBaseUtil(BasicDataSource theDataSource) {
        basicDataSource = theDataSource;
    }

    public List<Student> getStudents() throws Exception {
        List<Student> students = new ArrayList<>();
        Connection myCon = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        //step 2: Get a connection to the database
        try {
            myCon = basicDataSource.getConnection();

            // step 3: create a sql statement
            String sql = "select*from student order by last_name";
            myStmt = myCon.createStatement();

            // step 4: Execute Sql query
            myRs = myStmt.executeQuery(sql);

            // step 5:process the result set
            while (myRs.next()) {
                //Database column name
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                //create new student object
                Student tempStudent = new Student(id, firstName, lastName, email);

                // add it to the list of students
                students.add(tempStudent);
            }
            return students;
        } finally {
            // close JDBC objects
            close(myCon, myStmt, myRs);
        }
    }

    private void close(Connection myCon, Statement myStmt, ResultSet myRs) {
        try {
            if (myRs != null) {
                myRs.close();
            }
            if (myCon != null) {
                myCon.close();
            }
            //doesn't really close .....put back to the connection pool
            if (myStmt != null) {
                myStmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStudent(Student theStudent) throws SQLException {
        Connection myConn = null;
        PreparedStatement myStmt = null;
        try {
            //get db connection
            myConn = basicDataSource.getConnection();
            // create sql for insert
//            String sql="insert into student (first_name, last_name, email) values (?, ?, ?)";
            String sql = "insert into student "
                    + "(first_name, last_name, email) "
                    + "values (?, ?, ?) ";
            myStmt = myConn.prepareStatement(sql);

            //set the param values for the student
            myStmt.setString(1, theStudent.getFirstName());
            myStmt.setString(2, theStudent.getLastName());
            myStmt.setString(3, theStudent.getEmail());

            //execute sql insert
            myStmt.execute();
        } finally {
            close(myConn, myStmt, null);
        }
    }

    public Student getStudent(String theStudentId) throws Exception {

        Student theStudent = null;

        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        int studentId;
        theStudentId = theStudentId.replace("=", "");
        try {
            // convert student id to int
            studentId = Integer.parseInt(theStudentId);

            // get connection to database
            myConn = basicDataSource.getConnection();

            // create sql to get selected student
            String sql = "select * from student where id=?";

            // create prepared statement
            myStmt = myConn.prepareStatement(sql);

            // set params
            myStmt.setInt(1, studentId);

            // execute statement
            myRs = myStmt.executeQuery();

            // retrieve data from result set row
            if (myRs.next()) {
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                // use the studentId during construction
                theStudent = new Student(studentId, firstName, lastName, email);
            } else {
                throw new Exception("Could not find student id: " + studentId);
            }

            return theStudent;
        } finally {
            // clean up JDBC objects
            close(myConn, myStmt, myRs);
        }
    }

    public void updateStudent(Student theStudent) throws Exception {
        Connection myConn = null;
        PreparedStatement myStmt = null;
        try {

            // get a db connection
            myConn = basicDataSource.getConnection();
            //create a sql update statement
            String sql = "update student " + "set first_name=?, last_name=?, email=? " + "where id=? ";
            //prepare statement
            myStmt = myConn.prepareStatement(sql);
            //set params
            myStmt.setString(1, theStudent.getFirstName());
            myStmt.setString(2, theStudent.getLastName());
            myStmt.setString(3, theStudent.getEmail());
            myStmt.setInt(4, theStudent.getId());
            //execute sql statement
            myStmt.execute();
        } finally {
            close(myConn, myStmt, null);
        }
    }

    public void deleteStudent(String theStudentId) throws Exception {
        Connection myConn = null;
        PreparedStatement myStmt = null;
        try {
            // convert student id into int
            int studentId = Integer.parseInt(theStudentId);
            //get connection database
            myConn = basicDataSource.getConnection();
            // create sql to delete the student data
            String sql = "delete from student where id=? ";
            //prepare statment
            myStmt = myConn.prepareStatement(sql);
            //set params
            myStmt.setInt(1, studentId);
            //execute sql statement
            myStmt.execute();
        } finally {
            // cleanup my JDBC code
            close(myConn, myStmt, null);
        }
    }

    public List<Student> searchStudents(String theSearchName) throws Exception
    {
        List<Student> students = new ArrayList<>();

        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        int studentId;

        try {

            // get connection to database
            myConn = basicDataSource.getConnection();

            //
            // only search by name if theSearchName is not empty
            //
            if (theSearchName != null && theSearchName.trim().length() > 0) {
                // create sql to search for students by name
                String sql = "select * from student where lower(first_name) like ? or lower(last_name) like ?";
                // create prepared statement
                myStmt = myConn.prepareStatement(sql);
                // set params
                String theSearchNameLike = "%" + theSearchName.toLowerCase() + "%";
                myStmt.setString(1, theSearchNameLike);
                myStmt.setString(2, theSearchNameLike);

            } else {
                // create sql to get all students
                String sql = "select * from student order by last_name";
                // create prepared statement
                myStmt = myConn.prepareStatement(sql);
            }

            // execute statement
            myRs = myStmt.executeQuery();

            // retrieve data from result set row
            while (myRs.next()) {

                // retrieve data from result set row
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                // create new student object
                Student tempStudent = new Student(id, firstName, lastName, email);

                // add it to the list of students
                students.add(tempStudent);
            }

            return students;
        } finally {
            // clean up JDBC objects
            close(myConn, myStmt, myRs);
        }
    }

}