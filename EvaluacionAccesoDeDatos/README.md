# Laboratorio TableView — Catálogo de productos de Papelería Campus

**Autor:** Joshua Donaire  
**Proyecto Maven:** `org.example:EvaluacionAccesoDeDatos:1.0-SNAPSHOT`  
**Repositorio:** https://github.com/RowletBoi/B-squeda-parametrizada-y-CRUD.git

## Descripción

Aplicación de escritorio desarrollada con JavaFX y PostgreSQL para visualizar y gestionar el catálogo de productos de la papelería Campus. La interfaz permite listar productos en un `TableView`, registrar, actualizar y eliminar registros, seleccionar filas para cargar sus datos en el formulario y ejecutar filtros predefinidos y un filtro de existencias. La persistencia se realiza con JDBC y consultas SQL.

## Tecnologías y dependencias

| Componente | Versión declarada o proporcionada |
|---|---|
| JDK utilizado | Java 26 |
| JavaFX Controls / FXML | 21.0.6 |
| PostgreSQL JDBC | 42.7.11 |
| Maven Compiler Plugin | 3.13.0 |
| JavaFX Maven Plugin | 0.0.8 |
| JUnit Jupiter API / Engine | 5.12.1 (dependencia de pruebas) |
| Gestión de dependencias | Maven |
| Base de datos | PostgreSQL; versión del servidor pendiente de confirmar |

**Nota sobre compilación:** el `pom.xml` compartido declara `<source>9</source>` y `<target>9</target>`, aunque se usa un JDK 26. Se debe verificar la compatibilidad de esa configuración con el código y `module-info.java` antes de entregar; no se afirma que la compilación haya sido verificada con esta configuración.

## Estructura conocida del proyecto

- `org.example.evaluacionaccesodedatos.HelloApplication`: clase principal que extiende `Application`; en `start(Stage)` carga `productos-view.fxml` con `FXMLLoader`, crea una `Scene` y muestra el `Stage`. El título actual de la ventana es `Hello!`.
- `org.example.evaluacionaccesodedatos.controller.ProductoController`: eventos de la interfaz, carga de tabla, operaciones CRUD, validaciones y filtros.
- `org.example.evaluacionaccesodedatos.model.Productos`: modelo de datos usado por el controlador.
- `org.example.evaluacionaccesodedatos.database.DatabaseConnection`: obtiene conexiones JDBC mediante `DriverManager`.
- `productos-view.fxml`: interfaz con formulario, `TableView`, botones, `ComboBox` de filtros y `CheckBox` para productos con existencias. `HelloApplication` lo busca como recurso relativo a su paquete; queda pendiente confirmar la ubicación física del archivo en `src/main/resources`.

**Flujo actual:** interfaz FXML → `ProductoController` → JDBC (`DatabaseConnection` y `PreparedStatement`) → PostgreSQL → `ResultSet` → instancias de `Productos` → `ObservableList` → `TableView`.

**Pendiente frente a la guía:** la versión compartida mantiene SQL dentro del controlador, en vez de utilizar un DAO (`ProductoDAO`) separado. No se presenta esa separación como implementada.

## Preparación de la base de datos

1. Crear la base de datos `papeleria_campus` en un servidor PostgreSQL local.
2. Conectarse expresamente a esa base de datos (no a la base predeterminada `postgres`).
3. Ejecutar `sql/estructura.sql` para crear el esquema `catalogos`, la tabla **`catalogos.producto`** (singular) y sus restricciones.
4. Ejecutar `sql/datos.sql` para insertar los 12 productos de prueba.
5. Confirmar que el listado inicial tiene 12 registros:

```sql
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM catalogos.producto
ORDER BY codigo;
```

La tabla utiliza `id_producto` (`SERIAL`, clave primaria), `codigo` (`VARCHAR(10)`, obligatorio y único), `nombre` (`VARCHAR(80)`), `categoria` (`VARCHAR(20)`), `precio` (`NUMERIC(10,2)`) y `stock` (`INTEGER`). El script proporcionado también incluye cinco restricciones `CHECK`: código normalizado, longitud del nombre, categoría permitida, precio positivo y stock no negativo.

**Precaución:** no volver a ejecutar los INSERT sobre una tabla con los mismos códigos; se producirá un conflicto `UNIQUE`. Para repetir las pruebas, restaurar los 12 registros iniciales en una base local de pruebas usando los scripts y un procedimiento de restablecimiento documentado; no ejecutar comandos destructivos sobre una base con datos que deban conservarse.

## Configuración JDBC

La implementación compartida usa JDBC con servidor local, puerto 5432 y base `papeleria_campus`:

```text
jdbc:postgresql://localhost:5432/papeleria_campus
```

La clase `DatabaseConnection` utiliza `DriverManager.getConnection(URL, USER, PASSWORD)`. **En el código actual esas credenciales están definidas directamente en la clase; esto debe corregirse antes de publicar el repositorio.** No guardar ni subir contraseñas reales al repositorio. Se recomienda leer usuario, contraseña y URL de variables de entorno (por ejemplo, `DB_URL`, `DB_USER`, `DB_PASSWORD`) o de un archivo local excluido por `.gitignore`, acompañado por un ejemplo sin secretos.

## Compilar y ejecutar

Desde la carpeta raíz que contiene `pom.xml`, con Maven y Java instalados:

```bash
mvn clean javafx:run
```

El `pom.xml` configura la clase principal como:

```text
org.example.evaluacionaccesodedatos/org.example.evaluacionaccesodedatos.HelloApplication
```

La clase principal y la carga del recurso FXML se han confirmado en el código compartido. **No se ha verificado la ejecución del comando**. Si hay errores, comprobar la configuración `<source>/<target>` del compilador, `module-info.java` y que `productos-view.fxml` esté ubicado como recurso dentro del paquete de la aplicación.

## Funciones presentes en el controlador compartido

- Carga de registros desde `catalogos.producto` al `TableView`.
- Alta, actualización y eliminación de productos; `UPDATE` y `DELETE` usan `id_producto`.
- Confirmación antes de eliminar y aviso cuando no hay una fila seleccionada.
- Validaciones de código, nombre, categoría, precio y stock; detección previa de código duplicado en el alta.
- Filtros predefinidos B1–B7 mediante `ComboBox` y condición de existencias `stock > 0` mediante `CheckBox`.

**Limitaciones pendientes de verificar o mejorar:** los filtros del código compartido son predefinidos, no una búsqueda libre por texto con parámetros; no se ha confirmado un contador visual de registros; tras operaciones CRUD se llama a `cargarDatos()`, que puede restablecer la vista sin respetar filtros activos. El método `validarFormulario()` compartido no realiza la misma comprobación previa de duplicados al actualizar; PostgreSQL conserva la restricción `UNIQUE`.

## Pruebas y evidencias

Registrar en `pruebas.md` los casos B1–B7, V1–V10, E1–E4 y el ciclo de CRUD, con entrada, resultado esperado, resultado observado, estado y captura correspondiente. No declarar una prueba aprobada hasta haberla ejecutado. La prueba CRUD usa el producto temporal `X001` y debe finalizar con su eliminación para recuperar los 12 productos iniciales.

Guardar en `evidencias/` capturas legibles del listado inicial, filtro combinado, una validación y el ciclo guardar/actualizar/cancelar eliminación/confirmar eliminación. Si hay resultados diferentes de los que exige el laboratorio, documentarlos como tales.

## Entrega y control de versiones

Entregar el proyecto fuente, `pom.xml`, archivos FXML, `sql/estructura.sql`, `sql/datos.sql`, `README.md`, `pruebas.md`, carpeta `evidencias/` y un ejemplo de configuración sin secretos en un ZIP titulado `Donaire_Joshua_LabTableView.zip`, más el enlace al repositorio GitHub. La guía solicita al menos tres commits reales no vacíos y acceso de lectura al docente; estos requisitos aún deben verificarse.

## Fuentes y herramientas de apoyo

- Guía del laboratorio «Acceso a datos y visualización en TableView — Catálogo de productos de la papelería Campus».
- Documentación de JavaFX/OpenJFX, PostgreSQL y pgJDBC, como referencias técnicas sugeridas por la guía.
- Asistencia de ChatGPT para revisión del código y redacción de documentación; el autor debe revisar, probar y validar el resultado.

## Datos por confirmar para cerrar este README

- Versión instalada del servidor PostgreSQL.
- Ubicación física de `productos-view.fxml` en los recursos de Maven (el nombre y contenido de `HelloApplication` ya están confirmados).
- Configuración externa de credenciales y archivo de ejemplo, después de implementarla.
- Resultado real de `mvn clean javafx:run`.
- Evidencias de ejecución, pruebas y commits exigidos.
