package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.teachingunit.TeachingUnitDTO;

import java.util.List;

/**
 * Puerto de entrada para obtener las unidades didácticas del docente autenticado.
 *
 * <p>El identificador del usuario no viaja en la URL ni en el cuerpo de la petición: llega desde el
 * token. El adaptador de entrada (el controlador REST) lo extrae del {@code Authentication} que deja
 * preparado el filtro de JWT y lo pasa como argumento. De este modo el caso de uso solo puede
 * devolver las unidades del docente indicado y nunca las de otro (RF-2).
 *
 * <p>Por construcción el método no declara ningún parámetro de ruta ni de cuerpo, así que no existe
 * forma de introducir un usuario ajeno ni de descartar el que llega del token (RF-8).
 *
 * <p>Sobre los tipos del identificador: {@code userId} es {@code Long} porque es el tipo de la
 * identidad del token ({@code User.id}), mientras que el puerto de salida
 * {@code TeachingUnitRepositoryPort.findByUserId} usa {@code Integer} porque es el tipo de
 * {@code TeachingUnit.userId} y de la columna {@code id_usuario}. La conversión entre ambos tipos
 * ocurre en el servicio, que es la única pieza que conoce los dos extremos; ni este puerto ni el
 * adaptador de persistencia dependen del formato concreto de la columna.
 *
 * <p>Devuelve la lista plana de DTO y no la envoltura con el mensaje: construir
 * {@code TeachingUnitListResponseDTO} corresponde al controlador, que es quien conoce el formato de
 * la respuesta HTTP.
 *
 * <p>El caso de uso es una función pura de {@code userId}: no lee el contexto de seguridad
 * ({@code SecurityContextHolder}) ni depende de variables thread-local, de modo que se puede
 * ejercitar en pruebas sin montar contexto alguno.
 */
public interface GetTeachingUnitsUseCase {
    List<TeachingUnitDTO> getTeachingUnits(Long userId);
}