package com.challengeAlura.main;

import com.challengeAlura.model.Autor;
import com.challengeAlura.model.Datos;
import com.challengeAlura.model.Libro;
import com.challengeAlura.repository.AutorRepo;
import com.challengeAlura.repository.LibrosRepo;
import com.challengeAlura.services.Consulta;
import com.challengeAlura.services.ConvertirDatos;

import java.util.List;
import java.util.Scanner;

public class Principal {
    private final LibrosRepo librosRepo;
    private final AutorRepo autorRepo;
    private final Scanner teclado = new Scanner(System.in);
    private List<Libro> libros;
    private List<Autor> autores;
    private final Consulta consulta = new Consulta();
    private final ConvertirDatos convertirDatos = new ConvertirDatos();
    private static final String url = "https://gutendex.com/books/";

    public Principal(LibrosRepo librosRepo, AutorRepo autorRepo) {
        this.librosRepo = librosRepo;
        this.autorRepo = autorRepo;
    }

    public void iniciarPrograma() {
        int eleccion = 0;
        while (true) {
            System.out.println("=".repeat(10));
            System.out.println("""
                    > << eliga una opción a consultar >>
                    > ----------------------------------
                    > 1: Buscar libro por su nombre.
                    > 2: Listar libros registrados.
                    > 3: Listar autores registrados.
                    > 4: Listar autores vivos por año.
                    > 5: Listar por idiomas.
                    > ----------------------------------
                    > 0: Salir.
                    
                    """);
            System.out.println("=".repeat(10));
            System.out.print("> ");
            eleccion = teclado.nextInt();
            teclado.nextLine();
            switch (eleccion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    consultarRegistrados();
                    break;
                case 3:
                    consultarAutoresRegistrados();
                    break;
                case 4:
                    ConsultarFiltroFecha();
                    break;
                case 5:
                    ConsultarIdiomaLibro();
                    break;
                case 0:
                    System.out.println("Has salido de la app");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Elección no valida");
                    teclado.nextLine();
                    break;
            }
        }
    }

    private Datos buscarDatosLibro() {
        String libroBuscar = "";
        System.out.println("""
                Indique el libro a buscar:
                
                """);
        System.out.print("> ");
        libroBuscar = teclado.nextLine();
        String finalName = url + "?search=" + libroBuscar.toLowerCase().replace(" ", "%20");
        var json = consulta.Consultar(finalName);
        Datos datoslibro = convertirDatos.getData(json, Datos.class);
        return datoslibro;
    }

    private void buscarLibro() {
        Datos datosLibro = buscarDatosLibro();
        if (datosLibro.results().isEmpty()) {
            System.out.println("¡No hubo resultados!");
            System.out.println("Presione Enter para continuar...");
            teclado.nextLine();
        } else {
            Libro libro = new Libro(datosLibro.results().get(0));
            libro.setTitle(datosLibro.results().get(0).title().length() > 240 ? datosLibro.results().get(0).title().substring(0, 240) : datosLibro.results().get(0).title());
            Autor autor = new Autor(datosLibro.results().get(0).authors().get(0));
            try {
                System.out.println("=".repeat(10) + " Resultados de la búsqueda " + "=".repeat(10));
                System.out.printf("""
                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        Descargas: %s
                        %n""", libro.getTitle(), libro.getAuthor(), libro.getLanguage(), libro.getDownload_count().toString());
                System.out.println("=".repeat(10) + " Resultados de la búsqueda " + "=".repeat(10));
                librosRepo.save(libro);
                autorRepo.save(autor);
                System.out.println("Presione Enter para continuar...");
                teclado.nextLine();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void consultarRegistrados() {
        try {
            libros = librosRepo.findAll();
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados...");
                System.out.println("Presione Enter para continuar...");
                teclado.nextLine();
            } else {
                libros.forEach(l -> {
                    System.out.printf("""
                            Libro: %s
                            Autor: %s
                            Idioma: %s
                            Descargas: %s
                            %n""", l.getTitle(), l.getAuthor(), l.getLanguage(), l.getDownload_count().toString());
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void consultarAutoresRegistrados() {
        autores = autorRepo.findAll();
        try {
            if (autores.isEmpty()) {
                System.out.println("No hay libros registrados...");
                System.out.println("Presione Enter para continuar...");
                teclado.nextLine();
            } else {
                autores.forEach(a -> {
                    System.out.printf("""
                            Autor: %s
                            Nacimiento: %s
                            Fallecimiento: %s
                            %n""", a.getName(), a.getBirth_day() != null ? a.getBirth_day().toString() : "No se encuentra fecha de nacimiento", a.getDeath_day() != null ? a.getDeath_day().toString() : "En la actualidad");
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void ConsultarFiltroFecha() {
        int autor = 0;
        while (autor == 0) {
            System.out.println("Ingrese el año para buscar a sus respectivos autores");
            System.out.print("> ");
            autor = teclado.nextInt();
            if (autor == 0) {
                System.out.println("Provea un año para consultar que autores están registrados en este intervalo.");
                teclado.nextLine();
            } else {
                List<Autor> autores = autorRepo.getAuthorByDate(autor);
                autores.forEach(a -> {
                    System.out.printf("""
                            Autor: %s
                            Nacimiento: %s
                            Fallecimiento: %s
                            %n""", a.getName(), a.getBirth_day() != null ? a.getBirth_day().toString() : "No se encuentra fecha de nacimiento", a.getDeath_day() != null ? a.getDeath_day().toString() : "En la actualidad");
                });
            }
        }
    }

    private void ConsultarIdiomaLibro() {
        int lenguaje = 0;
        while (lenguaje == 0) {
            System.out.println("""
                    Eliga el idioma a consultar del libro:
                    1: en - Inglés
                    2: es - Español
                    3: fr - Francés
                    4: de - Alemán
                    5: it - Italiano
                    """);
            System.out.println("=".repeat(10));
            lenguaje = teclado.nextInt();
            switch (lenguaje) {
                case 1:
                    libros = librosRepo.findByLanguage("en");
                    break;
                case 2:
                    libros = librosRepo.findByLanguage("es");
                    break;
                case 3:
                    libros = librosRepo.findByLanguage("fr");
                    break;
                case 4:
                    libros = librosRepo.findByLanguage("de");
                    break;
                case 5:
                    libros = librosRepo.findByLanguage("it");
                    break;
                default:
                    System.out.println("Elección no valida...\nPresione Enter para reintentar.");
                    teclado.nextLine();
                    break;
            }
        }
        if (libros.isEmpty()) {
            System.out.println("No hay libros con el lenguaje seleccionado...");
            teclado.nextLine();
        } else {
            libros.forEach(l -> {
                System.out.printf("""
                    Libro: %s
                    Autor: %s
                    Idioma: %s
                    Descargas: %s
                    %n""", l.getTitle(), l.getAuthor(), l.getLanguage(), l.getDownload_count().toString());
            });
            teclado.nextLine();
        }
    }
}
