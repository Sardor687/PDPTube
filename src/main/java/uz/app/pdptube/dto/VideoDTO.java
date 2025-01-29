package uz.app.pdptube.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.app.pdptube.enums.Category;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private String title;
    private String description;
    private Category category = Category.UNDEFINED;
    private Integer ageRestriction = 0;
//    private String videoLink;
}
