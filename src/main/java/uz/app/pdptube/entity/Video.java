package uz.app.pdptube.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.app.pdptube.enums.Category;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category = Category.UNDEFINED;

    @Column(nullable = false)
    private Integer ageRestriction = 0;

    @Column(nullable = false)
    private String videoLink;

    @OneToMany(mappedBy = "video")
    private List<Comment> comments;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer dislikes = 0;

    @Column(nullable = false)
    private Integer views = 0;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Channel channel;

    @PrePersist
    public void setDefaults() {
        if (this.category == null) {
            this.category = Category.UNDEFINED;
        }
        if (this.ageRestriction == null) {
            this.ageRestriction = 0;
        }
        if (this.likes == null) {
            this.likes = 0;
        }
        if (this.dislikes == null) {
            this.dislikes = 0;
        }
        if (this.views == null) {
            this.views = 0;
        }
    }
}
