package uz.app.pdptube.helper;

import org.springframework.security.core.context.SecurityContextHolder;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.entity.Video;

public class Helper {

    public static User getCurrentPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    public static boolean ageRestricted(Video video) {
        int ageRestriction = video.getAgeRestriction();
        User user = Helper.getCurrentPrincipal();
        if (user == null) {
            return true;
        }
        Integer age = user.getAge();

        return (ageRestriction > age);
    }

    public static boolean fileUploaded(String videoLink){
        return !(videoLink.equalsIgnoreCase("String") || videoLink.isEmpty() || videoLink.isBlank());
    }
}
