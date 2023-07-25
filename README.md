# Sistema de Gestión de Estudiantes

Este es un proyecto de software que implementa un sistema de gestión de estudiantes en Java utilizando Swing para la interfaz gráfica y una base de datos MySQL para almacenar la información de los estudiantes. El sistema permite realizar operaciones como agregar nuevos estudiantes, editar información existente, eliminar estudiantes y cargar imágenes de perfil para cada estudiante.

## Requisitos

Antes de compilar y ejecutar este proyecto, asegúrate de tener instalados los siguientes componentes:

1. Java Development Kit (JDK): Versión 8 o superior.
2. MySQL: Una instancia de la base de datos MySQL configurada y en funcionamiento.

## Configuración de la base de datos

1. Crea una base de datos en MySQL con el nombre "card_bd1".

2. Ejecuta el siguiente script SQL para crear la tabla "estu_table1" que almacenará la información de los estudiantes:

```sql
CREATE TABLE estu_table1 (
    codigo_estu VARCHAR(10) PRIMARY KEY,
    nombre_estu VARCHAR(50) NOT NULL,
    apellidos_estu VARCHAR(50) NOT NULL,
    facultad_estu VARCHAR(30) NOT NULL,
    carrera_estu VARCHAR(30) NOT NULL,
    foto_estu LONGBLOB
);
```

## Compilación y Ejecución

Sigue estos pasos para compilar y ejecutar el proyecto:

1. Abre una terminal o línea de comandos.

2. Navega al directorio raíz del proyecto donde se encuentra el archivo "InterfazTabla.java".

3. Compila el proyecto usando el siguiente comando:

```bash
javac InterfazTabla.java
```

4. Ejecuta la aplicación con el siguiente comando:

```bash
java InterfazTabla
```

## Uso del Sistema

Una vez que la aplicación se esté ejecutando, podrás ver la interfaz gráfica con la tabla de estudiantes. Puedes realizar las siguientes acciones:

- **Agregar estudiante:** Haz clic en el botón "Crear" para agregar un nuevo estudiante. Ingresa los detalles del estudiante y, si lo deseas, sube una foto de perfil.

- **Editar estudiante:** Haz clic en el botón "Editar" de una fila para editar los detalles de un estudiante existente.

- **Eliminar estudiante:** Haz clic en el botón "Eliminar" de una fila para eliminar un estudiante de la base de datos y la tabla.

## Contribuciones

Si deseas contribuir a este proyecto, eres bienvenido(a) a enviar pull requests o informar sobre problemas (issues) en GitHub.

Espero que este sistema de gestión de estudiantes te sea útil. Si tienes alguna pregunta o comentario, no dudes en contactarme.

¡Gracias por tu interés en mi proyecto!

Mayta Quispe Marco Fidel