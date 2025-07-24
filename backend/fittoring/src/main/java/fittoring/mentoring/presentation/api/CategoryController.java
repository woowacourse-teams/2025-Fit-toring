package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.CategoryService;
import fittoring.mentoring.presentation.dto.CategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> responseBody = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }
}
