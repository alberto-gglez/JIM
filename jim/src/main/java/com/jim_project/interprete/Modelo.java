package com.jim_project.interprete;

import com.jim_project.interprete.util.Configuracion;
import com.jim_project.interprete.util.Error;

/**
 * Clase que abstrae el concepto de modelo de computación, almacenando información
 * sobre su tipo y la ruta al directorio de macros del modelo.
 * @author Alberto García González
 */
public class Modelo {

    private Configuracion _configuracion;

    /**
     * Enumeración utilizada para distinguir los distintos modelos. El analizador previo
     * se considera un modelo para facilitar el diseño del programa.
     */
    public enum Tipo {

        PREVIO, L, LOOP, WHILE
    };

    private Tipo _tipo;
    private final String _ruta;

    /**
     * Constructor que recibe el nombre del modelo y una referencia a la {@link Configuracion}
     * del programa.
     * @param nombre El nombre del modelo.
     * @param configuracion Una referencia a la configuración del programa.
     */
    public Modelo(String nombre, Configuracion configuracion) {
        _configuracion = configuracion;

        if (_configuracion != null) {
            switch (nombre.toUpperCase()) {
                case "L":
                    _tipo = Tipo.L;
                    break;

                case "LOOP":
                    _tipo = Tipo.LOOP;
                    break;

                case "WHILE":
                    _tipo = Tipo.WHILE;
                    break;

                default:
                    Error.deModeloNoValido(nombre);
                    _tipo = null;
            }
            switch (_tipo) {
                case L:
                    _ruta = _configuracion.rutaMacrosL();
                    break;

                case LOOP:
                    _ruta = _configuracion.rutaMacrosLoop();
                    break;

                case WHILE:
                    _ruta = _configuracion.rutaMacrosWhile();
                    break;
                    
                default:
                    _ruta = null;
            }
        } else {
            _tipo = null;
            _ruta = null;
        }
    }

    /**
     * Devuelve el tipo del modelo.
     * @return El tipo del modelo.
     */
    public Tipo tipo() {
        return _tipo;
    }

    /**
     * Devuelve la ruta al directorio de macros asociado al modelo definida
     * en la {@link Configuracion}.
     * @return La ruta al directorio de macros asociado al modelo.
     */
    public String ruta() {
        return _ruta;
    }
}
