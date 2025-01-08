package uz.app.pdptube.payload;

public record ResponseMessage(Boolean success, String message, Object data) {
}
