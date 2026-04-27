package org.example.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
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

    private static final String API_URL = "http://localhost:8080/energy";
    private HttpClient httpClient = HttpClient.newHttpClient();

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
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/current"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = new JSONObject(response.body());
            
            communityPoolLabel.setText(String.format("%.2f%% used", data.getDouble("communityPercentage")));
            gridPortionLabel.setText(String.format("%.2f%%", data.getDouble("gridPercentage")));
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void loadHistoricalData() {
        try {
            LocalDateTime start = LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue())
            );
            LocalDateTime end = LocalDateTime.of(
                    endDatePicker.getValue(),
                    LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue())
            );

            String url = String.format("%s/historical?from=%s&to=%s", 
                    API_URL, 
                    start.atOffset(ZoneOffset.UTC), 
                    end.atOffset(ZoneOffset.UTC));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray data = new JSONArray(response.body());
            
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
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
