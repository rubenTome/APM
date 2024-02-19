# Grupo 10 (PanchangAPP).
## Integrantes y roles
- **(CEO) Julián Barcia Facal**
Encargado de coordinación, comunicación, documentación y gestión del proyecto. Realizará testing de integración, al igual que participará en la unificación y creación de módulos da arquitectura.
- **(CTO) Ruben Tome Moure**
Gestionará la geolocalización, y el uso de sensores y API en caso de ser utilizadas.
- **(CXO) David Zambrana Seoane**
Encargado de la arquitectura, distribuirá las tareas a realizar a nivel técnico entre todo los integrantes.
- **(COO)Jerónimo José Pardo Rodríguez**
Centrará o su foco en mantener una correcta UX.

## Funcionalidades 
Este proyecto se centra en el desarrollo de una aplicación que facilite un método para la creación y organización de eventos deportivos. Todos estos eventos se concretarán en un entorno de mapa, donde los usuarios poderán confirmar su asistencia si así lo desean. Una vez terminado cada uno de los eventos, se recogerán estadísticas tanto de resultados de equipo como individuales para poder crear una clasificación correspondiente.

### Críticas
- **Sistema de identificación o login. Permitirá identificar al usuario univocamente.** _Coordinación entre CEO y COO para crear un modelo simple y funcional_
- **Creación de cada uno de los eventos sobre un mapa, fijando la localización en este. Del mismo modo, en esta creación se deben poder fijar otras opciones como: horario, capacidad máxima, equipamiente requerido, formato (casual o torneo). Una vez ceado el evento, el resto de usuarios podrán visualizarlo e incribirse si lo desean.** _Funcionalidad crítica y amplia por lo tanto se comparte por todo el equipo, enfasis del CTO en geolocalización_
- **Diferenciación a la hora de crear eventos casuales y torneos y modelado de ambos tipos de evento.** _Tarea coordinada entre todos los integrantes del grupo_
- **Sistema de registro de resultados y datos.** _Coordinación entre COO y CXO_
- **Sistema de ranking de usuario y de equipos.** _Coordinación entre COO y CXO_
- **Sistema de recompensas. (Método de fidelización).** _Tarea compartida por todo el grupo_
### Non críticas
- **Sistema de notificación de eventos próximos a localización actual.** _Tarea coordinada por CEO_
- **Sistema de captación de datos de sistemas wearables (km percorridos, pasos, calorías)** _Tarea coordinada por CTO_
- **Módulo de información sobre o clima en horas próximas a evento.** _Tarea coordinada por CTO_
- **Capacidade de compartir de redes sociais.** _Tarea coordinda por COO_

## Target
El usuario objetivo estaría en una franja de edad comprendida entre los 12 y los 30 años. Dado que se tratan de usuarios bastante jóvenes, el status económico no sería demasiado alto, por lo que la aplicación buscará ser en su gran mayoría gratuita. La aplicación no realizará ningún otro sesgo respecto a al target esperado, cuestiones como el género son intrascendetes pues al final el usuario final será uno al que le guste el deporte, y en especial deportes de equipo. Estas personas estarían buscando en la aplicación facilidades para poder organizar encuentros casuales con otras personas para practicar deporte sin necesidad previa de conocerse. 
Respecto al mercado, sería más bien de nicho. Debido a la franja de edad en la que la aplicación se desarolla, no se espera una cantidad de usuarios masificada. Por lo tanto, el diseño de la aplicación no tiene que estar orientado a cubrir una amplia demanda si no más bien en realizar correctamente lo que se propone. Nuestro cliente perfecto, no se reduce a un único usuario, puesto que este no encontraría mucha utilidad en la aplicación, si no que más bien este cliente ideal se representa por un grupo de personas que quieren organizar un evento deportivo, conformando equipos, y que buscan ampliar la participación en este mediante el uso de la app.
## Estudio de mercado
Nuestra aplicación, a diferencia de la gran mayoría disponibles actualmente, no se centra en organizar eventos de forma general, sino en posibilitar encuentros deportivos casuales entre grupos pequeños de personas, y permitir que estas compitan entre ellas en eventos tales como torneos. Para fidelizar a los usuarios, permitiremos crear equipos y formar parte de ellos, además de tener una lista de amigos, por lo tanto, si surge una aplicación similar, el usuario deberá añadir a todos sus contactos y crear de nuevo los equipos, lo cual puede ser tedioso y desmotivará al usuario a la hora de realizar este cambio. Además, se creará un sistema de puntos, que permitirá comprar ítems estéticos asociados a la cuenta (imágenes de avatar,nuevos escudos de equipo...), y por lo tanto si el usuario pretende cambiarse de aplicación, perderá todos estos ítems. Para ganar dichos puntos, deberán comprarse con dinero o ganar en rankings o torneos.

En cuanto a aplicaciones similares en el mercado, no hemos encontrado ninguna empleable en España. Sin embargo, en Reino Unido, debido a la gran importancia que tiene el futbol amateur y la comunmente conocida Sunday League existen una gran variedad de ejemplos sobre los que basarse. (La mayoría de capturas son recogidas directamente de la APP Store debido a las restricciones geográficas a la hora de descargarlas.) 
#### Are you in
![Imagen are you in](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/are_you_in/are_you_in.png)
Esta primer ejemplo muestra la aplicación de Are you In. Como se puede ver en las capturas esta aplicación se centra en la organización de eventos deportivos, a los cuales cualquier usuario de puede inscribir. Estos eventos se le muestran al usario en forma de lista (tercera captura), pudiendo acceder a cada uno de ellos de forma individual (primera captura) para obtener información mas pormenorizada. De esta aplicación, además del esquema de UX, se han extraido ideas como el uso de un sistema de "coins" o puntos que el usuario obtenga y pueda emplear en la aplicación como método de fidelización.

#### TSL
![Imagen TSL](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/TSL/Sin%20nombre.png)
Esta segunda aplicación llamada TSL, ya se centra mucho más en el mercado de _Sunday League_ que se mencionó anteriormente. Como se puede ver mantiene un esquema muy similar al anterior, con una listado de todos los eventos que ocurren o ocurrirán en un futuro. Esta aplicación además cuenta con una pestaña de "Photos" que permite la integración las redes sociales de forma natural. Además hace uso de un calendario, como posible modelo para mantener la usuario informado sobre los eventos.

#### Organidor torneo, crear liga
Esta última aplicación llamada __Organidor torneo, crear liga__ , se encuentra muy próxima en muchas de las funcionalidades que propone. Como se puede ver en esta primera captura, los eventos a crear pueden ser de todo tipo, modificando tanto el formato (liga,playoff) como la disciplina.
![Evento Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/crearevento.png)

A la hora de incorporar equipos, esta aplicación incluye una nueva funcionalidad denominda importar. Esta permite "copiar"  a un equipo entero a otro evento sin necesidad de tener que crearlo de nuevo. 
![Equipo Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/equipos.png)

Como se puede ver en estas capturas, assets como el escudo se encuentra detrás de un modelo PRO de pago. Este trae otras funcionalidades como las que se muestran en esta captura. Como nuestro proyecto se centra en jóvenes con pocos o nulos ingresos este modelo de negocio puede resultar contraproducente pero se apunta como posible consideración.
![Pro Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/Pro.png)

El modelo de clasificación y estadísticas se asemejar en gran manera a nuestra idea incial, por lo cual se tomará como referencia.
![Pro Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/resultados.png)






