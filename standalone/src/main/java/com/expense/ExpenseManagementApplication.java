package com.expense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.expense.facade",
    "com.expense.management",
    "com.expense.dbadapter",
    "com.expense.model"
})
public class ExpenseManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExpenseManagementApplication.class, args);
    }
}
