package com.chip.parkpro1.interfaces;

public interface SignUpStuff {
    void next1Pressed(String firstName, String lastName, String email, String password);

    void next2Pressed(String make, String model, String color, String plateNumber);

    void next3Pressed(String type, String cardNumber, String expiryDate, String CVV2, String cardHolderName);

    void backToPrevious(int index);

    void cancelPressed();
}
