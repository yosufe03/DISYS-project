package org.example.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.function.Consumer;

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

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        initializeDateTimeSelectors();
        loadCurrentData();
    }

    private void initializeDateTimeSelectors() {
        LocalDateTime now = LocalDateTime.now();

        startDatePicker.setValue(now.toLocalDate());
        endDatePicker.setValue(now.plusHours(1).toLocalDate());

        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour()));
        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.plusHours(1).getHour()));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
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
        getAsync(API_URL + "/current", this::showCurrent);
    }

    private void showCurrent(JSONObject data) {
        communityPoolLabel.setText(formatPercent(data.getDouble("communityDepleted")) + " used");
        gridPortionLabel.setText(formatPercent(data.getDouble("gridPortion")));
    }

    private void loadHistoricalData() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showError("Please select both start and end date.");
            return;
        }

        LocalDateTime start = LocalDateTime.of(
                startDate,
                LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue())
        );
        LocalDateTime end = LocalDateTime.of(
                endDate,
                LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue())
        );

        if (start.isAfter(end)) {
            showError("Start must be before end.");
            return;
        }

        String url = String.format("%s/historical?start=%s&end=%s",
                API_URL,
                start.atZone(ZoneId.systemDefault()).toInstant(),
                end.atZone(ZoneId.systemDefault()).toInstant());

        getAsync(url, this::showHistorical);
    }

    private void getAsync(String url, Consumer<JSONObject> onSuccessFunction) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        onSuccessFunction.accept(new JSONObject(response.body()));
                    } else {
                        showError("Could not load data. HTTP status: " + response.statusCode());
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Could not load data: " + ex.getMessage()));
                    return null;
                });
    }

    private void showHistorical(JSONObject data) {
        communityProducedLabel.setText(formatKwh(data.getDouble("communityProduced")));
        communityUsedLabel.setText(formatKwh(data.getDouble("communityUsed")));
        gridUsedLabel.setText(formatKwh(data.getDouble("gridUsed")));
    }

    private String formatPercent(double value) {
        return String.format("%.2f %%", value);
    }

    private String formatKwh(double value) {
        return String.format("%.3f kWh", value);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Energy Community");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
