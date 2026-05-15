-- V1__create_schema.sql
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS workplaces (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    raio_metros DOUBLE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_workplace_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_login (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    cargo VARCHAR(80) NOT NULL,
    workplace_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    tipo_trabalho VARCHAR(20),
    PRIMARY KEY (id),
    UNIQUE KEY uk_employee_user_id (user_id),
    CONSTRAINT fk_employee_workplace FOREIGN KEY (workplace_id) REFERENCES workplaces(id),
    CONSTRAINT fk_employee_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS time_punches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    tipo_time_punch VARCHAR(20) NOT NULL,
    data_hora DATETIME NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_timep_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    time_punch_id BIGINT NOT NULL,
    tipo_alerta VARCHAR(20) NOT NULL,
    mensagem VARCHAR(200),
    resolvido BOOLEAN NOT NULL,
    observacao_admin VARCHAR(255),
    resolvido_em DATETIME,
    data_hora DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_alert_timepunch FOREIGN KEY (time_punch_id) REFERENCES time_punches(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS diarias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    data_diaria DATE NOT NULL,
    modo_diaria VARCHAR(50) NOT NULL,
    created_by DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_diaria_employee_data (employee_id, data_diaria),
    CONSTRAINT fk_diaria_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


SET FOREIGN_KEY_CHECKS = 1;
