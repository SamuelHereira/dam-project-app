package com.example.ugproject.utils;

public class Validations {

    public static boolean validateCedula(String cedula) {
        if (cedula.length() != 10) {
            return false;
        }
        int sum = 0;
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int[] digitos = new int[10];
        for (int i = 0; i < 10; i++) {
            digitos[i] = Integer.parseInt(cedula.substring(i, i + 1));
        }
        for (int i = 0; i < 9; i++) {
            int temp = digitos[i] * coeficientes[i];
            if (temp > 9) {
                temp -= 9;
            }
            sum += temp;
        }
        int lastDigit = Integer.parseInt(cedula.substring(9, 10));
        int lastDigitCalculated = (sum % 10) == 0 ? 0 : 10 - (sum % 10);
        return lastDigit == lastDigitCalculated;
    }

    public static boolean validateEmail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }

    public static boolean validateOnlyNumbers(String text) {
        return text.matches("[0-9]+");
    }

    public static boolean validateOnlyLetters(String text) {
        return text.matches("[a-zA-Z ]+");
    }


}




