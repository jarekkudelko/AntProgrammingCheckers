package com.antcheckers.ui;

import com.antcheckers.antcolony.AntColony;
import com.antcheckers.utility.Parameters;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    public ToggleGroup players, prediction, turns;
    public HBox playersGroup, predictionGroup, turnsGroup;
    public TextField cycles, iterations, vaporize, initial, exploit;
    public CheckBox localPheromone;
    public Button submit;

    public TextField playerPawnsMin, playerPawnsMax;
    public TextField opponentPawnsMin, opponentPawnsMax;
    public TextField playerQueensMin, playerQueensMax;
    public TextField opponentQueensMin, opponentQueensMax;
    public TextField playerBaseMin, playerBaseMax;
    public TextField opponentBaseMin, opponentBaseMax;
    public TextField playerMidMin, playerMidMax;
    public TextField opponentMidMin, opponentMidMax;
    public TextField playerEndMin, playerEndMax;
    public TextField opponentEndMin, opponentEndMax;
    public TextField playerEdgeMin, playerEdgeMax;
    public TextField opponentEdgeMin, opponentEdgeMax;
    public TextField playerFormationMin, playerFormationMax;
    public TextField opponentFormationMin, opponentFormationMax;
    public TextField playerStalkingMin, playerStalkingMax;
    public TextField opponentStalkingMin, opponentStalkingMax;

    public Label cyclesError;
    public Label iterationsError;
    public Label vaporizeError;
    public Label initialError;
    public Label exploitError;
    public Label playerPawnsError;
    public Label playerQueensError;
    public Label playerBaseError;
    public Label playerMidError;
    public Label playerEndError;
    public Label playerEdgeError;
    public Label playerFormationError;
    public Label playerStalkingError;
    public Label opponentPawnsError;
    public Label opponentQueensError;
    public Label opponentBaseError;
    public Label opponentMidError;
    public Label opponentEndError;
    public Label opponentEdgeError;
    public Label opponentFormationError;
    public Label opponentStalkingError;
    public Label playersError;
    public Label predictionError;
    public Label turnsError;
    public TextArea output;
    public ProgressBar progress;

    float[] weights;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progress.setVisible(false);
    }

    @FXML protected void getOptimalWeights() {
        Task task = new Task() {
            @Override
            protected Object call() {
                setMatchParameters();
                setSystemParameters();
                setEvaluationParameters();
                AntColony antColony = new AntColony();
                weights = antColony.run();
                return null;
            }
        };
        if (validForm()) {
            output.setText("Computing weights. It may take a while ..." + "\n");
            changeControlsStatus();
            progress.setVisible(true);
            progress.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
            task.setOnSucceeded(e -> {
                changeControlsStatus();
                progress.setVisible(false);
                output.setText("Optimal weights: " + "\n" + Arrays.toString(weights));
            });
        }
    }

    private void setMatchParameters() {
        RadioButton radioButton;
        radioButton = (RadioButton) players.getSelectedToggle();
        Parameters.players = Integer.parseInt(radioButton.getText());
        radioButton = (RadioButton) prediction.getSelectedToggle();
        Parameters.lookupDepth = Integer.parseInt(radioButton.getText());
        radioButton = (RadioButton) turns.getSelectedToggle();
        Parameters.lastTurn = Integer.parseInt(radioButton.getText());
    }

    private void setSystemParameters() {
        Parameters.cycles = Integer.parseInt(cycles.getText());
        Parameters.iterations = Integer.parseInt(iterations.getText());
        Parameters.vaporizeFactor = Float.parseFloat(vaporize.getText());
        Parameters.initialPheromone = Float.parseFloat(initial.getText());
        Parameters.exploitationFactor = Float.parseFloat(exploit.getText());
        Parameters.localPheromone = localPheromone.isSelected();

    }

    private void setEvaluationParameters() {
        Parameters.minNodes[0] = Integer.parseInt(playerPawnsMin.getText());
        Parameters.minNodes[1] = Integer.parseInt(opponentPawnsMin.getText());
        Parameters.minNodes[2] = Integer.parseInt(playerQueensMin.getText());
        Parameters.minNodes[3] = Integer.parseInt(opponentQueensMin.getText());
        Parameters.minNodes[4] = Integer.parseInt(playerBaseMin.getText());
        Parameters.minNodes[5] = Integer.parseInt(opponentBaseMin.getText());
        Parameters.minNodes[6] = Integer.parseInt(playerMidMin.getText());
        Parameters.minNodes[7] = Integer.parseInt(opponentMidMin.getText());
        Parameters.minNodes[8] = Integer.parseInt(playerEndMin.getText());
        Parameters.minNodes[9] = Integer.parseInt(opponentEndMin.getText());
        Parameters.minNodes[10] = Integer.parseInt(playerEdgeMin.getText());
        Parameters.minNodes[11] = Integer.parseInt(opponentEdgeMin.getText());
        Parameters.minNodes[12] = Integer.parseInt(playerFormationMin.getText());
        Parameters.minNodes[13] = Integer.parseInt(opponentFormationMin.getText());
        Parameters.minNodes[14] = Integer.parseInt(playerStalkingMin.getText());
        Parameters.minNodes[15] = Integer.parseInt(opponentStalkingMin.getText());

        Parameters.maxNodes[0] = Integer.parseInt(playerPawnsMax.getText());
        Parameters.maxNodes[1] = Integer.parseInt(opponentPawnsMax.getText());
        Parameters.maxNodes[2] = Integer.parseInt(playerQueensMax.getText());
        Parameters.maxNodes[3] = Integer.parseInt(opponentQueensMax.getText());
        Parameters.maxNodes[4] = Integer.parseInt(playerBaseMax.getText());
        Parameters.maxNodes[5] = Integer.parseInt(opponentBaseMax.getText());
        Parameters.maxNodes[6] = Integer.parseInt(playerMidMax.getText());
        Parameters.maxNodes[7] = Integer.parseInt(opponentMidMax.getText());
        Parameters.maxNodes[8] = Integer.parseInt(playerEndMax.getText());
        Parameters.maxNodes[9] = Integer.parseInt(opponentEndMax.getText());
        Parameters.maxNodes[10] = Integer.parseInt(playerEdgeMax.getText());
        Parameters.maxNodes[11] = Integer.parseInt(opponentEdgeMax.getText());
        Parameters.maxNodes[12] = Integer.parseInt(playerFormationMax.getText());
        Parameters.maxNodes[13] = Integer.parseInt(opponentFormationMax.getText());
        Parameters.maxNodes[14] = Integer.parseInt(playerStalkingMax.getText());
        Parameters.maxNodes[15] = Integer.parseInt(opponentStalkingMax.getText());
    }

    private boolean validForm() {
        boolean valid = true;
        if (Validation.toggleNotSelected(players, playersError))
            valid = false;
        if (Validation.toggleNotSelected(prediction, predictionError))
            valid = false;
        if (Validation.toggleNotSelected(turns, turnsError))
            valid = false;
        if (Validation.outOfRange(cycles, cyclesError, 1, 1000))
            valid = false;
        if (Validation.outOfRange(iterations, iterationsError, 1, 100))
            valid = false;
        if (Validation.outOfRange(vaporize, vaporizeError, 0.001f, 0.1f))
            valid = false;
        if (Validation.outOfRange(initial, initialError, 0.001f, 0.1f))
            valid = false;
        if (Validation.outOfRange(exploit, exploitError, 0.1f, 1f))
            valid = false;
        if(Validation.weightsError(playerPawnsMin, playerPawnsMax, playerPawnsError))
            valid = false;
        if(Validation.weightsError(playerQueensMin, playerQueensMax, playerQueensError))
            valid = false;
        if(Validation.weightsError(playerBaseMin, playerBaseMax, playerBaseError))
            valid = false;
        if(Validation.weightsError(playerMidMin, playerMidMax, playerMidError))
            valid = false;
        if(Validation.weightsError(playerEndMin, playerEndMax, playerEndError))
            valid = false;
        if(Validation.weightsError(playerEdgeMin, playerEdgeMax, playerEdgeError))
            valid = false;
        if(Validation.weightsError(playerFormationMin, playerFormationMax, playerFormationError))
            valid = false;
        if(Validation.weightsError(playerStalkingMin, playerStalkingMax, playerStalkingError))
            valid = false;
        if(Validation.weightsError(opponentPawnsMin, opponentPawnsMax, opponentPawnsError))
            valid = false;
        if(Validation.weightsError(opponentQueensMin, opponentQueensMax, opponentQueensError))
            valid = false;
        if(Validation.weightsError(opponentBaseMin, opponentBaseMax, opponentBaseError))
            valid = false;
        if(Validation.weightsError(opponentMidMin, opponentMidMax, opponentMidError))
            valid = false;
        if(Validation.weightsError(opponentEndMin, opponentEndMax, opponentEndError))
            valid = false;
        if(Validation.weightsError(opponentEdgeMin, opponentEdgeMax, opponentEdgeError))
            valid = false;
        if(Validation.weightsError(opponentFormationMin, opponentFormationMax, opponentFormationError))
            valid = false;
        if(Validation.weightsError(opponentStalkingMin, opponentStalkingMax, opponentStalkingError))
            valid = false;
        return valid;
    }

    private void changeControlsStatus() {
        boolean newStatus = !cycles.isDisable();
        playersGroup.setDisable(newStatus);
        predictionGroup.setDisable(newStatus);
        turnsGroup.setDisable(newStatus);
        cycles.setDisable(newStatus);
        iterations.setDisable(newStatus);
        vaporize.setDisable(newStatus);
        initial.setDisable(newStatus);
        exploit.setDisable(newStatus);
        localPheromone.setDisable(newStatus);
        playerPawnsMin.setDisable(newStatus);
        playerPawnsMax.setDisable(newStatus);
        opponentPawnsMin.setDisable(newStatus);
        opponentPawnsMax.setDisable(newStatus);
        playerQueensMin.setDisable(newStatus);
        playerQueensMax.setDisable(newStatus);
        opponentQueensMin.setDisable(newStatus);
        opponentQueensMax.setDisable(newStatus);
        playerBaseMin.setDisable(newStatus);
        playerBaseMax.setDisable(newStatus);
        opponentBaseMin.setDisable(newStatus);
        opponentBaseMax.setDisable(newStatus);
        playerMidMin.setDisable(newStatus);
        playerMidMax.setDisable(newStatus);
        opponentMidMin.setDisable(newStatus);
        opponentMidMax.setDisable(newStatus);
        playerEndMin.setDisable(newStatus);
        playerEndMax.setDisable(newStatus);
        opponentEndMin.setDisable(newStatus);
        opponentEndMax.setDisable(newStatus);
        playerEdgeMin.setDisable(newStatus);
        playerEdgeMax.setDisable(newStatus);
        opponentEdgeMin.setDisable(newStatus);
        opponentEdgeMax.setDisable(newStatus);
        playerFormationMin.setDisable(newStatus);
        playerFormationMax.setDisable(newStatus);
        opponentFormationMin.setDisable(newStatus);
        opponentFormationMax.setDisable(newStatus);
        playerStalkingMin.setDisable(newStatus);
        playerStalkingMax.setDisable(newStatus);
        opponentStalkingMin.setDisable(newStatus);
        opponentStalkingMax.setDisable(newStatus);
        submit.setDisable(newStatus);
    }
}
