# Work Item Handler para la consulta de atributos de un recurso ``Appointment``
Este work item handler se ha generado utilizando el arquetipo maven ``org.jbp.jbpm-workitems-archetype`` en su versión ``7.74.1.Final``

Se usa como ejemplo de work item handler en la asignatura Arquitecturas de servicios sanitarios

Recibe como parámetro de entrada la URL de un recurso ``Appointment`` en un servidor FHIR, versión R5, y devuelve el médico y paciente de dicha cita

Para las pruebas se ha insertado un recurso ``Appointment`` en el servidor de test público de hapi fhir ``https://hapi.fhir.org/baseR5/Appointment/773551``
