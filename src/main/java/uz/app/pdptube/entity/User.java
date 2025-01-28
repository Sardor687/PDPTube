package uz.app.pdptube.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private Integer age;

    private String profilePicture;


    @Override
    @JsonIgnore
    public String getUsername() {
        return email; // Email login sifatida ishlatiladi
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true; // Hisob muddati tugamagan
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true; // Hisob bloklanmagan
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true; // Foydalanuvchi ma'lumotlari amal qiladi
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true; // Foydalanuvchi faolligi
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Simple role (masalan: USER yoki ADMIN)
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Barchaga "USER" roli berilgan
        return authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
