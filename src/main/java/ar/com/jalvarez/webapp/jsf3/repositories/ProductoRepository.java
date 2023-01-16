package ar.com.jalvarez.webapp.jsf3.repositories;

import ar.com.jalvarez.webapp.jsf3.models.Producto;

import java.util.List;

public interface ProductoRepository extends CrudRepository<Producto> {
    List<Producto> buscarPorNombre(String nombre);


}
