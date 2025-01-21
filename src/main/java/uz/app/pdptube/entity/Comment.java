package uz.app.pdptube.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;


    private Integer videoId;

    @Column(nullable = false)
    private String text;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer dislikes = 0;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private LinkedList<Comment> replies;

    @ManyToOne
    private Comment parentComment;

    @PrePersist
    public void setDefaults() {
        if (this.likes == null) {
            this.likes = 0;
        }
        if (this.dislikes == null) {
            this.dislikes = 0;
        }
    }
}
