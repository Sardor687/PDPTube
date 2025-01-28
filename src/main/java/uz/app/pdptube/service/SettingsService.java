package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.pdptube.dto.SettingsDTO;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserRepository userRepository;

    // Yangi email va parolni tasdiqlash uchun oraliq saqlanadigan ma'lumotlar:
    // Masalan: verificationDataMap.put(yangiEmail, verificationData)

    /**
     * 1) Foydalanuvchi joriy sozlamalarini olish
     */
    public ResponseMessage getSettings() {
        User currentUser = Helper.getCurrentPrincipal();

        // Sozlamalarni DTO ko‘rinishida qaytarish
        SettingsDTO settingsDTO = SettingsDTO.builder()
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .email(currentUser.getEmail())  // hozirgi email
                .age(currentUser.getAge())
                .profilePicture(currentUser.getProfilePicture())
                .build();

        return new ResponseMessage(true, "Foydalanuvchi malumotlari muvaffaqiyatli olindi", settingsDTO);
    }


    /**
     * 2) Sozlamalarni yangilash (yangi email + yangi parol + boshqa ma’lumotlar)
     * Yangi emailga 4 xonali kod yuboramiz, to'g'ri kiritsa keyin confirm qilinadi.
     */
    public ResponseMessage updateSettings(SettingsDTO settingsDTO) {
        User currentUser = Helper.getCurrentPrincipal();


        // 2.1) Ism, familiya, age, profilePicture kabi oddiy maydonlarni darhol yangilaymiz
        if (settingsDTO.getFirstName() != null) {
            currentUser.setFirstName(settingsDTO.getFirstName());
        }
        if (settingsDTO.getLastName() != null) {
            currentUser.setLastName(settingsDTO.getLastName());
        }
        if (settingsDTO.getAge() != null) {
            currentUser.setAge(settingsDTO.getAge());
        }
        if (settingsDTO.getProfilePicture() != null) {
            currentUser.setProfilePicture(settingsDTO.getProfilePicture());
        }

        // 2.2) Agar foydalanuvchi "email" va "newPassword" ham jo‘natgan bo‘lsa:
        if (settingsDTO.getEmail() != null && settingsDTO.getPassword() != null) {

            // (a) Yangi parol murakkabligini tekshiramiz
            if (!isStrongPassword(settingsDTO.getPassword())) {
                return new ResponseMessage(false,
                        "Parol juda oddiy! Kamida 8 belgi, 1 ta katta harf, 1 ta kichik harf, " +
                                "1 ta raqam va 1 ta maxsus belgi bo‘lishi kerak.",
                        null
                );
            }

            // (b) Yangi email boshqa userga tegishli emasligini tekshirish
            Optional<User> byEmail = userRepository.findByEmail(settingsDTO.getEmail());
            if (byEmail.isPresent() && !byEmail.get().getId().equals(currentUser.getId())) {
                return new ResponseMessage(false, "Ushbu email allaqachon band", null);
            }
            currentUser.setPassword(settingsDTO.getPassword());
            currentUser.setEmail(settingsDTO.getEmail());
            userRepository.save(currentUser);
            return new ResponseMessage(true,"Sozlamalar yangilandi", null);
        } else {
            // Agar email yoki parol kiritilmagan bo‘lsa, demak faqat ism/familiya/age/rasm o‘zgardi
            userRepository.save(currentUser);
            return new ResponseMessage(true,
                    "Sozlamalar muvaffaqiyatli yangilandi (email/parol o‘zgarmadi).",
                    currentUser
            );
        }
    }


    /**
     * 3) Tasdiqlash kodi (verify) – foydalanuvchi /settings/update-verify
     * ga newEmail va code jo‘natadi. To‘g‘ri bo‘lsa, bazadagi userga
     * shu email va shu parolni o‘rnatamiz.
     */


    /**
     * 4) Parol murakkabligini tekshirish (regex).
     * Kamida 8 ta belgi, kamida 1 ta katta harf, 1 ta kichik harf,
     * 1 ta raqam va 1 ta maxsus belgi bo‘lishi kerak.
     */
    private boolean isStrongPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(regex);
    }

    /**
     * 5) 4 xonali tasdiqlash kodini generatsiya qilish
     */
    private String generateVerificationCode() {
        return String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 6) Ichki yordamchi klass: Tasdiqlash paytida vaqtincha saqlanadigan ma’lumotlar
     */
    private static class VerificationData {
        private String newEmail;         // foydalanuvchi kiritgan yangi email
        private String newPassword;      // foydalanuvchi kiritgan yangi doimiy parol
        private String verificationCode; // 4 xonali kod
        private Integer userId;          // qaysi userga tegishli

        public VerificationData(String newEmail, String newPassword, String verificationCode, Integer userId) {
            this.newEmail = newEmail;
            this.newPassword = newPassword;
            this.verificationCode = verificationCode;
            this.userId = userId;
        }

        public String getNewEmail() {
            return newEmail;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public Integer getUserId() {
            return userId;
        }
    }
}
