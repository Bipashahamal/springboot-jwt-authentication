CREATE TABLE employee_project (
    employee_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id, project_id),
    CONSTRAINT fk_employee_project_employee FOREIGN KEY (employee_id) REFERENCES employee(id),
    CONSTRAINT fk_employee_project_project FOREIGN KEY (project_id) REFERENCES project(id)
);