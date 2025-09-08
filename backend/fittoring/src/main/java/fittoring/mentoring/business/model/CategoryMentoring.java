package fittoring.mentoring.business.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category_mentoring")
@Entity
public class CategoryMentoring {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Category category;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    @ManyToOne
    private Mentoring mentoring;

    public CategoryMentoring(Category category, Mentoring mentoring) {
        this(null, category, mentoring);
    }

    public String getCategoryTitle() {
        return category.getTitle();
    }
}

