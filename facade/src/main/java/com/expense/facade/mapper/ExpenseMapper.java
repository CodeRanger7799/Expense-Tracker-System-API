package com.expense.facade.mapper;

import com.expense.api.model.ExpenseApiRequest;
import com.expense.api.model.ExpenseApiResponse;
import com.expense.model.dto.ExpenseRequest;
import com.expense.model.dto.ExpenseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    
    @Mapping(target = "amount", expression = "java(doubleToBigDecimal(apiRequest.getAmount()))")
    ExpenseRequest apiRequestToDto(ExpenseApiRequest apiRequest);
    
    @Mapping(target = "amount", expression = "java(bigDecimalToDouble(dto.getAmount()))")
    @Mapping(target = "createdAt", expression = "java(localDateTimeToOffsetDateTime(dto.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(localDateTimeToOffsetDateTime(dto.getUpdatedAt()))")
    ExpenseApiResponse dtoToApiResponse(ExpenseResponse dto);
    
    List<ExpenseApiResponse> dtoListToApiResponseList(List<ExpenseResponse> dtoList);
    
    default BigDecimal doubleToBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
    
    default Double bigDecimalToDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
    
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
}
