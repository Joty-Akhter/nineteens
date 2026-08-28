package com.nineteens.web.dto;

import com.nineteens.domain.category.Category;
import com.nineteens.domain.category.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CategoryDtos {

    private CategoryDtos() {
    }

    public record CategoryResponse(
            Long id,
            String name,
            String slug,
            String description,
            String imageUrl,
            CategoryStatus status
    ) {
        public static CategoryResponse from(Category category) {
            return new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getSlug(),
                    category.getDescription(),
                    category.getImageUrl(),
                    category.getStatus());
        }
    }

    public record CategoryRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 180) String slug,
            String description,
            String imageUrl,
            CategoryStatus status
    ) {
    }
}
