package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
	private final static String URL = "jdbc:mysql://localhost:3306/hospital_db";
	private final static String USER = "root";
	private final static String PASSWORD = "123456";

	static class Patient {
		private int id;
		private String name;
		private int age;
		private String gender;
		private String phone;
		private String diagnosis;
		private LocalDate admissionDate;

		Patient(int id, String name, int age, String gender, String phone, String diagnosis,
				LocalDate admissionDate) {
			this.id = id;
			this.name = name;
			this.age = age;
			this.gender = gender;
			this.phone = phone;
			this.diagnosis = diagnosis;
			this.admissionDate = admissionDate;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}

		public String getGender() {
			return gender;
		}

		public String getPhone() {
			return phone;
		}

		public String getDiagnosis() {
			return diagnosis;
		}

		public LocalDate getAdmissionDate() {
			return admissionDate;
		}
	}

	private TextField idField = new TextField();
	private TextField nameField = new TextField();
	private TextField ageField = new TextField();
	private ComboBox<String> genderBox = new ComboBox<>();
	private TextField phoneField = new TextField();
	private TextField diagnosisField = new TextField();
	private DatePicker admissionPicker = new DatePicker();
	private TextField searchField = new TextField();
	private TableView<Patient> table = new TableView<>();
	private Label status = new Label("Ready.");

	@Override
	public void start(Stage stage) {
		Label title = new Label("Hospital Management System");
		title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
		Label subtitle = new Label("Patient Registration");
		subtitle.setStyle("-fx-text-fill: #dce9f5;");
		VBox header = new VBox(4, title, subtitle);
		header.setPadding(new Insets(15, 20, 15, 20));
		header.setStyle("-fx-background-color: #1f4e79;");

		idField.setEditable(false);
		idField.setPromptText("Auto-generated");
		genderBox.getItems().addAll("Female", "Male", "Non-binary", "Prefer not to say");
		genderBox.setPromptText("Select gender");

		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);
		form.addRow(0, new Label("Patient ID:"), idField);
		form.addRow(1, new Label("Full Name:"), nameField);
		form.addRow(2, new Label("Age:"), ageField);
		form.addRow(3, new Label("Gender:"), genderBox);
		form.addRow(4, new Label("Phone:"), phoneField);
		form.addRow(5, new Label("Diagnosis / Reason:"), diagnosisField);
		form.addRow(6, new Label("Admission Date:"), admissionPicker);

		Button addButton = new Button("Add Patient");
		Button updateButton = new Button("Update");
		Button deleteButton = new Button("Delete");
		Button clearButton = new Button("Clear");
		addButton.setOnAction(new AddHandlerClass());
		updateButton.setOnAction(new UpdateHandlerClass());
		deleteButton.setOnAction(new DeleteHandlerClass());
		clearButton.setOnAction(new ClearHandlerClass());

		HBox actionRow1 = new HBox(10, addButton, updateButton);
		HBox actionRow2 = new HBox(10, deleteButton, clearButton);

		Label formTitle = new Label("Patient Details");
		formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		VBox formBox = new VBox(12, formTitle, form, actionRow1, actionRow2);
		formBox.setPadding(new Insets(15));
		formBox.setStyle("-fx-border-color: #c8d3de; -fx-border-radius: 6;");

		searchField.setPromptText("Search by name or phone");
		Button searchButton = new Button("Search");
		Button showAllButton = new Button("Show All");
		searchButton.setOnAction(new SearchHandlerClass());
		showAllButton.setOnAction(new ShowAllHandlerClass());
		HBox searchBar = new HBox(10, searchField, searchButton, showAllButton);

		TableColumn<Patient, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
		TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
		TableColumn<Patient, String> ageCol = new TableColumn<>("Age");
		ageCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAge())));
		TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
		genderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender()));
		TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
		phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
		TableColumn<Patient, String> diagnosisCol = new TableColumn<>("Diagnosis");
		diagnosisCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDiagnosis()));
		TableColumn<Patient, String> dateCol = new TableColumn<>("Admission");
		dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionDate().toString()));

		table.getColumns().add(idCol);
		table.getColumns().add(nameCol);
		table.getColumns().add(ageCol);
		table.getColumns().add(genderCol);
		table.getColumns().add(phoneCol);
		table.getColumns().add(diagnosisCol);
		table.getColumns().add(dateCol);
		table.getSelectionModel().selectedItemProperty().addListener(new SelectionHandlerClass());

		Label recordsTitle = new Label("Patient Records");
		recordsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		VBox recordsBox = new VBox(12, recordsTitle, searchBar, table);
		recordsBox.setPadding(new Insets(15));
		recordsBox.setStyle("-fx-border-color: #c8d3de; -fx-border-radius: 6;");
		HBox.setHgrow(recordsBox, Priority.ALWAYS);

		HBox center = new HBox(20, formBox, recordsBox);
		center.setPadding(new Insets(20));

		HBox bottom = new HBox(status);
		bottom.setPadding(new Insets(10, 20, 10, 20));
		bottom.setStyle("-fx-background-color: #eef2f6;");

		BorderPane root = new BorderPane();
		root.setTop(header);
		root.setCenter(center);
		root.setBottom(bottom);

		loadPatients("");

		stage.setTitle("Hospital Management System");
		stage.setScene(new Scene(root, 1150, 560));
		stage.show();
	}

	class AddHandlerClass implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			Patient p = readForm(0);
			if (p == null) {
				return;
			}
			try {
				insert(p);
				loadPatients("");
				clearForm();
				showSuccess("Patient added: " + p.getName());
			} catch (SQLException ex) {
				showError(ex.getMessage());
			}
		}
	}

	class UpdateHandlerClass implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			Patient selected = table.getSelectionModel().getSelectedItem();
			if (selected == null) {
				showError("Select a patient in the table first.");
				return;
			}
			Patient p = readForm(selected.getId());
			if (p == null) {
				return;
			}
			try {
				update(p);
				loadPatients("");
				clearForm();
				showSuccess("Patient ID " + p.getId() + " updated.");
			} catch (SQLException ex) {
				showError(ex.getMessage());
			}
		}
	}

	class DeleteHandlerClass implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			Patient selected = table.getSelectionModel().getSelectedItem();
			if (selected == null) {
				showError("Select a patient in the table first.");
				return;
			}
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
					"Delete patient " + selected.getName() + " (ID " + selected.getId() + ")?",
					ButtonType.OK, ButtonType.CANCEL);
			confirm.setTitle("Delete Patient");
			confirm.setHeaderText("Confirm delete");
			if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
				return;
			}
			try {
				delete(selected.getId());
				loadPatients("");
				clearForm();
				showSuccess("Patient ID " + selected.getId() + " deleted.");
			} catch (SQLException ex) {
				showError(ex.getMessage());
			}
		}
	}

	class ClearHandlerClass implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			clearForm();
			status.setText("");
		}
	}

	class SearchHandlerClass implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			String keyword = searchField.getText().trim();
			if (keyword.isEmpty()) {
				showError("Enter a name or phone to search.");
				return;
			}
			loadPatients(keyword);
			showSuccess(table.getItems().size() + " record(s) found.");
		}
	}

	class ShowAllHandlerClass implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			searchField.clear();
			loadPatients("");
		}
	}

	class SelectionHandlerClass implements ChangeListener<Patient> {
		@Override
		public void changed(ObservableValue<? extends Patient> obs, Patient oldValue, Patient p) {
			if (p != null) {
				idField.setText(String.valueOf(p.getId()));
				nameField.setText(p.getName());
				ageField.setText(String.valueOf(p.getAge()));
				genderBox.setValue(p.getGender());
				phoneField.setText(p.getPhone());
				diagnosisField.setText(p.getDiagnosis());
				admissionPicker.setValue(p.getAdmissionDate());
			}
		}
	}

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

	private void loadPatients(String keyword) {
		String sql = "SELECT patient_id, name, age, gender, phone, diagnosis, admission_date FROM patients WHERE name LIKE ? OR phone LIKE ? ORDER BY patient_id";
		try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, "%" + keyword + "%");
			ps.setString(2, "%" + keyword + "%");
			try (ResultSet rs = ps.executeQuery()) {
				table.getItems().clear();
				while (rs.next()) {
					table.getItems().add(mapPatient(rs));
				}
			}
		} catch (SQLException ex) {
			showError(ex.getMessage());
		}
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

	private Patient readForm(int id) {
		String name = nameField.getText().trim();
		String ageText = ageField.getText().trim();
		String gender = genderBox.getValue();
		String phone = phoneField.getText().trim();
		String diagnosis = diagnosisField.getText().trim();
		LocalDate date = admissionPicker.getValue();

		if (name.isEmpty() || ageText.isEmpty() || gender == null || phone.isEmpty() || diagnosis.isEmpty()
				|| date == null) {
			showError("All fields are required.");
			return null;
		}
		if (!ageText.matches("\\d{1,3}") || Integer.parseInt(ageText) > 125) {
			showError("Age must be a whole number from 0 to 125.");
			return null;
		}
		if (!phone.matches("[0-9()+\\-\\s]{7,20}")) {
			showError("Phone must be 7-20 digits.");
			return null;
		}
		if (date.isAfter(LocalDate.now())) {
			showError("Admission date cannot be in the future.");
			return null;
		}
		return new Patient(id, name, Integer.parseInt(ageText), gender, phone, diagnosis, date);
	}

	private void clearForm() {
		idField.clear();
		nameField.clear();
		ageField.clear();
		genderBox.setValue(null);
		phoneField.clear();
		diagnosisField.clear();
		admissionPicker.setValue(null);
		table.getSelectionModel().clearSelection();
	}

	private void showSuccess(String message) {
		status.setStyle("-fx-text-fill: green;");
		status.setText(message);
	}

	private void showError(String message) {
		status.setStyle("-fx-text-fill: red;");
		status.setText("Error: " + message);
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		launch(args);
	}
}