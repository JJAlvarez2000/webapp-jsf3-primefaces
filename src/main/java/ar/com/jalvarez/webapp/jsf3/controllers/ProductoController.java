package ar.com.jalvarez.webapp.jsf3.controllers;

import ar.com.jalvarez.webapp.jsf3.models.Categoria;
import ar.com.jalvarez.webapp.jsf3.models.Producto;
import ar.com.jalvarez.webapp.jsf3.services.ProductoService;
import jakarta.annotation.PostConstruct;
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
    private String textoBuscar;
    private List<Producto> listado;

    // lo inicializamos con postconstruct
    @PostConstruct
    public void init() {
        listado = productoService.listar();
    }

    // con produces creamos un objeto que se puede inyectar en otros lugares
    // en este caso se inyecta en la vista
    // y esta guardado en el contenedor de CDI
    @Produces
    @Model
    public String titulo() {
//        return "HW Java Server Faces 3.0";
        return bundle.getString("producto.texto.titulo");
    }

    // lo comentamos dado que no es funcional con AJAX
    // necesitamos que el SPA sepa que se modifico el modelo para que se actualice la vista
    // y no que tengamos que actualizar la pagina manualmente
    // asi que en alternativa vamos a inicializar el componente productocontroller con el listado
    // (ver arriba)

//    @Produces
//    @RequestScoped
//    @Named("productos")
//    public List<Producto> findAll() {
//        return productoService.listar();
//    }

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
//        return "index.xhtml?faces-redirect=true";
        // ahora es una peticion asincrona
        listado = productoService.listar();
        return "index.html";
    }


    public String editar(Long id) {
        this.id = id;
        listado = productoService.listar();
        return "form.xhtml";
    }
    public void eliminar(Producto producto) {
        productoService.eliminar(producto.getId());
        facesContext.addMessage(null, new FacesMessage(String.format(bundle.getString("producto.mensaje.eliminar"), producto.getNombre())));
        // actualizamos el listado (compatibilidad con AJAX),
        // ahora el eliminar se invoca desde el boton de eliminar en la vista, hacemos el cambio tambien en index
        // cambiamos que se ejecute desde CDI a usar un atributo del controlador
        listado = productoService.listar();
//        return "index.xhtml";
    }
    public void buscar() {
        this.listado = productoService.buscarPorNombre(this.textoBuscar);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Producto> getListado() {
        return listado;
    }

    public void setListado(List<Producto> listado) {
        this.listado = listado;
    }

    public String getTextoBuscar() {
        return textoBuscar;
    }

    public void setTextoBuscar(String textoBuscar) {
        this.textoBuscar = textoBuscar;
    }
}
