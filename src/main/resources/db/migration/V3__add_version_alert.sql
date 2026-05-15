-- V3__add_version_alert.sql
-- Adiciona controle de versão para alterar alertas na aplicação

ALTER TABLE alerts ADD COLUMN version BIGINT NOT NULL DEFAULT 1;