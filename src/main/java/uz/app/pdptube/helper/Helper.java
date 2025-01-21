package uz.app.pdptube.helper;

import org.springframework.security.core.context.SecurityContextHolder;
import uz.app.pdptube.entity.User;

public class Helper {


    public static User getCurrentPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
