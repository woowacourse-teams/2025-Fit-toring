package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.presentation.dto.CategoryResponse;
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
