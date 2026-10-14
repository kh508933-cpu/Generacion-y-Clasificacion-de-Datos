
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {

    private static final String CARPETA = "data";

    static class Vendedor {
        String tipoDocumento;
        String numeroDocumento;
        String nombres;
        String apellidos;
        double totalVendido;

        Vendedor(String tipo, String numero, String nombres,
                String apellidos) {
            this.tipoDocumento = tipo;
            this.numeroDocumento = numero;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.totalVendido = 0;
        }

        String nombreCompleto() {
            return nombres + " " + apellidos;
        }
    }

    static class Producto {
        String id;
        String nombre;
        double precio;
        int cantidadVendida;

        Producto(String id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.cantidadVendida = 0;
        }
    }

    public static void main(String[] args) {
        try {
            generarReportes();
            System.out.println("REPORTES GENERADOS CON EXITO");
            System.out.println("Archivo: data/reporte_vendedores.txt");
            System.out.println("Archivo: data/reporte_productos.txt");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("ERROR AL GENERAR LOS REPORTES");
            System.out.println("Detalle: " + e.getMessage());
        }
    }

    public static void generarReportes() throws IOException {
        File carpeta = new File(CARPETA);

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            throw new IOException(
                "No existe la carpeta data. Ejecuta primero GenerateInfoFiles."
            );
        }

        Map<String, Vendedor> vendedores = cargarVendedores();
        Map<String, Producto> productos = cargarProductos();

        procesarVentas(vendedores, productos);

        generarReporteVendedores(vendedores);
        generarReporteProductos(productos);
    }

    private static Map<String, Vendedor> cargarVendedores()
            throws IOException {

        Map<String, Vendedor> vendedores = new HashMap<String, Vendedor>();
        File archivo = new File(CARPETA, "salesmen.txt");

        try (BufferedReader lector =
                new BufferedReader(new FileReader(archivo))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 4) {
                    throw new IOException(
                        "Registro de vendedor invalido: " + linea
                    );
                }

                Vendedor vendedor = new Vendedor(
                    datos[0].trim(),
                    datos[1].trim(),
                    datos[2].trim(),
                    datos[3].trim()
                );

                vendedores.put(
                    vendedor.tipoDocumento + ";" + vendedor.numeroDocumento,
                    vendedor
                );
            }
        }

        return vendedores;
    }

    private static Map<String, Producto> cargarProductos()
            throws IOException {

        Map<String, Producto> productos = new HashMap<String, Producto>();
        File archivo = new File(CARPETA, "products.txt");

        try (BufferedReader lector =
                new BufferedReader(new FileReader(archivo))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 3) {
                    throw new IOException(
                        "Registro de producto invalido: " + linea
                    );
                }

                try {
                    Producto producto = new Producto(
                        datos[0].trim(),
                        datos[1].trim(),
                        Double.parseDouble(datos[2].trim())
                    );

                    productos.put(producto.id, producto);

                } catch (NumberFormatException e) {
                    throw new IOException(
                        "Precio de producto invalido: " + linea
                    );
                }
            }
        }

        return productos;
    }

    private static void procesarVentas(
            Map<String, Vendedor> vendedores,
            Map<String, Producto> productos) throws IOException {

        File carpeta = new File(CARPETA);
        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            throw new IOException("No se pudieron leer los archivos de data.");
        }

        for (File archivo : archivos) {
            String nombre = archivo.getName();

            if (!archivo.isFile()
                    || !nombre.startsWith("sales_")
                    || !nombre.endsWith(".txt")) {
                continue;
            }

            try (BufferedReader lector =
                    new BufferedReader(new FileReader(archivo))) {

                String primeraLinea = lector.readLine();

                if (primeraLinea == null) {
                    continue;
                }

                String[] identificacion =
                        primeraLinea.trim().split(";", -1);

                if (identificacion.length < 2) {
                    throw new IOException(
                        "Identificacion de vendedor invalida en " + nombre
                    );
                }

                String clave = identificacion[0].trim()
                        + ";" + identificacion[1].trim();

                Vendedor vendedor = vendedores.get(clave);

                if (vendedor == null) {
                    throw new IOException(
                        "El vendedor " + clave
                        + " no existe en salesmen.txt"
                    );
                }

                String linea;

                while ((linea = lector.readLine()) != null) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }

                    String[] datos = linea.split(";", -1);

                    if (datos.length < 2) {
                        throw new IOException(
                            "Venta invalida en " + nombre + ": " + linea
                        );
                    }

                    String idProducto = datos[0].trim();

                    if (idProducto.isEmpty()) {
                        continue;
                    }

                    try {
                        int cantidad =
                                Integer.parseInt(datos[1].trim());

                        if (cantidad < 0) {
                            throw new IOException(
                                "Cantidad negativa en " + nombre
                            );
                        }

                        Producto producto = productos.get(idProducto);

                        if (producto == null) {
                            throw new IOException(
                                "El producto " + idProducto
                                + " no existe en products.txt"
                            );
                        }

                        vendedor.totalVendido +=
                                producto.precio * cantidad;

                        producto.cantidadVendida += cantidad;

                    } catch (NumberFormatException e) {
                        throw new IOException(
                            "Cantidad invalida en " + nombre + ": " + linea
                        );
                    }
                }
            }
        }
    }

    private static void generarReporteVendedores(
            Map<String, Vendedor> vendedores) throws IOException {

        List<Vendedor> lista =
                new ArrayList<Vendedor>(vendedores.values());

        Collections.sort(lista, new Comparator<Vendedor>() {
            @Override
            public int compare(Vendedor a, Vendedor b) {
                return Double.compare(b.totalVendido, a.totalVendido);
            }
        });

        File archivo = new File(CARPETA, "reporte_vendedores.txt");

        try (BufferedWriter escritor =
                new BufferedWriter(new FileWriter(archivo))) {

            escritor.write("VENDEDOR;DOCUMENTO;TOTAL VENDIDO");
            escritor.newLine();

            for (Vendedor vendedor : lista) {
                escritor.write(
                    vendedor.nombreCompleto() + ";"
                    + vendedor.tipoDocumento + " "
                    + vendedor.numeroDocumento + ";"
                    + String.format(java.util.Locale.US, "%.2f",
                            vendedor.totalVendido)
                );
                escritor.newLine();
            }
        }
    }

    private static void generarReporteProductos(
            Map<String, Producto> productos) throws IOException {

        List<Producto> lista =
                new ArrayList<Producto>(productos.values());

        Collections.sort(lista, new Comparator<Producto>() {
            @Override
            public int compare(Producto a, Producto b) {
                return Integer.compare(b.cantidadVendida, a.cantidadVendida);
            }
        });

        File archivo = new File(CARPETA, "reporte_productos.txt");

        try (BufferedWriter escritor =
                new BufferedWriter(new FileWriter(archivo))) {

            escritor.write("PRODUCTO;PRECIO;CANTIDAD VENDIDA");
            escritor.newLine();

            for (Producto producto : lista) {
                escritor.write(
                    producto.nombre + ";"
                    + String.format(java.util.Locale.US, "%.2f",
                            producto.precio) + ";"
                    + producto.cantidadVendida
                );
                escritor.newLine();
            }
        }
    }
}
