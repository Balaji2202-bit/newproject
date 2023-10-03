package com.myapp.jdbc;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;
import java.io.IOException;
import java.util.List;

// get student from db
//add students to the request
//sent to JSP page
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet
{
//    private static final long seralVersion=1L;
    private StudentDataBaseUtil studentDataBaseUtil;
//    @Resource(name="jdbc/web_student_tracker")
    private BasicDataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        //create our studentDbUtil...pass in the conn pool
        try
        {
            dataSource=new BasicDataSource();
            dataSource.setUrl("jdbc:mysql://localhost:3306/web_student_tracker");
            dataSource.setUsername("root");
            dataSource.setPassword("Password@1");
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            studentDataBaseUtil =new StudentDataBaseUtil(dataSource);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        // List the students in the MVC
        try {
            // read the command parameter
            String theCOmmand=req.getParameter("command");

            //if command is missing , then default  to listing the students
            if(theCOmmand==null)
            {
                 theCOmmand="LIST";
            }

            //route the appropriate method
            switch (theCOmmand)
            {
                case "LIST":
                    listStudents(req,resp);
                    break;
                case "ADD":
                    addStudent(req,resp);
                    break;
                case "LOAD":
                    loadStudent(req,resp);
                    break;
                case "UPDATE":
                        updateStudent(req,resp);
                case "DELETE":
                    deleteStudent(req,resp);
                    break;
                case "SEARCH":
                    searchStudents(req, resp);
                    break;
                default:
                    listStudents(req,resp);
            }
//            listStudents(req,resp);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }

    }

    private void searchStudents(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        // read search name from form data
        String theSearchName = req.getParameter("theSearchName");

        // search students from db util
        List<Student> students = studentDataBaseUtil.searchStudents(theSearchName);

        // add students to the request
        req.setAttribute("STUDENT_LIST", students);

        // send to JSP page (view)
        RequestDispatcher dispatcher = req.getRequestDispatcher("/list-students.jsp");
        dispatcher.forward(req, resp);
    }

    private void deleteStudent(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        // read student id from data
        String theStudentId=req.getParameter("studentId");
        //delete student data from database
        studentDataBaseUtil.deleteStudent(theStudentId);
        // send them back to the list
        listStudents(req,resp);
    }

    private void updateStudent(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        // read student info from list
        int id=Integer.parseInt(req.getParameter("studentId"));
        String firstName=req.getParameter("firstName");
        String lastName=req.getParameter("lastName");
        String email=req.getParameter("email");

        //create a new student object
        Student theStudent= new Student(id,firstName,lastName,email);

        // perform update on database
        studentDataBaseUtil.updateStudent(theStudent);

        // send them back to the database list page
        listStudents(req,resp);
    }

    private void addStudent(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // read student info from list
        String firstName=req.getParameter("firstName");
        String lastName=req.getParameter("lastName");
        String email=req.getParameter("email");

        //create a new student object
        Student theStudent=new Student(firstName,lastName,email);
        //add the student to the database
        studentDataBaseUtil.addStudent(theStudent);
        //send to the main page
        listStudents(req,resp);
    }

    private void loadStudent(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // read student id from form data
        String theStudentId = request.getParameter("studentId");

        // get student from database (db util)
        Student theStudent = studentDataBaseUtil.getStudent(theStudentId);

        // place student in the request attribute
        request.setAttribute("THE_STUDENT", theStudent);

        // send to jsp page: update-student-form.jsp
        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/update-student-form.jsp");
        dispatcher.forward(request, response);
    }


    private void listStudents(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // get student from db
        List<Student> students= studentDataBaseUtil.getStudents();

        //add students to the request
         req.setAttribute("STUDENT_LIST",students);

        //sent to JSP page
        RequestDispatcher requestDispatcher=req.getRequestDispatcher("/list_student.jsp");
        requestDispatcher.forward(req,resp);
    }
}
