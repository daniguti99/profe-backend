-- Script SQL para registrar las descargas de sesiones (exportación a PDF).
-- Ejecutar manualmente en MySQL local antes de probar la exportación a PDF.
-- El UNIQUE (id_sesion, id_usuario) es la garantía de que solo cuenta la primera descarga de cada
-- usuario sobre cada sesión; el contador sesion.descargas se incrementa en la primera descarga.

CREATE TABLE IF NOT EXISTS sesion_descarga (
  id_descarga BIGINT NOT NULL AUTO_INCREMENT,
  id_sesion INT NOT NULL,
  id_usuario INT NOT NULL,
  fecha_descarga DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_descarga),
  UNIQUE KEY uq_sesion_usuario_descarga (id_sesion, id_usuario),
  CONSTRAINT fk_sesion_descarga_sesion FOREIGN KEY (id_sesion) REFERENCES sesion (id_sesion) ON DELETE CASCADE,
  CONSTRAINT fk_sesion_descarga_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
