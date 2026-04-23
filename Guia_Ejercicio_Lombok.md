# 🌶️ Lombok y Resolución Paso a Paso del Ejercicio

¡Buena decisión, mi king! Usar Lombok te va a ahorrar muchísimo tiempo en el examen. Vamos a ver cómo funciona y luego resolvemos el ejercicio del inventario de videojuegos usando la lógica correcta.

---

## 1. ¿Qué es Lombok y cómo me salva la vida?
Normalmente, cuando creas una clase en Java (un *Entity*), por cada variable tienes que escribir un método `get()` y un método `set()`. Si tienes 10 columnas en tu tabla, terminas con 60 líneas de código basura que solo estorban la vista.

**Lombok** es una librería que escribe esos getters y setters por ti de manera "invisible" al momento de compilar. 

**¿Cómo se usa?**
Simplemente pones las anotaciones `@Getter` y `@Setter` justo arriba de la declaración de tu clase. ¡Y pum! Mágicamente todas tus variables ya tienen getters y setters sin que escribas nada más.

```java
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Persona {
    private String nombre; 
    // Ya no necesitas escribir public String getNombre() { return nombre; } ...
}
```

---

## 2. Resolución Paso a Paso (Ejercicio Videojuegos)

### Paso 1: Configurar la conexión
Ya lo hicimos en tu `application.properties`, pero para recordar: el porqué de esto es para que Spring sepa a qué puerta de tu base de datos tocar (`gtics_practica_lab`) y con qué llaves de acceso entrar (`root`).

### Paso 2: Crear los Entities (El Molde)
Vamos a crear dos clases en el paquete `entity`. Aquí le decimos a Java cómo son nuestras tablas.

**A. `Categoria.java`**
```java
package com.example.laboratorio3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter // <-- ¡Lombok al rescate!
@Setter // <-- ¡Lombok al rescate!
@Entity
@Table(name = "categoria") // El nombre EXACTO de la tabla en MySQL
public class Categoria {

    @Id // Le dice a Spring "Esta es mi llave primaria"
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Significa AUTO_INCREMENT en BD
    @Column(name = "id_categoria") // Si la columna NO se llama igual que tu variable, lo aclaras.
    private Integer id;

    private String nombre; // Como la columna se llama "nombre", no necesitamos @Column
}
```

**B. `Videojuego.java`** (¡Aquí está el truco de la llave foránea!)
```java
package com.example.laboratorio3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "videojuego")
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_videojuego")
    private Integer id;

    private String titulo;
    
    private Double precio; // Usamos Double para los DECIMAL de MySQL

    // --- LA RELACIÓN ---
    // ¿Por qué ManyToOne? Porque MUCHOS (Many) videojuegos pertenecen a UNA (One) categoría.
    // La regla de oro: donde está la Foreign Key, va el @JoinColumn.
    @ManyToOne
    @JoinColumn(name = "id_categoria") // El nombre de la columna Foreign Key en la tabla videojuego
    private Categoria categoria; // ¡Fíjate que el tipo de dato es Categoria, no Integer!
}
```

### Paso 3: Crear los Repositories (El Almacenero)
En el paquete `repository`, creamos dos **Interfaces**. ¿Por qué? Porque Spring es tan inteligente que si heredas de `JpaRepository`, te regala todos los métodos de búsqueda y guardado (SQL) sin que tú programes nada.

**A. `VideojuegoRepository.java`**
```java
package com.example.laboratorio3.repository;

import com.example.laboratorio3.entity.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Integer> {
    // ¿Por qué Videojuego, Integer? 
    // Videojuego = La tabla que este almacenero va a manejar.
    // Integer = El tipo de dato de su Llave Primaria (@Id).
}
```

### Paso 4: Crear el Controller (El Mesero)
En el paquete `controller`, creamos `VideojuegoController`. Este es el que recibe la petición HTTP de tu navegador y le pide los juegos al repositorio.

```java
package com.example.laboratorio3.controller;

import com.example.laboratorio3.repository.VideojuegoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/juegos") // Todas las rutas de este controller empezarán con /juegos
public class VideojuegoController {

    // "Inyectamos" el repositorio. Es como darle el teléfono del almacén al mesero.
    final VideojuegoRepository videojuegoRepository;

    public VideojuegoController(VideojuegoRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    @GetMapping("/lista") // La ruta final será: localhost:8080/juegos/lista
    public String listarJuegos(Model model) {
        // Le pedimos todos los juegos al repositorio y los ponemos en la "bandeja" (Model)
        model.addAttribute("listaJuegos", videojuegoRepository.findAll());
        
        // Retornamos el nombre del archivo HTML que el mesero debe mostrar
        return "lista"; 
    }
}
```

### Paso 5: La Vista HTML (El Plato Servido)
En la carpeta `src/main/resources/templates`, creas un archivo llamado `lista.html`. Aquí usas Thymeleaf (`th:each`) para recorrer la lista de juegos que mandó el controlador.

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Inventario de Videojuegos</title>
</head>
<body>
    <h1>Lista de Videojuegos</h1>
    
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Título</th>
                <th>Precio</th>
                <th>Categoría</th>
            </tr>
        </thead>
        <tbody>
            <!-- Esto es como un "for" en Java. Por cada 'juego' en 'listaJuegos'... -->
            <tr th:each="juego : ${listaJuegos}">
                <td th:text="${juego.id}"></td>
                <td th:text="${juego.titulo}"></td>
                <td th:text="${juego.precio}"></td>
                
                <!-- ¡MAGIA DE SPRING! Como Videojuego tiene un objeto Categoria adentro, podemos hacer esto: -->
                <td th:text="${juego.categoria.nombre}"></td>
            </tr>
        </tbody>
    </table>
</body>
</html>
```

¡Eso es todo! Si sigues esta lógica, el laboratorio lo tienes en el bolsillo. Léelo con calma, fíjate muy bien en el `Paso 2B` (la relación) y el `Paso 5` (cómo imprimimos el nombre de la categoría en el HTML).
