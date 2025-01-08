package uz.app.pdptube.entity;


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
    private List<User> followers;


    @OneToOne
    @JoinColumn(name = "owner_id", unique = true, nullable = false)
    private User owner;



}
