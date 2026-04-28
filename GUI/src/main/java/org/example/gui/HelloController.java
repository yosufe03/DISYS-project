package org.example.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import org.json.JSONArray;
import org.json.JSONObject;

public class HelloController {
    @FXML private Label communityPoolLabel;
    @FXML private Label gridPortionLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;
    @FXML private Spinner<Integer> startMinuteSpinner;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> endHourSpinner;
    @FXML private Spinner<Integer> endMinuteSpinner;
    @FXML private Label communityProducedLabel;
    @FXML private Label communityUsedLabel;
    @FXML private Label gridUsedLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        initializeSpinners();
        loadCurrentData();
    }

    private void initializeSpinners() {
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 14));
        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 14));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        
        startDatePicker.setValue(LocalDate.of(2025, 1, 10));
        endDatePicker.setValue(LocalDate.of(2025, 2, 10));
    }

    @FXML
    protected void onRefreshClick() {
        loadCurrentData();
    }

    @FXML
    protected void onShowDataClick() {
        loadHistoricalData();
    }

    private void loadCurrentData() {
        apiClient.getAsync("/current")
                .thenAccept(response -> Platform.runLater(() -> {
                    JSONObject data = new JSONObject(response);
                    communityPoolLabel.setText(String.format("%.2f%% used", data.getDouble("communityPercentage")));
                    gridPortionLabel.setText(String.format("%.2f%%", data.getDouble("gridPercentage")));
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Current data could not be loaded: \n" + ex));
                    return null;
                });
    }

    private void loadHistoricalData() {
        LocalDateTime start = LocalDateTime.of(
                startDatePicker.getValue(),
                LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue())
        );
        LocalDateTime end = LocalDateTime.of(
                endDatePicker.getValue(),
                LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue())
        );

        String queryString = String.format("?start=%s&end=%s",
                start.atOffset(ZoneOffset.UTC),
                end.atOffset(ZoneOffset.UTC));

        apiClient.getAsync("/historical" + queryString)
                .thenAccept(response -> Platform.runLater(() -> {
                    JSONArray data = new JSONArray(response);
                    double produced = 0, used = 0, grid = 0;
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject entry = data.getJSONObject(i);
                        produced += entry.getDouble("communityProduced");
                        used += entry.getDouble("communityUsed");
                        grid += entry.getDouble("gridUsed");
                    }
                    communityProducedLabel.setText(String.format("%.3f kWh", produced));
                    communityUsedLabel.setText(String.format("%.3f kWh", used));
                    gridUsedLabel.setText(String.format("%.3f kWh", grid));
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Historic data could not be loaded: \n" + ex));
                    return null;
                });
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
