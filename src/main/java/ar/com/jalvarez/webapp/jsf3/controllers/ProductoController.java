package ar.com.jalvarez.webapp.jsf3.controllers;

import ar.com.jalvarez.webapp.jsf3.models.Categoria;
import ar.com.jalvarez.webapp.jsf3.models.Producto;
import ar.com.jalvarez.webapp.jsf3.services.ProductoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.ResourceBundle;

// model junta named con requestscope
@Model
public class ProductoController {
    private Producto producto;
    private Long id;
    @Inject
    ProductoService productoService;
    @Inject
    FacesContext facesContext;
    @Inject
    ResourceBundle bundle;

    // con produces creamos un objeto que se puede inyectar en otros lugares
    // en este caso se inyecta en la vista
    // y esta guardado en el contenedor de CDI
    @Produces
    @Model
    public String titulo() {
//        return "HW Java Server Faces 3.0";
        return bundle.getString("producto.texto.titulo");
    }
    @Produces
    @RequestScoped
    @Named("productos")
    public List<Producto> findAll() {
        return productoService.listar();
    }

    @Produces
    @Model
    public Producto producto() {
        this.producto = new Producto();
        if(id != null && id > 0) {
            productoService.porId(id).ifPresent(p -> {
                this.producto = p;
            });
            }
        return producto;
    }

    @Produces
    @Model
    public List<Categoria> categorias() {
        return productoService.listarCategorias();
    }

    public String guardar() {
        System.out.println("Producto: " + producto);
        if(producto.getId() != null && producto.getId() > 0) {
            facesContext.addMessage(null, new FacesMessage(String.format(bundle.getString("producto.mensaje.editar"), producto.getNombre())));
        } else {
            facesContext.addMessage(null, new FacesMessage(String.format(bundle.getString("producto.mensaje.crear"), producto.getNombre())));
        }
        productoService.guardar(producto);
        return "index.xhtml?faces-redirect=true";
    }


    public String editar(Long id) {
        this.id = id;
        return "form.xhtml";
    }
    public String eliminar(Producto producto) {
        productoService.eliminar(producto.getId());
        facesContext.addMessage(null, new FacesMessage(String.format(bundle.getString("producto.mensaje.eliminar"), producto.getNombre())));
        return "index.xhtml?faces-redirect=true";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
