package fittoring.application.service;

import fittoring.domain.model.Category;
import fittoring.application.repository.CategoryRepository;
import fittoring.application.presentation.dto.CategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
            .map(CategoryResponse::from)
            .toList();
    }
}
