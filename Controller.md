# 🏆 Guía Nivel Dios: Listar, Crear y Guardar (Lab 2 + Lab 3)

¡Tienes toda la razón! Vamos a hacer esta guía **completa y a prueba de balas**. Esta guía lo tiene TODO. Desde cero hasta tener un registro funcionando con base de datos.
Usaremos el archivo `practica_avanzada_db.sql` que tiene las tablas `marca` y `celular`.

## 1. El truco del `localhost:8080` (La Ruta Raíz)
Si quieres que al entrar a `localhost:8080` te lleve directo a tu sistema sin escribir `/celulares/lista`, solo crea este pequeño controlador.

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "redirect:/celulares/lista"; // Redirecciona automáticamente al usuario
    }
}
```

---

## 2. Resolución Paso a Paso: Tienda de Celulares

### Paso 1: Los Entities (El Molde)
Vamos a mapear nuestras tablas. Recuerda usar `@Getter` y `@Setter` de Lombok para ahorrar tiempo (cero getters y setters manuales).

**A. `Marca.java`**
```java
package com.example.laboratorio3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "marca")
public class Marca {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Significa que es AUTO_INCREMENT en la DB
    @Column(name = "id_marca") // OBLIGATORIO: Porque tu variable aquí se llama "id" y no "id_marca"
    private Integer id;

    private String nombre; // No necesita @Column porque se llama exactamente igual en MySQL
}
```

**B. `Celular.java`**
```java
package com.example.laboratorio3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "celular")
public class Celular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_celular")
    private Integer id;

    private String modelo;
    private Double precio;
    private String color;

    // --- LA RELACIÓN CON MARCA ---
    // MUCHOS celulares pertenecen a UNA marca.
    @ManyToOne
    @JoinColumn(name = "id_marca") // El nombre exacto de la llave foránea en MySQL
    private Marca marca; 
}
```

### Paso 2: Los Repositories (Los Almaceneros)
Creamos las interfaces para que Spring haga las consultas (SQL) por nosotros mágicamente.

**A. `MarcaRepository.java`**
```java
package com.example.laboratorio3.repository;

import com.example.laboratorio3.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Integer> {
    // Heredamos de JpaRepository para ganar el findAll(), save(), findById(), etc.
}
```

**B. `CelularRepository.java`**
```java
package com.example.laboratorio3.repository;

import com.example.laboratorio3.entity.Celular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CelularRepository extends JpaRepository<Celular, Integer> {
}
```

### Paso 3: El Controller (El Mesero)
Este es el cerebro. Tendrá 3 funciones: listar celulares, mostrar el formulario vacío, y atrapar los datos para guardarlos.

```java
package com.example.laboratorio3.controller;

import com.example.laboratorio3.entity.Celular;
import com.example.laboratorio3.repository.CelularRepository;
import com.example.laboratorio3.repository.MarcaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/celulares")
public class CelularController {

    // 1. INYECCIÓN DE DEPENDENCIAS: El Controller (Mesero) necesita acceso a los dos almacenes
    final CelularRepository celularRepository;
    final MarcaRepository marcaRepository; 

    public CelularController(CelularRepository celularRepository, MarcaRepository marcaRepository) {
        this.celularRepository = celularRepository;
        this.marcaRepository = marcaRepository;
    }

    // 2. LISTAR TODOS (Método GET)
    @GetMapping("/lista")
    public String listar(Model model) {
        // Ponemos la lista de la BD en la bandeja del Model para que el HTML la dibuje
        model.addAttribute("listaCelulares", celularRepository.findAll());
        return "listaCelulares"; 
    }

    // 3. MOSTRAR EL FORMULARIO VACÍO (Método GET)
    @GetMapping("/nuevo")
    public String nuevoCelular(Model model) {
        // Para que el usuario elija la marca en un combo-box, debemos mandarle la lista de marcas
        model.addAttribute("listaMarcas", marcaRepository.findAll());
        return "formCelular";
    }

    // 4. RECIBIR DATOS DEL FORMULARIO Y GUARDAR (Método POST)
    @PostMapping("/guardar")
    public String guardarCelular(Celular celular, RedirectAttributes attr) {
        // ¡LA MAGIA DE SPRING! Él arma el objeto 'Celular' con lo que el usuario escribió en el HTML.
        // Nosotros solo le decimos al repositorio: ¡Guárdalo en MySQL!
        celularRepository.save(celular);
        
        // El flash attribute sobrevive a la redirección para mostrar el mensaje de éxito verde
        attr.addFlashAttribute("mensaje", "¡Celular registrado con éxito!");
        
        // Redireccionamos a la lista, NO a un html (Patrón Post-Redirect-Get)
        return "redirect:/celulares/lista"; 
    }
}
```

### Paso 4: Las Vistas HTML (Thymeleaf)

**A. `listaCelulares.html`** (Mostrar la tabla)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Catálogo de Celulares</title>
</head>
<body>
    <h1>Catálogo de Celulares</h1>
    
    <!-- Mensaje de éxito del Flash Attribute -->
    <div th:if="${mensaje != null}" style="color: green; font-weight: bold;">
        <span th:text="${mensaje}"></span>
    </div>

    <!-- Botón que llama a la ruta del formulario (GET /celulares/nuevo) -->
    <a href="/celulares/nuevo">
        <button>Registrar Nuevo Celular</button>
    </a>
    <br><br>

    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Modelo</th>
                <th>Color</th>
                <th>Precio</th>
                <th>Marca</th>
            </tr>
        </thead>
        <tbody>
            <!-- El clásico th:each para recorrer la lista de celulares -->
            <tr th:each="cel : ${listaCelulares}">
                <td th:text="${cel.id}"></td>
                <td th:text="${cel.modelo}"></td>
                <td th:text="${cel.color}"></td>
                <td th:text="${cel.precio}"></td>
                
                <!-- Así accedemos a la tabla relacionada -->
                <td th:text="${cel.marca.nombre}"></td> 
            </tr>
        </tbody>
    </table>
</body>
</html>
```

**B. `formCelular.html`** (El Formulario de Registro)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Registrar Celular</title>
</head>
<body>
    <h1>Nuevo Celular</h1>

    <!-- 
      El 'action' apunta a la ruta @PostMapping del Controller (/celulares/guardar).
      El método debe ser POST obligatoriamente.
    -->
    <form method="post" action="/celulares/guardar">
        
        <!-- ¡REGLA DE ORO! El atributo 'name' debe llamarse IGUAL que en tu clase Celular.java -->
        
        <label>Modelo:</label>
        <input type="text" name="modelo" required><br><br>
        
        <label>Color:</label>
        <input type="text" name="color" required><br><br>

        <label>Precio:</label>
        <input type="number" step="0.01" name="precio" required><br><br>

        <!-- LLAVE FORÁNEA: Un <select> para elegir la marca -->
        <label>Marca:</label>
        <select name="marca" required> 
            <!-- El 'name="marca"' es porque en tu Entity Celular pusiste "private Marca marca;" -->
            
            <!-- Listamos las marcas que mandó el controlador (listaMarcas) -->
            <option th:each="mar : ${listaMarcas}" 
                    th:value="${mar.id}" 
                    th:text="${mar.nombre}">
            </option>
        </select><br><br>

        <button type="submit">Guardar Celular</button>
    </form>
</body>
</html>
```

### 🧠 El Secreto de los Formularios (El porqué)
El secreto para que el `repository.save(celular)` funcione a la perfección es que los **`name="xxx"`** de los `input` del formulario HTML (`formCelular.html`) se llamen **exactamente igual** a las variables que declaraste en tu clase `Celular.java`. Si el input tiene `name="precio"`, Spring lo inyectará directamente en la variable `private Double precio;`. ¡Tú solo confía en ese proceso y el código fluirá solo!
