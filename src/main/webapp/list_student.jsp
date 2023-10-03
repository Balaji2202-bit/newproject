<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<title>Student Tracker Application</title>
<link type="text/css" rel="stylesheet" href="css/style.css">

<body>
<div id="wrapper">
    <div id="header">
        <h2>Anna University </h2>
    </div>
</div>
<div id="container">
    <div id="content">
<!--        put new button -->
        <input type="button" value="Add Student"
               onclick="window.location.href='add-student-form.jsp'; return false; "
        class="add-student-button">
        <!--  add a search box -->
        <form action="StudentControllerServlet" method="GET">

            <input type="hidden" name="command" value="SEARCH" />

            Search student: <input type="text" name="theSearchName" />

            <input type="submit" value="Search" class="add-student-button" />

        </form>
        <table>
            <tr>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Email   </th>
                <th> Action</th>
            </tr>
            <c:forEach var="tempStudent" items="${STUDENT_LIST}">
<!--                set up the link for each student-->
                <c:url var="tempLink" value="StudentControllerServlet">
                    <c:param name="command" value="LOAD"/>
                    <c:param name="studentId" value="=${tempStudent.id}"/>
                </c:url>
                <!--                set up the link for each student-->
                <c:url var="deleteLink" value="StudentControllerServlet">
                    <c:param name="command" value="DELETE"/>
                    <c:param name="studentId" value="=${tempStudent.id}"/>
                </c:url>
                <tr>
                <td>${tempStudent.firstName} </td>
                <td>${tempStudent.lastName} </td>
                <td>${tempStudent.email} </td>
                    <td> <a href="${tempLink}" > Update </a>
                    | <a href="${tempLink}"  onclick="if(!(confirm('Are You sure want to delete this student in your List?'))) return false"> Delete </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>
</body>
</html>