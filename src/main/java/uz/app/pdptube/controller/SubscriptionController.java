package uz.app.pdptube.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.SubscriptionService;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<ResponseMessage> getSubscriptions() {
        ResponseMessage serviceResponse = subscriptionService.getSubscriptions();
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(serviceResponse);
    }




    @PostMapping("/subscribe/{channelId}")
    public ResponseEntity<ResponseMessage> subscribeToChannel(@PathVariable Integer channelId) {
        ResponseMessage serviceResponse = subscriptionService.subscribeToChannel(channelId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }

    @DeleteMapping("/unsubscribe/{channelId}")
    public ResponseEntity<ResponseMessage> unsubscribeFromChannel(@PathVariable Integer channelId) {
        ResponseMessage serviceResponse = subscriptionService.unsubscribeFromChannel(channelId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }
}
