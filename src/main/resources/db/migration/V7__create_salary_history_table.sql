CREATE TABLE salary_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    salary DECIMAL(10,2) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    employee_id BIGINT NOT NULL,
    CONSTRAINT fk_salary_history_employee FOREIGN KEY (employee_id) REFERENCES employee(id)
);