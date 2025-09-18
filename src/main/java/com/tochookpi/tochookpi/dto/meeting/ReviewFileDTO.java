package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.entity.ReviewFileEntity;
import com.tochookpi.tochookpi.enums.FileState;
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
    private FileState state;

    public ReviewFileEntity toEntity() {
        return ReviewFileEntity.builder()
                .id(this.id)
                .url(this.url)
                .type(this.type)
                .build();
    }
}
