# Work Item Handler para la consulta de atributos de un recurso ``Appointment``
## Desarrollo
Este work item handler se ha generado utilizando el arquetipo maven ``org.jbp.jbpm-workitems-archetype`` en su versión ``7.74.1.Final``

Necesita jdk11, por lo que la variable ``JAVA_HOME`` debe referenciar la ubicación de este jdk, por ejemplo en windows se puede usar ``$env:JAVA_HOME='C:\Program Files\Java\jdk-11.0.4'``

## Funcionalidad

Se usa como ejemplo de work item handler en la asignatura Arquitecturas de servicios sanitarios

Recibe como parámetro de entrada la URL de un recurso ``Appointment`` en un servidor FHIR, versión R5, y devuelve el médico y paciente de dicha cita

## Verificación

Para las pruebas se ha creado un recurso ``Appointment`` en el servidor de test público de hapi fhir.``https://hapi.fhir.org/baseR5/Appointment/773551`` El fichero ``Appointment`` muestra una representación json de dicho recurso.

Además en la carpeta resources se presentan varias representaciones de recursos en ficheros .json, que pueden ser de ayuda para ejecutar el ejercicio propuesto, y un fichero docker compose que permite arrancar un servidor FHIR R5 en un contenedor docker. 

Así, al ejecutar en modo administrador ``docker compose up -d`` se levantará un contenedor y el servidor FHIR estará disponible en la url ``http://localhost:8888/fhir``

## Ejercicio propuesto
Haga un fork de este repositorio en su espacio GitHub

Clone el repositorio localmente

Levante el servidor FHIR R5 en su equipo, usando el docker compose proporcionado

Cree los recursos referenciados en la representación ``Appointment.json`` proporcionada en la carpeta ``resources`` de los tests. En esta carpeta se le dan también diferentes ejemplos de representaciones de estos recursos, para que le resulte más sencillo, en ficheros con extensión .json. Sustituya todas las representaciones por las que le devuelve su servidor una vez creados los recursos.

Cree el ``Appointment`` (y sustituya ``Appointment.json`` por la representación obtenida de su servidor)

Modifique el test unidad para que en lugar de utilizar el servidor hapi fhir online use su servidor local

Ejecute y verifique que se supera el test, si no es así corríjalo hasta que los test se superen

Cree una nueva versión con la etiqueta ``EntregaP2`` y súbala a su fork en Github
