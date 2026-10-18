-- Script SQL para eliminar los tres campos "Gráfico" de la tabla sesion.
-- Ejecutar manualmente en MySQL local antes de probar la creación, la edición y el detalle de sesiones.
-- Los valores de URL que hubiera en esas columnas se descartan (dato sin uso).
-- Se recomienda un respaldo manual previo de la tabla si se quieren conservar.

ALTER TABLE sesion DROP COLUMN calentamiento_grafica_url;
ALTER TABLE sesion DROP COLUMN parte_principal_grafica_url;
ALTER TABLE sesion DROP COLUMN vuelta_calma_grafica_url;
