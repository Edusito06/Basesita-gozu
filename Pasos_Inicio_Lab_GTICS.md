# 🚀 Checklist de Supervivencia: Primeros 30 Minutos del Laboratorio

Guarda este archivo abierto durante tu laboratorio. Sigue estos pasos exactamente en este orden y no tendrás problemas de configuración.

---

## FASE 1: La Base de Datos (Minuto 0 al 5)
1. [ ] Descarga el archivo `.sql` y el PDF del laboratorio.
2. [ ] Abre tu programa de bases de datos (Ej: MySQL Workbench).
3. [ ] Abre el archivo `.sql` dentro del programa.
4. [ ] Ejecuta todo el script (suele ser un ícono de un rayito ⚡).
5. [ ] Busca el panel de la izquierda (Schemas) y dale click al botón de **Actualizar/Refresh**.
6. [ ] Asegúrate de que la base de datos apareció en la lista. **Copia o anota exactamente el nombre de esa base de datos** (Ej: `db_teleticket`).

## FASE 2: Abrir el Proyecto en IntelliJ (Minuto 5 al 10)
1. [ ] **Si el profe te da un ZIP:** Descomprímelo, abre IntelliJ -> `File -> Open` y selecciona la carpeta descomprimida.
2. [ ] **Si te pide crearlo desde cero:** Entra a *start.spring.io*, o créalo en IntelliJ usando *Spring Initializr*. Asegúrate de añadir las dependencias: **Spring Web, Spring Data JPA, MySQL Driver, y Thymeleaf**.
3. [ ] **¡PACIENCIA!** Al abrirlo, mira la esquina inferior derecha de IntelliJ. Habrá una barra cargando (Maven está descargando internet entero). **No toques nada hasta que termine de cargar.**

## FASE 3: Conexión (`application.properties`) (Minuto 10 al 15)
1. [ ] Ve a `src/main/resources/application.properties`.
2. [ ] Pega tus datos. Borra todo lo que haya y pega esto (reemplazando los datos):
```properties
# 1. Cambia 'nombre_de_la_bd' por el nombre exacto de la base de datos que creaste en la FASE 1
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_de_la_bd?serverTimezone=America/Lima

# 2. Tu usuario
spring.datasource.username=root

# 3. Tu contraseña (si usas XAMPP déjalo vacío, si usas Workbench suele ser root o la que tú sepas)
spring.datasource.password=root

# 4. Magia de Spring
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
3. [ ] **PRUEBA DE FUEGO:** Dale al botón verde de "Play" arriba a la derecha para correr el proyecto. Si en la consola sale el logo de Spring y al final dice "Started Application in X seconds" sin letras rojas gigantes... **¡Felicidades, la BD está conectada!** Apaga el proyecto (botón rojo cuadrado).

## FASE 4: Mapear las Entidades (Minuto 15 al 25)
No intentes programar nada en HTML todavía. Primero debes mapear las tablas que el PDF te pide usar.
1. [ ] Crea un paquete llamado `entity` (clic derecho en tu paquete principal -> New -> Package).
2. [ ] Por cada tabla que vayas a usar, crea una Java Class.
3. [ ] Ponle la anotación `@Entity` y `@Table(name = "nombre_de_la_tabla_en_bd")`.
4. [ ] Escribe los atributos (columnas).
5. [ ] **Regla de Oro:** Donde veas una llave foránea en tu BD, ahí pones el `@ManyToOne` y `@JoinColumn(name = "nombre_de_columna_fk")`.
6. [ ] Presiona `Alt + Insert` (o clic derecho -> Generate) para crear los Getters y Setters de todo.

## FASE 5: Los Repositorios (Minuto 25 al 30)
1. [ ] Crea un paquete llamado `repository`.
2. [ ] Por cada Entity que creaste, crea una **Interfaz** (New -> Java Class -> selecciona Interface).
3. [ ] Haz que extienda de `JpaRepository`.
   ```java
   @Repository
   public interface ProductoRepository extends JpaRepository<Producto, Integer> {
   }
   ```
   *(Nota: `Integer` es porque el ID de Producto es de tipo Integer).*

---

### ¡LISTO PARA LA ACCIÓN! (Minuto 30+)
A partir de aquí, el proyecto ya funciona. Ahora ya puedes leer el PDF e ir paso a paso.
1. Crea el paquete `controller`.
2. Crea el paquete `templates` (si no existe en resources).
3. Haz tu primer `@GetMapping` para mostrar una lista.
