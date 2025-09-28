package ru.practicum.adminApi.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.CategoriesRepository;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dto.category.CategoryDto;
import ru.practicum.base.dto.category.NewCategoryDto;
import ru.practicum.base.exception.ConditionsNotMetException;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.mapper.CategoryMapper;
import ru.practicum.base.model.Category;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoriesServiceImpl implements AdminCategoriesService {

    private final CategoriesRepository categoriesRepository;

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        log.info("Создание категории: {}", dto);
        checkCategoryName(dto);
        Category category = CategoryMapper.toEntity(dto);
        category = categoriesRepository.save(category);
        return CategoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public void delete(Long catId) {
        log.info("Удаление категории с ID = {}", catId);
        if (eventRepository.existsByCategory(getCategoryById(catId))) {
            throw new ConditionsNotMetException("Категория не должна быть пустой у события");
        }
        categoriesRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto update(NewCategoryDto dto, Long catId) {
        log.info("Обновление категории на {} с ID = {}", dto.getName(), catId);
        Category category = getCategoryById(catId);
        if (!Objects.equals(dto.getName(), category.getName())) {
            checkCategoryName(dto);
        }
        category.setName(dto.getName());
        categoriesRepository.save(category);
        return CategoryMapper.toDto(category);
    }

    private void checkCategoryName(NewCategoryDto dto) {
        if (categoriesRepository.existsCategoryByName(dto.getName())) {
            throw new ConflictException(String.format("Имя категории %s уже занято", dto.getName()));
        }
    }

    private Category getCategoryById(Long id) {
        log.info("Получение категории с ID = {}", id);
        return getCategory(id);
    }

    private Category getCategory(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с ID = %s не найдена", id)));
    }

}
