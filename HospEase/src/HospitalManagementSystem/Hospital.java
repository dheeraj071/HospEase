package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Hospital {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username  = "root";
    private static final String password  = "Oneplus123!@#";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MNGT SYS");
                System.out.println("1. ADD PATEINT");
                System.out.println("2. VIEW PATIENTS");
                System.out.println("3. VIEW DOCTORS");
                System.out.println("4. BOOK APPOINTMENT");
                System.out.println("5. EXIT");

                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        //add patients
                        patient.addPateint();
                        System.out.println();
                    case 2:
                        //view patients
                        patient.viewPatients();
                        System.out.println();
                    case 3:
                        //View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                    case 4:
                        //book appointments
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                    case 5:
                        return;
                    default:
                        System.out.println("Please enter valid choice of action.");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.println("Enter patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter aappointment date (yyyy-mm-dd): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailibility(doctorId, appointmentDate, connection)){
                    String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?,?,?)";
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                        preparedStatement.setInt(1,patientId);
                        preparedStatement.setInt(2,doctorId);
                        preparedStatement.setString(1,appointmentDate);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected>0){
                            System.out.println("Appointment Booked!");
                        }else{
                            System.out.println("Failed to book Appointment!");
                        }
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
            }else{
                System.out.println("Doctor not available on selected date");
            }
        }else{
            System.out.println("Dosent exist.");
        }
    }
    public static boolean checkDoctorAvailibility(int doctorId, String appointmentDate, Connection connection){
        String query =  "SELECT COUNT (*) FROM appointments where doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count==0){
                    return true;
                }else{
                    return false;
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
