package com.devsuperior.dscommerce.factory;

import com.devsuperior.dscommerce.entities.Category;

public class CategoryFactory {

    public static Category createCategory()  {
        Category category = new Category(1L, "Electronics");
        return category;
    }
}
