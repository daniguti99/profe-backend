-- Script SQL para añadir la columna activo a las tablas unidad y sesion.
-- Ejecutar manualmente en MySQL local antes de probar los endpoints de edición y borrado lógico.
-- Las filas existentes quedan con activo=1 (activas) por defecto.

ALTER TABLE unidad ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;
ALTER TABLE sesion ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;
