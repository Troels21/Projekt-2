package sample;

import java.sql.*;

public class SQL {

    static String url = "jdbc:mysql://localhost:3306/login";
    static String user = "root";
    static String password = "";
    static Connection myConn;
    static Statement myStatement;

    // konstruktør der opretter forbindelse til databsen
    public SQL() {
        try {
            myConn = DriverManager.getConnection(url, user, password);
            myStatement = myConn.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*public static void main(String[] args) throws SQLException {
        SQL s = new SQL();

    }*/

    //metode til der samler oprettelse af tabeller og opdatering af patientliste.
    public void createNewPatient(String CPR) {
        writePatientListe(CPR);
        createTableCPREKG(CPR);
        createTableCPRPuls(CPR);
    }


    // Write metoden, som skriver CPR ind i patientlisten
    public void writePatientListe(String CPR) {
        String write_to_database1 = "INSERT INTO patientListe " + "(CPR) values(?);";
        PreparedStatement PP1;
        try {
            PP1 = myConn.prepareStatement(write_to_database1);
            PP1.setString(1, CPR);
            PP1.execute();
        } catch (SQLException throwables) {
            System.out.println("CPR eksisterer allerede i systemet.");
        }


    }

    // create metode som laver en tabel som indeholder tid, puls, temp og spo2.
    public void createTableCPRPuls(String CPR) {
        String sql_CreateTable = "CREATE TABLE IF NOT EXISTS patientMaalingPuls" + CPR + "(\n"
                + "timeaxis INT PRIMARY KEY AUTO_INCREMENT,\n"
                + "PulsValue DOUBLE,\n"
                + "TEMPValue DOUBLE,\n"
                + "SpO2Value DOUBLE);";
        try {
            myStatement.execute(sql_CreateTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // create metode som laver en tabel som indeholder tid og ekg.
   public void createTableCPREKG(String CPR) {
        String sql_CreateTable = "CREATE TABLE IF NOT EXISTS patientMaalingEKG" + CPR + "(\n"
                + "timeaxis INT PRIMARY KEY AUTO_INCREMENT,\n"
                + "EKGValue DOUBLE);";
        try {

            myStatement.execute(sql_CreateTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // write metode som skriver puls temp og spo2 ind i en tabel, som den identificere med CPR.
    public void writeToPatientMaalingPuls(String CPR, double Puls, double Temp, double SpO2) {
        try {
            String write_to_database2 = "insert into patientMaalingPuls" + CPR + "(PulsValue, TEMPValue,SpO2Value) values(?, ?, ?)";
            PreparedStatement PP2 = myConn.prepareStatement(write_to_database2);

            PP2.setDouble(1, Puls);
            PP2.setDouble(2, Temp);
            PP2.setDouble(3, SpO2);

            PP2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //write metode som skriver ekg ind i en tabel, som den identificere med EKG.
    public void writeToPatientMaalingEKG(String CPR, double EKG) {
        try {
            String write_to_database2 = "insert into patientMaalingEKG" + CPR + "(EKGValue) values(?)";
            PreparedStatement PP2 = myConn.prepareStatement(write_to_database2);

            PP2.setDouble(1, EKG);

            PP2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //row tæller
    public int rowCounter(String Table, String CPR) {
        String sql_Count = "SELECT COUNT(*) FROM " + Table + CPR;
        ResultSet rs;
        try {
            rs = myStatement.executeQuery(sql_Count);
            rs.next();
            return rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    // Read metode som læser data fra ekg tabel
    public void readDataEKG(String CPR, int[] tid_array, double[] ekg_array) throws SQLException {
        String sql_SelectFrom = "SELECT * FROM login.patientMaalingEKG" + CPR;
        ResultSet rs = myStatement.executeQuery(sql_SelectFrom);
        int i = 0;
        while (rs.next()) {
            tid_array[i] = rs.getInt("timeaxis");
            ekg_array[i] = rs.getDouble("EKGValue");
            i++;
        }
    }


    // read metode som læser data fra Puls tabel
    public void readDataPuls(String CPR, int[] tid_array, double[] puls_array, double[] temp_array, double[] SpO2_array) throws SQLException {
        String sql_SelectFrom = "SELECT * FROM login.patientMaalingPuls" + CPR;
        ResultSet rs = myStatement.executeQuery(sql_SelectFrom);
        int i = 0;
        while (rs.next()) {
            tid_array[i] = rs.getInt("timeaxis");
            puls_array[i] = rs.getDouble("PulsValue");
            temp_array[i] = rs.getDouble("TEMPValue");
            SpO2_array[i] = rs.getDouble("SpO2Value");
            i++;
        }
    }

    // read metode som læser data fra logininfo
    public String Read_data_logininfo(String username){
        try {
            String sql_SelectFrom = "SELECT *\n" +
                    "From login.logininfo\n" +
                    "WHERE username ='" + username + "' ;";
            ResultSet rs = myStatement.executeQuery(sql_SelectFrom);
            rs.next();
            return rs.getString(2) + "," + rs.getString(3) + "," + rs.getString(4);
        } catch (SQLException throwables) {
            return "null";
        }
    }


    // boolsk kontrol af om cpr eksistere i databse
    public boolean doesPatientExsist(String CPR) {
        String findPatient = "SELECT CPR\n"
                + " FROM login.patientListe\n"
                + "WHERE EXISTS (SELECT CPR FROM patientliste WHERE CPR = " + CPR + ");";
        ResultSet rs;
        try {
            rs = myStatement.executeQuery(findPatient);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException throwables) {
            return false;
        }

    }
}
