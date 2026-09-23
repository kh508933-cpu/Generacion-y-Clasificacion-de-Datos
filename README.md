# Entrega 1 - Generación y clasificación de datos

## Proyecto
Generación y clasificación de datos - Primera entrega.

## Descripción
Este proyecto implementa la clase `GenerateInfoFiles`, encargada de generar
los archivos de texto que servirán como entrada para el programa principal
del proyecto.

La solución está desarrollada para Java 8 y no solicita información al
usuario durante su ejecución.

## Estructura

```text
Entrega_1_Generacion_Clasificacion_Datos/
├── src/
│   └── GenerateInfoFiles.java
├── data/
├── README.md
└── .project
```

La carpeta `data` se crea automáticamente al ejecutar el programa.

## Archivos generados

Al ejecutar `GenerateInfoFiles`, se crean:

- `products.txt`: información de los productos.
- `salesmen.txt`: información de los vendedores.
- Un archivo `sales_ID_Nombre.txt` por cada vendedor, con sus ventas.

## Formatos

### products.txt

```text
IDProducto;NombreProducto;PrecioPorUnidadProducto
```

Ejemplo:

```text
1;Computador;250000
2;Teclado;80000
```

### salesmen.txt

```text
TipoDocumento;NumeroDocumento;Nombres;Apellidos
```

Ejemplo:

```text
CC;1234567890;Karen;Morales
```

### Archivo de ventas

```text
IDProducto;CantidadProductoVendido;
```

Ejemplo:

```text
3;5;
7;2;
```

## Ejecución en Eclipse

1. Crear o importar el proyecto en Eclipse.
2. Verificar que el proyecto utilice Java 8.
3. Ejecutar la clase `GenerateInfoFiles` como Java Application.
4. Revisar la carpeta `data`.
5. Los archivos generados serán utilizados como entradas para la siguiente
   etapa del proyecto.

## Observación

La primera entrega está enfocada en la generación de los archivos de prueba.
La organización y clasificación de la información de ventas corresponde a
las siguientes entregas del proyecto.
