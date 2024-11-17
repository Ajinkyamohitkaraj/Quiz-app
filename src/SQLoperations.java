import java.sql.*;

public class SQLoperations {
    
    private Connection con;

    // Constructor to establish connection
    public SQLoperations() throws SQLException {
        String url = "jdbc:mysql://localhost:3307/Ajinkya";
        String usr = "root";
        String pass = "123456789"; // Ideally load from env variables
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, usr, pass);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    // Method to insert a new user securely
    public void newUser(String name, String uname, String pass) throws SQLException {
        String query = "INSERT INTO actors (fname, uname, pass) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, uname);
            pstmt.setString(3, pass);
            pstmt.executeUpdate();
        }
    }

    // Method to authenticate user
    public int authUser(String uname, String pass) throws SQLException {
        String query = "SELECT id, pass FROM actors WHERE uname = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, uname);
            try (ResultSet rst = pstmt.executeQuery()) {
                if (!rst.next()) return -1; // User not found
                return rst.getString("pass").equals(pass) ? rst.getInt("id") : 0;
            }
        }
    }

    // Method to add a new question securely
    public void newQuestion(String code, String question, String op1, String op2, String op3, String op4) throws SQLException {
        String query = "INSERT INTO questions (quizcode, question, op1, op2, op3, op4) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.setString(2, question);
            pstmt.setString(3, op1);
            pstmt.setString(4, op2);
            pstmt.setString(5, op3);
            pstmt.setString(6, op4);
            pstmt.executeUpdate();
        }
    }

    // Method to add a user-question mapping
    public void userQuestionAdd(int id, String quizcode) throws SQLException {
        String query = "INSERT INTO userQuestions (id, quizcode, total) VALUES (?, ?, 0)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, quizcode);
            pstmt.executeUpdate();
        }
    }

    // Method to update answer choice
    public void answerUpdt(String quizcode, int qno, int option) throws SQLException {
        String query = "INSERT INTO quizquestions (quizcode, qno, opno) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, quizcode);
            pstmt.setInt(2, qno);
            pstmt.setInt(3, option);
            pstmt.executeUpdate();
        }
    }

    // Method to retrieve questions
    public ResultSet getQuestions(String quizcode) throws SQLException {
        String query = "SELECT * FROM questions WHERE quizcode = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, quizcode);
        return pstmt.executeQuery(); // User should close ResultSet and Statement
    }

    // Method to search surveys
    public ResultSet surveys(int id, String search) throws SQLException {
        String query = "SELECT * FROM userQuestions WHERE id = ? AND quizcode LIKE ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, id);
        pstmt.setString(2, "%" + search + "%");
        return pstmt.executeQuery();
    }

    // Method to update total count
    public void addTotal() throws SQLException {
        String query = "UPDATE userQuestions SET total = total + 1";
        try (Statement stm = con.createStatement()) {
            stm.executeUpdate(query);
        }
    }

    // Method to check if a quiz exists
    public boolean check(String search) throws SQLException {
        String query = "SELECT 1 FROM userQuestions WHERE quizcode = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, search);
            try (ResultSet rst = pstmt.executeQuery()) {
                return rst.next();
            }
        }
    }

    // Method to remove a survey
    public void removeSurvey(String quizcode) throws SQLException {
        try (Statement stm = con.createStatement()) {
            stm.executeUpdate("DELETE FROM questions WHERE quizcode = '" + quizcode + "'");
            stm.executeUpdate("DELETE FROM quizquestions WHERE quizcode = '" + quizcode + "'");
            stm.executeUpdate("DELETE FROM userQuestions WHERE quizcode = '" + quizcode + "'");
        }
    }

    // Method to get count of responses
    public int getCount(String quizcode, int qno, int op) throws SQLException {
        String query = "SELECT COUNT(opno) AS count FROM quizquestions WHERE quizcode = ? AND qno = ? AND opno = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, quizcode);
            pstmt.setInt(2, qno + 1);
            pstmt.setInt(3, op);
            try (ResultSet rst = pstmt.executeQuery()) {
                return rst.next() ? rst.getInt("count") : 0;
            }
        }
    }
}
