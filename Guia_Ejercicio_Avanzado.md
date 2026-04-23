# 🏆 Guía Nivel Avanzado: Lab 2 + Lab 3 (Listar y Guardar)

¡Empecemos resolviendo tu duda principal!

## 1. ¿Cómo hacer que `localhost:8080` abra de frente mi tabla?

Es muy fácil. Solo debes decirle a Spring: *"Oye, si alguien entra a la ruta vacía (`/`), mándalo de un empujón a la ruta `/juegos/lista`"*. Para eso usamos un `redirect:`.

Crea un controlador rápido (por ejemplo `HomeController.java`) y ponle esto:

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/juegos/lista"; // Esto es como decirle al usuario "vete para allá"
    }
}
```
¡Listo! Ahora si pones `localhost:8080`, automáticamente te llevará a tu lista.

---

## 2. EJERCICIO NIVEL DIOS (Simulación de Lab)
He creado en tu carpeta un archivo llamado **`practica_avanzada_db.sql`**. El contexto es una tienda de Celulares. Necesitas listar los celulares, pero además, **tener un botón que te lleve a un formulario para registrar un celular nuevo en la base de datos**.

### Paso 1: Entities y Repositories
Esto ya lo dominas. Haces tu `Marca.java`, `Celular.java` (con `@ManyToOne` hacia Marca), y sus dos repositorios (`MarcaRepository`, `CelularRepository`).

### Paso 2: El Controller (¡Aquí viene lo nuevo!)
Ahora el mesero no solo sirve comida (GET), sino que también toma notas del cliente y las lleva a la cocina (POST).

```java
@Controller
@RequestMapping("/celulares")
public class CelularController {

    final CelularRepository celularRepository;
    final MarcaRepository marcaRepository; // Necesitamos este para el combo-box del formulario

    public CelularController(CelularRepository celularRepository, MarcaRepository marcaRepository) {
        this.celularRepository = celularRepository;
        this.marcaRepository = marcaRepository;
    }

    // 1. LISTAR (Lo que ya sabes)
    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("listaCelulares", celularRepository.findAll());
        return "listaCelulares"; 
    }

    // 2. MOSTRAR EL FORMULARIO (GET)
    @GetMapping("/nuevo")
    public String nuevoCelular(Model model) {
        // Le mandamos la lista de marcas al formulario para que el usuario pueda elegir en un <select>
        model.addAttribute("listaMarcas", marcaRepository.findAll());
        return "formCelular"; // Nos lleva al archivo HTML del formulario
    }

    // 3. RECIBIR LOS DATOS DEL FORMULARIO Y GUARDAR EN BD (POST)
    @PostMapping("/guardar")
    public String guardarCelular(Celular celular, RedirectAttributes attr) {
        
        // ¡LA MAGIA DE SPRING! Él solito agarra el formulario HTML y lo convierte en un objeto Celular.
        // Solo llamamos al método save() del repositorio y listo, ¡se insertó en la base de datos!
        celularRepository.save(celular);
        
        // Mensaje verde bonito de éxito (Flash Attribute, sobrevive a redirecciones)
        attr.addFlashAttribute("mensaje", "¡Celular registrado con éxito!");
        
        return "redirect:/celulares/lista"; // Después de guardar, lo regresamos a la lista
    }
}
```

### Paso 3: Las Vistas HTML (Thymeleaf)

**A. `listaCelulares.html`** (Ya sabes cómo hacer esto, le agregamos el botón de crear y el mensaje).
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <h1>Catálogo de Celulares</h1>
    
    <!-- Este div solo aparece si el controlador mandó un "mensaje" -->
    <div th:if="${mensaje != null}" style="color: green; font-weight: bold;">
        <span th:text="${mensaje}"></span>
    </div>

    <a href="/celulares/nuevo">Registrar Nuevo Celular</a>
    
    <table border="1">
        <!-- ... (aquí haces tu th:each="cel : ${listaCelulares}") ... -->
    </table>
</body>
</html>
```

**B. `formCelular.html` (EL FORMULARIO)**
Aquí combinamos HTML puro con Thymeleaf.
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <h1>Registrar Celular</h1>

    <!-- El formulario hace POST a la ruta /celulares/guardar -->
    <form method="post" action="/celulares/guardar">
        
        <label>Modelo:</label>
        <input type="text" name="modelo" required><br>
        
        <label>Precio:</label>
        <input type="number" step="0.01" name="precio" required><br>

        <label>Color:</label>
        <input type="text" name="color" required><br>

        <!-- COMBOBOX PARA LA MARCA (Llave Foránea) -->
        <label>Marca:</label>
        <select name="marca"> 
            <!-- El 'name' debe llamarse igual que la variable de la relación en tu clase Celular (@ManyToOne) -->
            <option th:each="mar : ${listaMarcas}" 
                    th:value="${mar.id}" 
                    th:text="${mar.nombre}"></option>
        </select><br>

        <button type="submit">Guardar Celular</button>
    </form>
</body>
</html>
```

### 🧠 ¿Por qué funciona esto mágicamente?
El secreto más grande de Spring Boot está en el `name="xxx"` de los inputs en tu formulario HTML.
Si tu clase `Celular` tiene una variable llamada `precio`, tu input en HTML DEBE tener `name="precio"`. Si tiene la llave foránea llamada `marca`, el select DEBE tener `name="marca"`. 

Al darle al botón "Guardar", Spring mira todos los `name`, arma el objeto `Celular` por ti, y se lo pasa a la función `@PostMapping`. Tú solo le haces `save()` y te vas a dormir. ¡Fácil!
