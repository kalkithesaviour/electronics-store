package com.vishal.electronicsstore.util;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import com.vishal.electronicsstore.dto.PageableResponse;

public class PageableUtil {

    private PageableUtil() {
    }

    public static <D, E> PageableResponse<D> getPageableResponse(
            Page<E> page,
            Class<D> type,
            ModelMapper modelMapper) {
        List<E> entities = page.getContent();

        List<D> dtos = entities.stream()
                .map(entity -> entityToDto(entity, type, modelMapper)).toList();

        return PageableResponse.<D>builder()
                .content(dtos)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();
    }

    private static <D, E> D entityToDto(E entity, Class<D> type, ModelMapper modelMapper) {
        return modelMapper.map(entity, type);
    }

}
