package org.alberto.interprete;

import java.util.Hashtable;
import java.util.Stack;

/**
 * Clase encargada de gestionar los distintos bucles del programa interpretado.
 */
public class Bucle {

    private static Hashtable<Integer, Bucle> _buclesLineaApertura = new Hashtable<>();
    private static Hashtable<Integer, Bucle> _buclesLineaCierre = new Hashtable<>();
    private static Stack<Integer> _inicioBucles = new Stack<>();

    private int _lineaInicio;
    private int _lineaFin;
    private int _contador;

    /**
     * Mantiene el número de línea indicado en memoria, representando el número
     * de línea inicial de un objeto Bucle que será creado más tarde, una vez se
     * encuentre el número de línea de cierre y sea indicado con el método
     * {@link #cerrar(int) cerrar}.
     */
    public static void abrir(int lineaInicio) {

        _inicioBucles.push(lineaInicio);
    }

    /**
     * Recupera la última línea de inicio de bucle indicada y crea un nuevo
     * objeto Bucle representado por dicha línea de inicio y la línea de fin
     * indicada por el parámetro lineaFin. El objeto creado es añadido a la
     * colección de bucles.
     */
    public static void cerrar(int lineaFin) {

        if (!_inicioBucles.empty()) {

            int lineaInicio = _inicioBucles.pop();
            _buclesLineaApertura.put(lineaInicio, new Bucle(lineaInicio, lineaFin));
            _buclesLineaCierre.put(lineaFin, new Bucle(lineaInicio, lineaFin));
        } else {

            // gestión de errores
        }

    }

    /**
     * Devuelve el objeto Bucle que comience por la línea de inicio indicada.
     */
    public static Bucle getPorLineaInicio(int lineaInicio) {

        Bucle bucle = null;

        try {

            bucle = _buclesLineaApertura.get(lineaInicio);
        } catch (Exception ex) {

            System.err.println("No se encuentra la apertura del bucle.");
        }

        return bucle;
    }

    /**
     * Devuelve el objeto Bucle que comience por la línea de fin indicada.
     */
    public static Bucle getPorLineaFin(int lineaFin) {

        Bucle bucle = null;

        try {

            bucle = _buclesLineaCierre.get(lineaFin);
        } catch (Exception ex) {

            System.err.println("No se encuentra la apertura del bucle.");
        }

        return bucle;
    }

    /**
     * Borra todos los bucles creados.
     */
    public static void limpiar() {

        _buclesLineaApertura.clear();
        _buclesLineaCierre.clear();
        _inicioBucles.clear();
    }

    /**
     * Constructor de clase.
     *
     * @param	lineaInicio El número de línea inicial del bucle.
     * @param	lineaFin El número de línea final del bucle.
     */
    public Bucle(int lineaInicio, int lineaFin) {

        this._lineaInicio = lineaInicio;
        this._lineaFin = lineaFin;
        this._contador = -1;
    }

    /**
     * Devuelve la línea inicial.
     */
    public int lineaInicio() {

        return _lineaInicio;
    }

    /**
     * Devuelve la línea final.
     */
    public int lineaFin() {

        return _lineaFin;
    }

    /**
     * Devuelve el contador asociado al bucle. El contador marca el número de
     * veces que se ha ejecutado el bucle.
     */
    public int contador() {

        return _contador;
    }

    /**
     * Establece el valor del contador del bucle.
     */
    public void contador(int nuevoValor) {

        this._contador = nuevoValor;
    }

    /**
     * Comprueba si el bucle ha sido inicializado. Se considera que un bucle ha
     * sido inicializado si el valor de su contador es igual o mayor que 0.
     */
    public boolean inicializado() {

        return _contador != -1;
    }

    /**
     * Decrementa el contador del bucle una unidad.
     */
    public void decremento() {

        if (_contador > 0) {

            _contador--;
        }
    }

    /**
     * Reinicia el contador del bucle. El bucle pasa a estar no inicializado.
     */
    public void resetContador() {

        this._contador = -1;
    }

    /**
     * Devuelve una cadena que representa el bucle.
     */
    @Override
    public String toString() {

        return "(" + _lineaInicio + ", " + _lineaFin + ")";
    }

    /**
     * Imprime en pantalla todos los bucles almacenados.
     */
    public static void pintar() {

        System.out.println("Bucles");
        _buclesLineaCierre.forEach((k, v) -> System.out.println(v));
        System.out.println();
    }
}
