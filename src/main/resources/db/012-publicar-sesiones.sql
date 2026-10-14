-- Script SQL para añadir la visibilidad, la fecha de publicación y los contadores a la tabla sesion.
-- Ejecutar manualmente en MySQL local antes de probar los endpoints de publicación y de sesiones públicas.
-- Las filas existentes quedan con publico=0 (privadas), fecha_publicacion nula y los contadores a 0.

ALTER TABLE sesion ADD COLUMN publico TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE sesion ADD COLUMN fecha_publicacion DATETIME NULL;
ALTER TABLE sesion ADD COLUMN visualizaciones INT NOT NULL DEFAULT 0;
ALTER TABLE sesion ADD COLUMN descargas INT NOT NULL DEFAULT 0;
