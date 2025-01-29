package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.pdptube.entity.Notification;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public ResponseMessage notifications(){
        User principal = Helper.getCurrentPrincipal();
        List<Notification> notifications = notificationRepository.findAllByUserId(principal.getId());
        if (notifications.isEmpty()){
            return new ResponseMessage(true, "you don't have any notifications", notifications);
        }else {
            for (Notification notification : notifications) {
                notificationRepository.delete(notification);
            }
            return new ResponseMessage(true, "you have " + notifications.size() + " notifications", notifications);
        }
    }
}
