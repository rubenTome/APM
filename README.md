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
- **Sistema de identificación o login. Permitirá identificar al usuario univocamente. Login con cuentas de Google** _Coordinación entre CEO y COO para crear un modelo simple y funcional_
- **Creación de cada uno de los eventos sobre un mapa, fijando la localización en este. Del mismo modo, en esta creación se deben poder fijar otras opciones como: horario, capacidad máxima, equipamiente requerido, formato (casual o torneo). Una vez ceado el evento, el resto de usuarios podrán visualizarlo e incribirse si lo desean.** _Funcionalidad crítica y amplia por lo tanto se comparte por todo el equipo, enfasis del CTO en geolocalización_
- **Edición de formato del evento creado (casuales y torneo).** _Tarea coordinada entre todos los integrantes del grupo_
- **Sistema de registro de resultados y estadísticas.** _Coordinación entre COO y CXO_
- **Sistema de ranking de usuario y de equipos.** _Coordinación entre COO y CXO_
- **Sistema de recompensas y accesorios a "comprar". (Método de fidelización).** _Tarea compartida por todo el grupo_
### No críticas
- **Sistema de notificación de eventos próximos a localización actual.** _Tarea coordinada por CEO_
- **Sistema de captación de datos de dispositivos wearables (km recorridos, pasos, calorías) (Google Fit API)** _Tarea coordinada por CTO_
- **Módulo de información sobre el clima en horas próximas al evento.** _Tarea coordinada por CTO_
- **Capacidad de compartir en redes sociales.** _Tarea coordinda por COO_

## Target
El usuario objetivo estaría en una franja de edad comprendida entre los 12 y los 30 años. Dado que se tratan de usuarios bastante jóvenes, el status económico no sería demasiado alto, por lo que la aplicación buscará adaptarse a estas condiciones y ser y proporcionar en su mayoría servicios de forma gratuita. La aplicación no realizará ningún otro sesgo respecto al target esperado, cuestiones como el género son intrascendetes pues al final el usuario final se identificará unicamente por su gusto por el deporte, en especial deportes de equipo. Estas personas estarían buscando que la aplicación facilite poder organizar encuentros casuales con otras personas para practicar deporte sin necesidad previa de conocerse. 
Respecto al mercado, sería más bien de nicho. Debido a la franja de edad en la que la aplicación se desarolla, no se espera una cantidad de usuarios masificada. Por lo tanto, el diseño de la aplicación no tiene que estar orientado a cubrir una amplia demanda si no más bien en realizar correctamente lo que se propone. Nuestro cliente perfecto inicial, no se reduce a un único usuario, puesto que este no encontraría mucha utilidad en la aplicación, si no que más bien este cliente ideal se representa por un grupo de personas que quieren organizar un evento deportivo, conformando equipos, y que buscan ampliar la participación en este mediante el uso de la app.
## Estudio de mercado
Nuestra aplicación, a diferencia de la gran mayoría disponibles actualmente, no se centra en organizar eventos de forma general, sino en posibilitar encuentros deportivos casuales entre grupos pequeños de personas, y permitir que estas compitan entre ellas en eventos tales como torneos. Para fidelizar a los usuarios, permitiremos crear equipos y formar parte de ellos, además de tener una lista de amigos, por lo tanto, si surge una aplicación similar, el usuario deberá añadir a todos sus contactos y crear de nuevo los equipos, lo cual puede ser tedioso y desmotivará al usuario a la hora de realizar este cambio. Además, se creará un sistema de puntos, que permitirá comprar ítems estéticos asociados a la cuenta (imágenes de avatar,nuevos escudos de equipo...), y por lo tanto si el usuario pretende cambiarse de aplicación, perderá todos estos ítems. Para ganar dichos puntos, deberán comprarse con dinero o ganar en rankings o torneos.

En cuanto a aplicaciones similares en el mercado, no hemos encontrado ninguna semejante en España. Sin embargo, en Reino Unido, debido a la gran importancia que tiene el futbol amateur y la comunmente conocida Sunday League existen una gran variedad de ejemplos sobre los que basarse. (La mayoría de capturas son recogidas directamente de la APP Store debido a las restricciones geográficas a la hora de descargarlas.) 
#### Are you in
![Imagen are you in](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/are_you_in/are_you_in.png)
Esta primer ejemplo muestra la aplicación de Are you In. Como se puede ver en las capturas esta aplicación se centra en la organización de eventos deportivos, a los cuales cualquier usuario se puede inscribir. Estos eventos se le muestran al usario en forma de lista (tercera captura), pudiendo acceder a cada uno de ellos de forma individual (primera captura) para obtener información mas pormenorizada. De esta aplicación, además del esquema de UX, se han extraido ideas como el uso de un sistema de "coins" o puntos que el usuario obtenga y pueda emplear en la aplicación como método de fidelización.

#### TSL
![Imagen TSL](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/TSL/Sin%20nombre.png)
Esta segunda aplicación llamada TSL, ya se centra mucho más en el mercado de _Sunday League_ que se mencionó anteriormente. Como se puede ver mantiene un esquema muy similar al anterior, con una listado de todos los eventos que ocurren o ocurrirán en un futuro. Esta aplicación además cuenta con una pestaña de "Photos" que permite la integración las redes sociales de forma natural. Además hace uso de un calendario, como posible modelo para mantener la usuario informado sobre los eventos.

#### Organidor torneo, crear liga
Esta última aplicación llamada __Organidor torneo, crear liga__ , se encuentra muy próxima en muchas de las funcionalidades que propone a las ideas de nuestro proyecto. Como se puede ver en esta primera captura, los eventos a crear pueden ser de todo tipo, modificando tanto el formato (liga,playoff) como la disciplina.
![Evento Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/crearevento.png)

A la hora de incorporar equipos, esta aplicación incluye una nueva funcionalidad denominda importar. Esta permite "copiar"  a un equipo entero a otro evento sin necesidad de tener que crearlo de nuevo. No obstante en esta aplicación, la inscripción a estos eventos se hace de modo "local", los jugadores o usuarios serán inscritos por el creador del evento y su interacción no irá más allá que un texto. 
![Equipo Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/equipos.png)

Como se puede ver en estas capturas, assets como el escudo se encuentra detrás de un modelo PRO de pago. Este trae otras funcionalidades como las que se muestran en esta captura. Como nuestro proyecto se centra en jóvenes con pocos o nulos ingresos este modelo de negocio puede resultar contraproducente pero se apunta como posible consideración.
![Pro Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/Pro.png)

El modelo de clasificación y estadísticas se asemeja en gran manera a nuestra idea incial, por lo cual se tomará como referencia.
![Pro Organizador](https://github.com/rubenTome/APM/blob/main/estudio_mercado/capturas/Organizador/resultados.png)

##  Diseño de la arquitectura de comunicaciones.
![Diseño](https://github.com/rubenTome/APM/blob/main/dise%C3%B1o_arquitectura/Dise%C3%B1oArqComunicaciones.svg)

## División Componentes

### Actividades
Solo se cuenta con 2 actividades en este proyecto:
- **Actividad de Login:** Pantalla inicial en la que solo se muestra el botón de login con Google.
- **Actividad Principal:** Pantalla principal sobre la que se ejecuta la totalidad de la aplicación, sobre esta se servirán todos los fragments a emplear.
### Fragment 
Nuestra aplicación se va componer mayoritariamente de fragments ya que tenemos tanto una toolbar con un NavDrawer, como un BottomNavigation que nos permitirán navegar entre ellos. 
En el NavDrawer se mostrará un header con la información de usuario y diversas opciones:
- **Home Fragment:** Nos lleva de vuelta a la pantalla principal cerrando el menú.
- **Settings Fragment:** Nos lleva al fragment de los ajustes de la app.
- **Botón de Share:** Botón de compartir la app.
- **About Us:** Boton que redirecciona al Github del proyecto.
- **Log out:** Botón que cerra la sesión de la cuenta abierta en la primera actividad.
Por su parte el BottomNav cuenta con tres opciones:
- **Ranking Fragment:** Fragment que muestra el ranking de los jugadores.
- **Home Fragment:** Nos lleva de vuelta a la pantalla principal del mapa.
- **Calendario Fragment:** Fragment que muestra la lista de torneos en activo.

A continuación se enumeran todos los fragments y su utilidad:
- **Home Fragment**: Fragment principal que cuenta mostrará el mapa y todos los eventos en el. Clickando en estos eventos se puede acceder a su información o formulario de inscripción. Además esta pantalla cuenta un botón en la parte inferior para crear un nuevo evento. En caso de querer ver los eventos en forma de lista, el usuario solo tendrá que arrastrar la pestaña de la sección inferior para acceder al fragment de lista de eventos.
- **Crear Evento:** Fragment que nos muestra un formulario para crear nuestro evento, indicando nombre formato, fecha o capacidad máxima. Se puede acceder o bien desde el botón en el HomeFragment o en el botón del fragment de Lista de Eventos. Se podrá acceder a el desde el mapa al crear mantener presionado y crear un evento en el.
- **Lista de Eventos:** Fragment que muestra la lista de eventos disponibles visibles en el mapa pero mediante un RecyclerView. Solo se puede acceder a el arrastrando la pestaña que se muestra en el HomeFragment, por ahora este es un botón que se cambiará en el futuro.
- **Fragment Inscribirse en Evento Casual:** Fragment accesible desde o bien la lista de eventos o bien el mapa. Este Fragment permite al usuario ver la información del evento junto con los comentarios del administrador. Cuenta con una sección que muestra el tiempo que se espera en ese lugar a la hora del evento. Además cuenta con un botón de como llegar que permite acceder a otro fragment que guie al usuario. 
- **Fragment Inscribirse en Torneo:** Fragment accesible desde o bien la lista de eventos o bien el mapa. La principal diferencia con el fragment anterior es que a la hora de inscribirse no existe un botón que lo haga directamente. En los torneos el administrador ha creado unos equipos en los cuales cada uno de los usuarios se debe inscribir.
- **Fragment Direcciones:** Fragment unicamente accesible con el botón __Como Llegar__ de los fragments de inscripción. Este muestra una ruta desde la ubicación actual del usuario a la localización del torneo
- **Ranking Fragment:** Fragment que muestra el ranking de los jugadores con un RecyclerView. Se accede a el mediante el bottomNavigation. Si se presiona sobre cualquiera de los jugadores del ranking nos lleva a su perfil.
- **Profile Fragment:** Fragment que muestra la información de un usuario. Existen dos maneras de acceder a el o bien por el paso del ranking anterior o bien mediante el botón de perfil de la ToolBar. En caso de acceder desde el botón de la Toolbar la app también muestra un TabLayout superior donde el usuario puede moverse entre este fragment y el de historial de partidos.
- **Fragment Historial:** Fragment que muestra los resultados de los partidos en los que usuario ha participado. Accesible unicamente desde el TabLayout del perfil del usuario de la propia aplicación.
- **Calendario Fragment:** Fragment que muestra la lista de torneos en activo con un RecyclerView solo se puede acceder desde el bottomNavigation. En caso de clickar en cualquiera de los eventos nos lleva al fragment de su información.
- **Fragment Partidos**: Desde el Fragment de calendario si se accede a uno de los eventos este será el fragment que se muestre por defecto. En el se puede ver los partidos de este evento y sus resultados, en caso de ser dueño del evento puede modificar los resultados. Cuenta con un TabLayout en la parte superior para cambiar entre este fragment y el de clasificiación de equipos.
- **Fragment Clasificiación**: Fragment accesible desde el TabLayout anteriormente explicado. Muestra una clasificación de los equipos dentro del torneo, con datos como Partidos Juagos, Victorias, Empates, Derrotas y Diferencia de Goles.

## Tareas Segundo Plano
### Servicios
Los servicios que vamos a implementar en este caso están fuertemente relacionados con las API que vamos a emplear:
- Firebase Auuthentication: Vamos a emplear el modelo de autenticación que nos distribuye Firebase para ello haremos uso del servicio de google **play-services-auth**. Con este podremos conseguir que todos los usuarios que vayan a utilizar la aplicación se inscriban con una cuenta de Google, que podremos gestionar dede Firebase. 
- FireBase RealTimeDatabse: Vamos a emplear la base de datos en tiempo real de Firebase para todos los datos que se van a guardar. 
- Google Maps (Location service): Mediante la API que distribuye Google, vamos a tener en un fragment un mapa activo constantemente sobre el que se desarolla gran parte de nuestra aplicación. Este service **play-services-location**, tras gestionar los permisos de localización, nos permite mantenar un mapa actualizado con eventos antiguos y nuevos por igual. Además en el mapa se mostrará la localización del usuario en todo momento. Se buscará en un futuro ampliar el uso de este mapa y la localización para la creación de rutas de ayuda para saber como llegar a cada uno de los eventos.
### Corrutinas 
En cuanto a corrutinas, para evitar bloquear el Thread principal su uso se va a centrar en los puntos que pueden resultar más "complejos".
- Se buscará que las peticiones a la base de datos de Firebase, sean de escritura o lectura se ejecuten mediante corrutinas evitando sobrecargar el thread principal.
- Cada vez que accedemos a la información de un evento queremos que entre su información se muestre el tiempo que hará ese día. Para ello hacemos uso de OpenWeatherMap. Esta es una API que combinando su uso con RetroFit nos permite hacer consultas directamente desde la APP. Como no queremos esperar a la respuesta de estas consultas para poder abrir la pantalla, estas peticiones GET se han aislado en una corrutina. Esto permite a la aplicación cargar el fragment correspondiente aunque el ImageView y TextViews con los datos no hayan recibido los datos. Así se puede ver que al acceder al Fragment del torneo 1 la información sobre el tiempo aparece unos segundos más tarde. Evitamos por lo tanto con esta petición asíncrona bloquear el main thread.

## Geolocalización
Para localización se ha empleado la API propuesta en clase de Google Maps Platform. Con ella en el fragment principal se han incluido ya un mapa funcional en el que se muestra nuestra ubicación en todo momento tras pedirle los permisos requeridos al usuario. Esta ubicación se actuliza mediante un request cada cierto intervalo de tiempo. En este mismo mapa se han configurado manualmente en este punto unos marcadores que son los que permiten acceder a los eventos. En cuanto estos eventos se guarden en una base de datos se espera poder acceder a ellos mediante llamads y no como ahora que son simples datos "inscrutados" en la aplicación. 
Se ha intententando que este eventos solo aparezcan en pantalla si la distancia a ellos con respecto a la última localización es menor que 1 km pero por ahora no se ha logrado. 
Queda implementar tambien un sistema de rutas gracias al botón como llegar, pero dado que no tenemos eventos guardados en bases de datos con sus ubicaciones se prefiere hacerlo cuando estas estén bien definidas.







