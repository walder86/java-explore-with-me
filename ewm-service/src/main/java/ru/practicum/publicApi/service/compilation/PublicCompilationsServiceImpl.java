package ru.practicum.publicApi.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.CompilationRepository;
import ru.practicum.base.dto.compilation.CompilationDto;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.mapper.CompilationMapper;
import ru.practicum.base.model.Compilation;
import ru.practicum.base.util.page.MyPageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicCompilationsServiceImpl implements PublicCompilationsService {
    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        log.info("Получение списка сборников");
        MyPageRequest pageable = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }

        return CompilationMapper.toDtoList(compilations);
    }

    @Override
    public CompilationDto get(Long comId) {
        log.info("Получение сборника с ID = {}", comId);
        final Compilation compilation = getCompilation(comId);
        return CompilationMapper.toDto(compilation);
    }

    private Compilation getCompilation(Long comId) {
        return compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(String.format("Сборник с ID = %s не найден", comId)));
    }
}
