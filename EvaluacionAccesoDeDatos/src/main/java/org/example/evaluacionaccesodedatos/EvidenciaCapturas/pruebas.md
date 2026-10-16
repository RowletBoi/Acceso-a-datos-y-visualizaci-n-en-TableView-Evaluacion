# Registro de pruebas — Laboratorio TableView y CRUD

**Estudiante:** Joshua Donaire  
**Proyecto:** EvaluacionAccesoDeDatos — Papelería Campus  
**Tecnologías:** Java 26, JavaFX 21.0.6, PostgreSQL y Maven  
**Base de datos:** `papeleria_campus`  
**Tabla:** `catalogos.producto`  
**Fecha de documentación:** 16 de octubre de 2026

## 1. Objetivo

Registrar entradas, resultados esperados, resultados observados, estado y evidencias del laboratorio de acceso a datos y visualización en TableView. **Una captura acredita únicamente lo que muestra.** Las pruebas sin evidencia o resultado comunicado se mantienen pendientes, no se consideran aprobadas.

## 2. Evidencias adjuntas

| Archivo | Contenido visible | Alcance |
|---|---|---|
| `1_ListadoInicial.png` | TableView con las 12 filas iniciales y seis columnas | Carga inicial; no se ve un contador específico |
| `2_FiltroCombinado.png` | Selección B5 y filas T001 y T003 | Resultado B5; casilla de existencias sin marcar; filtros fijos del ComboBox |
| `3_CodigoDuplicado.png` | Aviso «Ya existe un producto con ese código» al introducir P001 | Rechazo visible de duplicado en creación |
| `4_ProductoGuardado.png` | Aviso «Producto guardado» al registrar P005 | Confirmación visual de INSERT, complementada por la captura 6 |
| `5_ProductoGuardado_Repetida.png` | Segunda imagen del mismo aviso de guardado | **No** acredita actualización |
| `9_ActualizarProducto_Antes.png` | Selección de T004, Cable HDMI, con stock 0 y nombre original | Estado anterior a actualización; no es producto temporal X001 |
| `10_ActualizarProducto_Confirmacion.png` | Formulario con nombre Cable HDMI 2.0, stock 4 y alerta «Producto actualizado» | La aplicación comunica éxito; la tabla de fondo todavía muestra los valores anteriores |
| `11_ActualizarProducto_Despues.png` | Fila T004 con nombre Cable HDMI 2.0 y stock 4 | Actualización visible en la tabla después de cerrar la alerta |
| `6_ConfirmarEliminacion.png` | Diálogo de confirmación para P005 y fila P005 visible en la tabla | Confirmación ofrecida y existencia de P005 antes de eliminar |
| `7_ProductoEliminado.png` | Aviso «Producto eliminado»; la tabla del fondo aún muestra P005 | Confirmación del comando; la tabla aún no aparece recargada |
| `8_ListadoTrasEliminacion.png` | Aviso de carga y tabla con los 12 productos originales, sin P005 | Resultado visual posterior a la eliminación |

## 3. Carga inicial y TableView

| Entrada o acción | Resultado esperado | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|
| Abrir aplicación conectada a PostgreSQL | Mostrar 12 registros iniciales, columnas ID, código, nombre, categoría, precio y stock | Se visualizan los 12 productos con las seis columnas | Evidenciado parcialmente: falta contador visible, orden por código y persistencia al reiniciar | `1_ListadoInicial.png` |

## 4. Búsqueda y filtros — B1 a B7

| Caso | Entrada exigida por la guía | Resultado esperado | Resultado obtenido / observación | Estado | Evidencia |
|---|---|---|---|---|---|
| B1 | Texto `CUADERNO`, sin otros filtros | P001 y P002; contador 2 | No proporcionado | Pendiente | — |
| B2 | Texto `T00`, sin otros filtros | T001–T004; contador 4 | No proporcionado | Pendiente | — |
| B3 | Categoría Papelería y texto vacío | P001–P004; contador 4 | No proporcionado | Pendiente | — |
| B4 | Solo con existencias y categoría Todas | 9 filas; se excluyen P004, T004, A003 | No proporcionado | Pendiente | — |
| B5 | Texto `USB`, Tecnología, solo con existencias | T001 y T003; contador 2 | Se visualizan T001 y T003 con stock 8 y 4. **No se observa la casilla marcada ni un contador**; se usó la opción fija B5 del ComboBox | Coincidencia de filas evidenciada; prueba exacta pendiente | `2_FiltroCombinado.png` |
| B6 | Texto `ZZZ999` | Sin filas; contador 0 y aviso sin coincidencias | No proporcionado | Pendiente | — |
| B7 | Limpiar filtros después de B5 | Búsqueda vacía, Todas, casilla desmarcada, 12 filas | Hay captura de 12 filas tras eliminación, pero no evidencia de haber ejecutado B7 | Pendiente | — |

**Observación:** La guía requiere búsqueda por texto libre, categoría y existencias combinables mediante SQL parametrizado. Las opciones B1–B7 predefinidas del ComboBox no prueban completamente ese requisito.

## 5. Ciclo CRUD

| Operación | Entrada / acción | Resultado esperado | Resultado observado | Estado | Evidencia |
|---|---|---|---|---|---|
| Crear, prueba de funcionamiento | Código P005; nombre Razer Deathadder V3; categoría Tecnología; precio 90.00; stock 4 | Registrar y mostrar nuevo producto | Alerta de guardado y aparición posterior de P005 como fila 13 | Evidenciado visualmente | `4_ProductoGuardado.png`, `6_ConfirmarEliminacion.png` |
| Crear, caso exacto exigido | X001; Producto de prueba; Accesorios; 10.00; stock 1 | 13 filas; persistencia al reiniciar | No proporcionado | Pendiente | — |
| Actualizar, prueba de funcionamiento | Seleccionar T004 (Cable HDMI), cambiar nombre a Cable HDMI 2.0 y stock de 0 a 4; mantener precio 180.00 | Modificar únicamente el producto seleccionado y mostrar nuevos valores | Se ve la selección inicial, la alerta de actualización exitosa y la fila T004 con nombre y stock nuevos. No se demuestra persistencia al reiniciar | Evidenciado visualmente | `9_ActualizarProducto_Antes.png`, `10_ActualizarProducto_Confirmacion.png`, `11_ActualizarProducto_Despues.png` |
| Actualizar, caso exacto exigido | Seleccionar X001; cambiar precio a 15.50 y stock a 3 | Solo X001 cambia y sigue habiendo 13 filas | No se aportó ese caso exacto; se probó T004 en cambio | Pendiente | — |
| Cancelar eliminación | Seleccionar producto temporal, pulsar Eliminar y luego Cancelar | El producto permanece | Se ve el diálogo de confirmación, pero no el resultado posterior a Cancelar | Pendiente | `6_ConfirmarEliminacion.png` (diálogo solamente) |
| Confirmar eliminación | Confirmar eliminación de P005 | P005 desaparece y quedan 12 filas | Aviso de eliminado, seguido de captura con los 12 originales y sin P005 | Evidenciado visualmente | `7_ProductoEliminado.png`, `8_ListadoTrasEliminacion.png` |
| Persistencia | Reiniciar y verificar con `SELECT` | Los cambios persisten según operación | No proporcionado | Pendiente | — |

## Evidencia detallada de actualización (capturas 9, 10 y 11)

**Antes de actualizar — captura 9:** T004 figura como **Cable HDMI**, precio C$180.00 y stock **0**.

![9 — Antes de actualizar T004](EvidenciaCapturas/9_ActualizarProducto_Antes.png)

**Confirmación — captura 10:** el formulario presenta **Cable HDMI 2.0**, stock **4**, y aparece la alerta «Producto actualizado».

![10 — Confirmación de actualización](EvidenciaCapturas/10_ActualizarProducto_Confirmacion.png)

**Después de actualizar — captura 11:** el TableView muestra T004 con **Cable HDMI 2.0**, stock **4** y precio C$180.00. Esto evidencia el cambio en la tabla, aunque no confirma por sí solo la persistencia después de reiniciar.

![11 — Producto actualizado en TableView](EvidenciaCapturas/11_ActualizarProducto_Despues.png)

## 6. Validaciones V1 a V10

| Caso | Entrada de prueba | Resultado esperado | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|---|
| V1 | Código vacío, espacios o 11 caracteres | Rechazo | No se aportó prueba capturada | Pendiente | — |
| V2 | Nombre con espacios, `AB` o 81 caracteres | Rechazo | No se aportó prueba capturada | Pendiente | — |
| V3 | Categoría no seleccionada o no permitida | Rechazo | No se aportó prueba capturada | Pendiente | — |
| V4 | Precio `abc` | Rechazo sin cerrar la app | No se aportó prueba capturada | Pendiente | — |
| V5 | Precio `0` o `-1` | Rechazo | No se aportó prueba capturada | Pendiente | — |
| V6 | Precio `12.345` o `100000000.00` | Rechazo sin redondeo silencioso | No se aportó prueba capturada | Pendiente | — |
| V7 | Stock `abc` o `2.5` | Rechazo | No se aportó prueba capturada | Pendiente | — |
| V8 | Stock `-1`, `2147483648`, `0` | Rechazo de los dos primeros; admisión de cero | Los datos iniciales incluyen stock 0, pero no se documentó toda la prueba | Pendiente | `1_ListadoInicial.png` (solo evidencia de datos existentes) |
| V9 | Guardar `P001` existente | Aviso y rechazo; sin duplicar fila | Alerta «Código duplicado / Producto existente» con P001 en formulario | Evidenciado en creación; otros subcasos V9 pendientes | `3_CodigoDuplicado.png` |
| V10 | Actualizar o eliminar sin seleccionar fila | Aviso y bloqueo | No se aportó prueba capturada | Pendiente | — |

**Nota:** Cada grupo V se califica cuando pasan todos sus subcasos aplicables tanto en creación como en actualización. El único subcaso visible de V9 no acredita por sí solo el grupo completo.

## 7. Errores y consistencia E1–E4

| Caso | Acción requerida | Resultado esperado | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|---|
| E1 | Puerto de conexión incorrecto en configuración de prueba | Aviso comprensible sin cierre ni falsa lista vacía | No proporcionado | Pendiente | — |
| E2 | Duplicidad o CHECK provocado directamente desde DAO, sin validación de UI | Gestionar SQLException manteniendo integridad | La alerta V9 es de la interfaz, por lo que **no reemplaza E2** | Pendiente | — |
| E3 | Actualizar y eliminar registros previamente borrados desde PostgreSQL | Cero filas afectadas sin aviso falso de éxito | No proporcionado | Pendiente | — |
| E4 | Provocar error SQL | Registrar operación y SQLState; mensaje seguro | No proporcionado | Pendiente | — |

## 8. Aspectos pendientes para completar la entrega

- Confirmar B1–B7 utilizando filtros parametrizados y registrar contadores de resultados.
- Completar prueba exacta de creación/actualización/eliminación de `X001`, incluyendo Cancelar. La prueba de actualización de T004 sí está evidenciada, pero no sustituye la de X001.
- Comprobar persistencia en PostgreSQL con `SELECT` y después de reiniciar.
- Ejecutar y documentar todos los subcasos V1–V10, incluyendo rechazos al actualizar.
- Ejecutar E1–E4 en una base local de pruebas y dejar constancia de fallos si aparecen.
- Verificar que los archivos SQL, README, configuración sin credenciales y repositorio estén incluidos en la entrega.

## 9. Referencias de imágenes

Las imágenes están en la carpeta `EvidenciaCapturas/`, al mismo nivel que este archivo. Por ejemplo:

![Listado inicial](EvidenciaCapturas/1_ListadoInicial.png)

![Filtro B5](EvidenciaCapturas/2_FiltroCombinado.png)

![Código duplicado](EvidenciaCapturas/3_CodigoDuplicado.png)

![Registro de producto](EvidenciaCapturas/4_ProductoGuardado.png)

![Antes de actualizar T004](EvidenciaCapturas/9_ActualizarProducto_Antes.png)

![Confirmación de actualización de T004](EvidenciaCapturas/10_ActualizarProducto_Confirmacion.png)

![T004 actualizado en la tabla](EvidenciaCapturas/11_ActualizarProducto_Despues.png)

![Confirmación de eliminación](EvidenciaCapturas/6_ConfirmarEliminacion.png)

![Eliminación comunicada](EvidenciaCapturas/7_ProductoEliminado.png)

![Listado tras eliminar](EvidenciaCapturas/8_ListadoTrasEliminacion.png)
