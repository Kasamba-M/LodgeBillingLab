package com.mulungushi.lodgebilling;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ReceiptController {

    @FXML private Label billIdLabel;
    @FXML private Label guestLabel;
    @FXML private Label roomLabel;
    @FXML private Label nightsLabel;
    @FXML private Label extrasLabel;
    @FXML private Label totalLabel;
    @FXML private Label paidLabel;
    @FXML private Label balanceLabel;
    @FXML private Label notesLabel;

    public void setBill(Bill bill) {
        billIdLabel.setText(bill.getBillId());
        guestLabel.setText(bill.getGuestName());
        roomLabel.setText(bill.getRoomType());
        nightsLabel.setText(String.valueOf(bill.getNights()));
        extrasLabel.setText(String.format("K %.2f", bill.getExtras()));
        totalLabel.setText(bill.getTotalFormatted());
        paidLabel.setText(bill.getPaidFormatted());
        balanceLabel.setText(bill.getBalanceFormatted());

        if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
            notesLabel.setText("Notes: " + bill.getNotes());
        } else {
            notesLabel.setText("");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) billIdLabel.getScene().getWindow();
        stage.close();
    }
}