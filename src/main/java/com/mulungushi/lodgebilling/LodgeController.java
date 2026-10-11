package com.mulungushi.lodgebilling;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class LodgeController {

    // Form controls
    @FXML private TextField guestNameField;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private TextField nightsField;
    @FXML private TextField extrasField;
    @FXML private TextField amountPaidField;
    @FXML private TextField notesField;
    @FXML private Label statusLabel;

    // Table
    @FXML private TableView<Bill> billsTable;
    @FXML private TableColumn<Bill, String> billIdCol;
    @FXML private TableColumn<Bill, String> guestCol;
    @FXML private TableColumn<Bill, String> roomCol;
    @FXML private TableColumn<Bill, Integer> nightsCol;
    @FXML private TableColumn<Bill, String> totalCol;
    @FXML private TableColumn<Bill, String> paidCol;
    @FXML private TableColumn<Bill, String> balanceCol;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    private int nextBillNumber = 1;

    // Room prices
    private static final double STANDARD_PRICE = 350.00;
    private static final double DELUXE_PRICE = 550.00;
    private static final double FAMILY_PRICE = 750.00;

    @FXML
    public void initialize() {
        // Populate room types
        roomTypeCombo.getItems().addAll("Standard", "Deluxe", "Family");
        roomTypeCombo.getSelectionModel().selectFirst();

        // Setup table columns
        billIdCol.setCellValueFactory(new PropertyValueFactory<>("billId"));
        guestCol.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        nightsCol.setCellValueFactory(new PropertyValueFactory<>("nights"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalFormatted"));
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paidFormatted"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balanceFormatted"));

        billsTable.setItems(bills);
    }

    @FXML
    private void handleCalculate() {
        try {
            String guest = guestNameField.getText().trim();
            String room = roomTypeCombo.getValue();
            int nights = Integer.parseInt(nightsField.getText().trim());
            double extras = Double.parseDouble(extrasField.getText().trim());
            double paid = Double.parseDouble(amountPaidField.getText().trim());

            if (guest.isEmpty()) {
                showAlert("Error", "Please enter guest name.");
                return;
            }
            if (nights <= 0) {
                showAlert("Error", "Nights must be greater than 0.");
                return;
            }

            double roomRate = getRoomRate(room);
            double roomCharge = roomRate * nights;

            // 5% discount if 5 or more nights
            if (nights >= 5) {
                roomCharge = roomCharge * 0.95;
            }

            double total = roomCharge + extras;
            double balance = total - paid;

            statusLabel.setText(String.format("Calculated → Total: K %.2f   |   Balance: K %.2f", total, balance));
            statusLabel.setStyle("-fx-text-fill: #1a56a8;");

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Nights, Extras and Amount paid.");
        }
    }

    @FXML
    private void handleSaveBill() {
        try {
            String guest = guestNameField.getText().trim();
            String room = roomTypeCombo.getValue();
            int nights = Integer.parseInt(nightsField.getText().trim());
            double extras = Double.parseDouble(extrasField.getText().trim());
            double paid = Double.parseDouble(amountPaidField.getText().trim());
            String notes = notesField.getText().trim();

            if (guest.isEmpty()) {
                showAlert("Error", "Please enter guest name.");
                return;
            }
            if (nights <= 0) {
                showAlert("Error", "Nights must be greater than 0.");
                return;
            }

            double roomRate = getRoomRate(room);
            double roomCharge = roomRate * nights;

            if (nights >= 5) {
                roomCharge = roomCharge * 0.95; // 5% discount
            }

            double total = roomCharge + extras;
            double balance = total - paid;

            String billId = String.format("B%03d", nextBillNumber++);
            Bill bill = new Bill(billId, guest, room, nights, extras, paid, notes, total, balance);
            bills.add(bill);

            statusLabel.setText("Saved " + billId + ": Balance: K " + String.format("%.2f", balance));
            statusLabel.setStyle("-fx-text-fill: green;");

            // Clear form after saving
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Nights, Extras and Amount paid.");
        }
    }

    @FXML
    private void handleNewGuest() {
        clearForm();
        statusLabel.setText("");
    }

    @FXML
    private void handleDeleteBill() {
        Bill selected = billsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Info", "Please select a bill to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete bill " + selected.getBillId() + "?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            bills.remove(selected);
            statusLabel.setText("Bill " + selected.getBillId() + " deleted.");
            statusLabel.setStyle("-fx-text-fill: #c0392b;");
        }
    }

    @FXML
    private void handleViewReceipt() {
        Bill selected = billsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Info", "Please select a bill to view the receipt.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/receipt.fxml"));
            Parent root = loader.load();

            ReceiptController controller = loader.getController();
            controller.setBill(selected);

            Stage stage = new Stage();
            stage.setTitle("Receipt - " + selected.getBillId());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            // If receipt.fxml is not yet created, show a simple alert instead
            showAlert("Receipt",
                    "Bill: " + selected.getBillId() + "\n" +
                            "Guest: " + selected.getGuestName() + "\n" +
                            "Room: " + selected.getRoomType() + "\n" +
                            "Nights: " + selected.getNights() + "\n" +
                            "Total: " + selected.getTotalFormatted() + "\n" +
                            "Paid: " + selected.getPaidFormatted() + "\n" +
                            "Balance: " + selected.getBalanceFormatted());
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        showAlert("About", "Lodge Billing Lab\nICT261 Lecture 3\nMr E Nyirenda\nMulungushi University");
    }

    private double getRoomRate(String roomType) {
        return switch (roomType) {
            case "Deluxe" -> DELUXE_PRICE;
            case "Family" -> FAMILY_PRICE;
            default -> STANDARD_PRICE;
        };
    }

    private void clearForm() {
        guestNameField.clear();
        roomTypeCombo.getSelectionModel().selectFirst();
        nightsField.clear();
        extrasField.clear();
        amountPaidField.clear();
        notesField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}