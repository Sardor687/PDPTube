package uz.app.pdptube.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.pdptube.dto.SettingsDTO;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.SettingsService;
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    // Foydalanuvchi sozlamalarini olish
    @GetMapping("/profile")
    public ResponseEntity<?> getSettings() {
        ResponseMessage responseMessage = settingsService.getSettings();
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(responseMessage);
    }


    // Foydalanuvchi sozlamalarini yangilash (yangi email kiritiladi, tasdiqlash kodi yuboriladi)
    @PutMapping("/update")
    public ResponseEntity<?> updateSettings(@RequestBody SettingsDTO settingsDTO) {
        ResponseMessage responseMessage = settingsService.updateSettings(settingsDTO);
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }





}
