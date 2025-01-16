package uz.app.pdptube.payload;

public record ResponseMessage(boolean success, String message, Object data) {
}
