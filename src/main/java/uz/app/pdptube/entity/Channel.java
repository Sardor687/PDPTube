package uz.app.pdptube.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true)
    private String name;


    private String description;

    private String profilePicture;

    @ManyToMany
    @JoinTable(
            name = "channel_followers",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> followers;


    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id", unique = true, nullable = false)
    @JsonIgnore
    private User owner;





}
