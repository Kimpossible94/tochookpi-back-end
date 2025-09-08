package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.entity.ReviewFileEntity;
import com.tochookpi.tochookpi.enums.FileType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFileDTO {
    private Long id;
    private String url;
    private FileType type;

    public ReviewFileEntity toEntity() {
        return ReviewFileEntity.builder()
                .url(this.url)
                .type(this.type)
                .build();
    }
}
