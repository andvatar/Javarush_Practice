package com.example.encryptorui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.swing.*;

public class EncryptorUIController {

    @FXML
    private TextField filePath;
    @FXML
    private TextField statisticFilePath;
    @FXML
    private TextField key;


    @FXML
    protected void selectFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePath.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    @FXML
    protected void selectStatisticFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            statisticFilePath.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    @FXML
    protected void encrypt() {
        if (filePath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Выберите файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (key.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Укажите ключ", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Integer.parseInt(key.getText());
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(null, "Ключ должен быть целым числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {

            Encryptor.encrypt(filePath.getText(), Integer.parseInt(key.getText()), "_Encypted");
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Файл успешно зашифрован", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    @FXML
    protected void decrypt() {
        if (filePath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Выберите файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (key.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Укажите ключ", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Integer.parseInt(key.getText());
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(null, "Ключ должен быть целым числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Encryptor.decrypt(filePath.getText(), Integer.parseInt(key.getText()), "_Decrypted");
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Файл успешно расшифрован", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    @FXML
    protected void bruteForce() {
        if (filePath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Выберите файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Encryptor.decryptBruteForce(filePath.getText(), "_bruteforced");
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Файл успешно расшифрован", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    @FXML
    protected void statisticAnalysis() {
        if (filePath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Выберите файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (statisticFilePath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Укажите файл с образцом текста", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Encryptor.decryptStatistic(filePath.getText(), statisticFilePath.getText(), "_statistic");
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Файл успешно расшифрован", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }
}