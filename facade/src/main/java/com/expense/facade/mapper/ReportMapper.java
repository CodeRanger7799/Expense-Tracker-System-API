package com.expense.facade.mapper;

import com.expense.api.model.ExpenseReportApiResponse;
import com.expense.model.dto.ExpenseReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    
    @Mapping(target = "totalAmount", expression = "java(bigDecimalToDouble(dto.getTotalAmount()))")
    @Mapping(target = "categorySubtotals", expression = "java(convertCategorySubtotals(dto.getCategorySubtotals()))")
    @Mapping(target = "generatedAt", expression = "java(localDateTimeToOffsetDateTime(dto.getGeneratedAt()))")
    ExpenseReportApiResponse dtoToApiResponse(ExpenseReportResponse dto);
    
    default Double bigDecimalToDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
    
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
    
    default Map<String, Double> convertCategorySubtotals(Map<String, BigDecimal> subtotals) {
        if (subtotals == null) {
            return null;
        }
        Map<String, Double> result = new HashMap<>();
        subtotals.forEach((key, value) -> result.put(key, value != null ? value.doubleValue() : null));
        return result;
    }
}
