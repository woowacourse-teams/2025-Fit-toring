package fittoring.application.mentoring.service;

import fittoring.application.mentoring.presentation.dto.response.CategoryResponse;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.domain.model.Category;
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
