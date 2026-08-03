package com.expenso.expense_tracker.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================
 * Category Response DTO
 * ============================================================
 *
 * Used for category listing APIs.
 *
 * ============================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    /**
     * Category Name
     */
    private String name;

}