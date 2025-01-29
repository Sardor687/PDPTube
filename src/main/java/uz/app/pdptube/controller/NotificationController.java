package uz.app.pdptube.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ResponseMessage> notifications() {
        ResponseMessage serviceResponse = notificationService.notifications();
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NO_CONTENT).body(serviceResponse);
    }
}
