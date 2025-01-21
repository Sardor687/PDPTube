package uz.app.pdptube.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.pdptube.dto.ChannelDTO;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.ChannelService;

import java.util.Optional;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;


    @GetMapping
    public ResponseEntity<?> getChannel() {
        ResponseMessage serviceResponse = channelService.getChannel();
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(serviceResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createChannel(@RequestBody ChannelDTO channelDTO) {
        ResponseMessage serviceResponse = channelService.createChannel(channelDTO);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(serviceResponse);

    }


    //videolar borligi sababli o'chirib bo'lmayapti, o'zim to'girlab qo'yaman hech kim tegmasin!!!
    //bitta videoni o'chirish,
    //kanalni o'chirishda videolarni ham o'chib ketishi
    //shu case lar hali to'g'irlanmadi
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteChannel() {
        ResponseMessage serviceResponse =  channelService.deleteChannel();
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }




}
