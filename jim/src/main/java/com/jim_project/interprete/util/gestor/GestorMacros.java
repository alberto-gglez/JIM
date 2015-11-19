package com.jim_project.interprete.util.gestor;

import java.util.ArrayList;
import java.util.HashMap;
import com.jim_project.interprete.Modelo;
import com.jim_project.interprete.Programa;
import com.jim_project.interprete.componente.Ambito;
import com.jim_project.interprete.util.ContenedorParametrosExpansion;
import com.jim_project.interprete.componente.Macro;
import com.jim_project.interprete.componente.Variable;

public class GestorMacros extends GestorComponentes {

    private HashMap<String, Macro> _macros;

    public GestorMacros(Programa programa) {
        this(programa, null);
    }
    
    public GestorMacros(Programa programa, Ambito ambito) {
        super(programa, ambito);
        _macros = new HashMap<>();
    }

    public Macro nuevaMacro(String id) {
        Macro macro = new Macro(id, this);
        _macros.put(id, macro);

        return macro;
    }

    public Macro obtenerMacro(String id) {
        return _macros.get(id);
    }

    public void limpiar() {
        _macros.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        _macros.forEach(
                (k, v) -> {
                    sb.append(v);
                }
        );
        sb.append("\n");

        return sb.toString();
    }

    // Métodos estáticos
    private boolean hayRecursividadEnMacros(Macro macro) {
        return hayRecursividadEnMacros(macro, new ArrayList<>());
    }

    private boolean hayRecursividadEnMacros(Macro macro, ArrayList<String> marcas) {
        String macroActual = macro.id();

        if (marcas.contains(macroActual)) {
            // se puede mejorar para que este método devuelva una lista
            // de las macros que contienen recursividad (sería la lista marcas)
            return true;
        }
        marcas.add(macroActual);

        boolean hayRecursividad = false;
        ArrayList<String> llamadas = macro.llamadasAMacros();

        for (int i = 0; i < llamadas.size() && !hayRecursividad; ++i) {
            Macro m = _macros.get(llamadas.get(i));
            hayRecursividad = hayRecursividad || hayRecursividadEnMacros(m, marcas);
        }
        // si hay recursividad, no borrar
        marcas.remove(macro.id());

        return hayRecursividad;
    }

    public String expandir(ContenedorParametrosExpansion parametrosExpansion) {
        //
        //
        // Usar salto de línea del sistema!
        //
        //
        String idMacro = parametrosExpansion.idMacro;
        String idVariableSalida = parametrosExpansion.idVariableSalida;
        ArrayList<String> parametrosEntrada = parametrosExpansion.variablesEntrada;
        int numeroLinea = parametrosExpansion.linea;

        Macro macro = _macros.get(idMacro);

        if (macro == null) {
            _programa.error().deMacroNoDefinida(numeroLinea, idMacro);
            return null;
        }

        // Añadir comprobación del número de parámetros (nP)
        //  - Permitir llamadas con 0 a Nv parámetros, siendo Nv
        //    el número de variables de entrada que se utilizan
        //    en la macro. Si nP > Nv, mostrar error.
        int nP = parametrosEntrada.size();
        int nV = macro.variablesEntrada().size();

        if (nP > nV) {
            _programa.error().enNumeroParametros(numeroLinea, idMacro, nV, nP);
            return null;
        }

        // Comprobamos que no hay llamadas recursivas directas ni indirectas
        // en la macro a expandir
        if (hayRecursividadEnMacros(macro)) {
            _programa.error().deRecursividadEnMacros(numeroLinea, idMacro);
            return null;
        }

        idVariableSalida = idVariableSalida.toUpperCase();

        String separador = System.getProperty("line.separator");
        String expansion = new String(macro.cuerpo());
        String asignaciones = idVariableSalida + " <- 0" + separador;

        ArrayList<String> vEntrada = macro.variablesEntrada();
        vEntrada.sort(null);

        ArrayList<String> vLocales = macro.variablesLocales();

        for (int i = 0; i < vEntrada.size(); ++i) {
            String variable = vEntrada.get(i);
            String nuevaVariable = _ambito.variables().nuevaVariable(Variable.Tipo.LOCAL).id();

            expansion = expansion.replace(variable, nuevaVariable);

            if (i < parametrosEntrada.size()) {
                asignaciones += nuevaVariable + " <- "
                        + parametrosEntrada.get(i).toUpperCase() + separador;
            }
        }

        for (String variable : vLocales) {
            String nuevaVariable = _ambito.variables().nuevaVariable(Variable.Tipo.LOCAL).id();
            expansion = expansion.replace(variable, nuevaVariable);
        }

        /* Se obtiene una nueva variable local y se reemplaza todas las
         * referencias a la variable de salida Y por esta nueva variable
         */
        String variableSalidaLocal = _ambito.variables().nuevaVariable(Variable.Tipo.LOCAL).id();
        expansion = "# Expansión de " + idMacro + separador
                + asignaciones + expansion.replace("VY", variableSalidaLocal);

        if (programa().modelo().tipo() == Modelo.Tipo.L) {
            ArrayList<String> etiquetas = macro.etiquetas();
            ArrayList<String> etiquetasSalto = macro.etiquetasSalto();

            /* Reempaza las etiquetas que marcan un objetivo de salto
             */
            HashMap<String, String> etiquetasReemplazadas = new HashMap<>();
            for (String etiqueta : etiquetas) {
                // registrar el número de línea de la etiqueta desplazado según
                // el número de línea de la llamada a la macro + el número de
                // asignaciones añadidas al código expandido
                String nuevaEtiqueta = _ambito.etiquetas().nuevaEtiqueta().id();
                etiquetasReemplazadas.put(etiqueta, nuevaEtiqueta);

                expansion = expansion.replace(etiqueta, nuevaEtiqueta);
            }

            /* Reemplaza todas las etiquetas que son objetivo de un salto
             */
            String etiquetaSalida = _ambito.etiquetas().nuevaEtiqueta().id();
            for (String etiqueta : etiquetasSalto) {
                if (etiquetasReemplazadas.containsKey(etiqueta)) {
                    expansion = expansion.replace(etiqueta,
                            etiquetasReemplazadas.get(etiqueta));
                } else {
                    expansion = expansion.replace(etiqueta, etiquetaSalida);
                }
            }
            /* Añadimos una última línea con la etiqueta designada como etiqueta
             * de salida de la macro y la asignación a la variable de salida
             * indicada por el usuario
             */
            expansion = expansion + separador + "[" + etiquetaSalida + "] "
                    + idVariableSalida + " <- " + variableSalidaLocal;
        } else {
            /* Añadimos una última línea con la asignación a la variable de
             * salida indicada por el usuario
             */
            expansion = expansion + idVariableSalida + " <- " + variableSalidaLocal;
        }

        return expansion + "\n# Fin expansión de " + idMacro + separador;
    }

    @Override
    public int count() {
        return _macros.size();
    }

    @Override
    public boolean vacio() {
        return _macros.isEmpty();
    }
}
