package com.antcheckers.ui;

import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class Validation {

    private static final int MAX_NODE_VALUE = 15;
    private static final int MIN_NODE_VALUE = -15;

    static boolean outOfRange(TextField textField, Label error, int min, int max){
        try{
            int val = Integer.parseInt(textField.getText());
            if(val < min || val > max) {
                error.setText("*Field value should be an integer between " + min + " and " + max);
                return true;
            } else {
                error.setText("");
                return false;
            }
        } catch (NumberFormatException nfe) {
            error.setText("*Field value should be an integer between " + min + " and " + max);
            return true;
        }
    }

    static boolean outOfRange(TextField textField, Label error, float min, float max){
        try{
            float val = Float.parseFloat(textField.getText());
            if(val < min || val > max) {
                error.setText("*Field value should range between " + min + " and " + max);
                return true;
            } else {
                error.setText("");
                return false;
            }
        } catch (NumberFormatException nfe) {
            error.setText("*Field value should range between " + min + " and " + max);
            return true;
        }
    }

    static boolean weightsError(TextField minTextField, TextField maxTextField, Label error){
        try{
            float min = Integer.parseInt(minTextField.getText());
            float max = Integer.parseInt(maxTextField.getText());
            if(min < MIN_NODE_VALUE || min > MAX_NODE_VALUE || max < MIN_NODE_VALUE || max > MAX_NODE_VALUE) {
                error.setText("*Fields values should be an integers between  " + MIN_NODE_VALUE + " and " + MAX_NODE_VALUE);
                return true;
            } else if(max <= min) {
                error.setText("*Maximal node value should be bigger than minimal");
                return true;
            } else {
                error.setText("");
                return false;
            }
        } catch (NumberFormatException nfe) {
            error.setText("*Fields values should be an integers between  " + MIN_NODE_VALUE + " and " + MAX_NODE_VALUE);
            return true;
        }
    }

    static boolean toggleNotSelected(ToggleGroup toggleGroup, Label error) {
        try {
            RadioButton radioButton = (RadioButton) toggleGroup.getSelectedToggle();
            radioButton.getText();
        } catch (NullPointerException npe) {
            error.setText("*Choice required");
            return true;
        }
        error.setText("");
        return false;
    }
}
