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
        int age = Helper.getCurrentPrincipal().getAge();
        return (ageRestriction > age);
    }
}
