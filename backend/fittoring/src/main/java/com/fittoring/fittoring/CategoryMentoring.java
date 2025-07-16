package com.fittoring.fittoring;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category_mentoring")
@Entity
public class CategoryMentoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
