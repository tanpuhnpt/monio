package com.mpt.monio.config;

import com.mpt.monio.category.entity.Category;
import com.mpt.monio.category.repo.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DatabaseSeeder {

    @Bean
    ApplicationRunner applicationRunner(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                log.info("Bắt đầu khởi tạo danh mục mặc định cho Monio...");

                List<String> defaultCategoryNames = List.of(
                        "Ăn uống", "Di chuyển", "Hóa đơn & Tiện ích", "Mua sắm",
                        "Giải trí","Sức khỏe", "Giáo dục", "Gia đình", "Du lịch", "Quà",

                        "Tiền lương", "Thưởng", "Đầu tư", "Kinh doanh",

                        "Khác"
                );

                List<Category> categories = defaultCategoryNames.stream()
                        .map(name -> {
                            Category category = new Category();
                            category.setName(name);
                            return category;
                        })
                        .toList();

                categoryRepository.saveAll(categories);

                log.info("Đã khởi tạo thành công {} danh mục mặc định!", categories.size());
            } else {
                log.info("Dữ liệu danh mục đã tồn tại. Bỏ qua bước Seeding.");
            }
        };
    }
}
