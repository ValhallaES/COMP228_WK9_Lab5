package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
	private final static String URL = "jdbc:mysql://localhost:3306/hospital_db";
	private final static String USER = "root";
	private final static String PASSWORD = "123456";

	public void insert(Patient p) throws SQLException {
		String sql = "INSERT INTO patients (name, age, gender, phone, diagnosis, admission_date) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, p.getName());
			ps.setInt(2, p.getAge());
			ps.setString(3, p.getGender());
			ps.setString(4, p.getPhone());
			ps.setString(5, p.getDiagnosis());
			ps.setDate(6, Date.valueOf(p.getAdmissionDate()));
			ps.executeUpdate();
		}
	}

	public void update(Patient p) throws SQLException {
		String sql = "UPDATE patients SET name = ?, age = ?, gender = ?, phone = ?, diagnosis = ?, admission_date = ? WHERE patient_id = ?";
		try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, p.getName());
			ps.setInt(2, p.getAge());
			ps.setString(3, p.getGender());
			ps.setString(4, p.getPhone());
			ps.setString(5, p.getDiagnosis());
			ps.setDate(6, Date.valueOf(p.getAdmissionDate()));
			ps.setInt(7, p.getId());
			ps.executeUpdate();
		}
	}

	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM patients WHERE patient_id = ?";
		try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<Patient> find(String keyword) throws SQLException {
		String sql = "SELECT patient_id, name, age, gender, phone, diagnosis, admission_date FROM patients WHERE name LIKE ? OR phone LIKE ? ORDER BY patient_id";
		List<Patient> patients = new ArrayList<>();
		try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, "%" + keyword + "%");
			ps.setString(2, "%" + keyword + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					patients.add(mapPatient(rs));
				}
			}
		}
		return patients;
	}

	private Patient mapPatient(ResultSet rs) throws SQLException {
		return new Patient(
				rs.getInt("patient_id"),
				rs.getString("name"),
				rs.getInt("age"),
				rs.getString("gender"),
				rs.getString("phone"),
				rs.getString("diagnosis"),
				rs.getDate("admission_date").toLocalDate());
	}
}