package com.chip.parkpro1.exceptions;

public final class ErrorRequest {

    public enum ErrorType {

        AuthFailureError(4), ServerError(9), NetworkError(5), ParseError(8);
        private final int code;

        ErrorType(int code) {
            this.code = code;
        }

        public int toInt() {
            return code;
        }

        public String toString() {
            switch (code) {
                case 4:
                    return "Authentication Failed";
                case 9:
                    return "Server Error";
                case 5:
                    return "Network Error";
                case 8:
                    return "Parse Error";
            }
            return String.valueOf(code);
        }
    }

}
