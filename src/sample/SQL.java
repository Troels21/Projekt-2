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

    public static void main(String[] args) throws SQLException {
        SQL sql = new SQL();

        String CPR = "2222222222";
        createNewPatient(CPR);
        //writePatientListe(CPR);
        //writeToPatientMaalingPuls(CPR, 66,  37.5, 98.5);
        //writeToPatientMaalingEKG(CPR,100);

        Read_data_Puls("patientMaalingPuls", CPR);
        System.out.println();
        //Read_data_EKG("patientMaalingEKG",CPR);
        System.out.println();
        Read_data_logininfo("logininfo", "");
        System.out.println();
        Read_data_patientliste("patientListe", "");
    }

    //metode til der samler oprettelse af tabeller og opdatering af patientliste.
    public static void createNewPatient(String CPR){
        writePatientListe(CPR);
        createTableCPREKG(CPR);
        createTableCPRPuls(CPR);
    }


    // Write metoden, som skriver CPR ind i patientlisten
    public static void writePatientListe(String CPR){
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
    static public void createTableCPRPuls(String CPR) {
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
    static public void createTableCPREKG(String CPR) {
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
    public static void writeToPatientMaalingPuls(String CPR, double Puls, double Temp, double SpO2) {
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
    public static void writeToPatientMaalingEKG(String CPR, double EKG) {
        try {
            String write_to_database2 = "insert into patientMaalingEKG" + CPR + "(EKGValue) values(?)";
            PreparedStatement PP2 = myConn.prepareStatement(write_to_database2);

            PP2.setDouble(1, EKG);

            PP2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Read metode som læser data fra ekg tabel
    static public void readDataEKG(String CPR, int[] tid_array, double[] ekg_array) throws SQLException {
        String sql_SelectFrom = "SELECT * FROM login.patientMaalingEKG" + CPR;
        ResultSet rs = myStatement.executeQuery(sql_SelectFrom);
        int i = 0;
        while (rs.next()) {
            //System.out.print(rs.getInt("timeaxis"));
            //System.out.println(rs.getDouble("EKGValue"));
            tid_array[i] = rs.getInt("timeaxis");
            ekg_array[i] = rs.getDouble("EKGValue");

            i++;
        }
    }

    //row tæller
    static public void rowCounter(){
        String sql_SelectFrom = "SELECT * FROM login." + Table + CPR;
        ResultSet rs = myStatement.executeQuery(sql_SelectFrom);


    }


    // read metode som læser data fra Puls tabel
    static public void Read_data_Puls(String Table, String CPR) throws SQLException {
        String sql_SelectFrom = "SELECT * FROM login." + Table + CPR;
        ResultSet rs = myStatement.executeQuery(sql_SelectFrom);

        while (rs.next()) {
            System.out.print(rs.getDouble("timeaxis"));
            System.out.println(rs.getDouble("PulsValue"));
            System.out.println(rs.getDouble("TEMPValue"));
            System.out.println(rs.getDouble("SpO2Value"));
        }
    }

    // read metode som læser data fra logininfo
    static public void Read_data_logininfo(String Table, String CPR) throws SQLException {
        String sql_SelectFrom = "SELECT * FROM login." + Table + CPR;
        ResultSet rs = myStatement.executeQuery(sql_SelectFrom);

        while (rs.next()) {
            System.out.print(rs.getInt("id")+" ");
            System.out.print(rs.getString("username")+" ");
            System.out.print(rs.getString("password")+" ");
            System.out.println(rs.getInt("doctor"));
        }
    }

    // read metode som læser data fra patient liste
    static public void Read_data_patientliste(String Table, String CPR) throws SQLException {
        String sql_SelectFrom = "SELECT * FROM login." + Table + CPR;
        ResultSet rs = myStatement.executeQuery(sql_SelectFrom);

        while (rs.next()) {
            System.out.print(rs.getString("CPR")+" ");
        }
    }

}
