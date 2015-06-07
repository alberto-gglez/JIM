
package interprete;

import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.Math;

public class Macro {

	private static Hashtable<String, Macro> _macros = new Hashtable<>();
	
	private String _id;
	private String _cuerpo;
	private ArrayList<String> _entrada = new ArrayList<>();
	private ArrayList<String> _locales = new ArrayList<>();
	private ArrayList<String> _etiquetas = new ArrayList<>();
	
 	public static Macro set(String id) {
 		
 		Macro macro = new Macro(id);
 		_macros.put(id, macro);

 		return macro;
 	}

 	public static Macro get(String id) {

 		return _macros.get(id);
 	}

 	public static void clear() {

 		_macros.clear();
 	}

 	public static String expandir(String vSalida, String idMacro, ArrayList<String> parametros) {

 		Macro macro = Macro.get(idMacro);

 		if (macro == null) {

 			return null;
 		}

 		String expansion = new String(macro.cuerpo());
 		String asignaciones = vSalida.toUpperCase() + " <- 0\n";

 		ArrayList<String> vEntrada = macro.variablesEntrada();
 		vEntrada.sort(null);

 		ArrayList<String> vLocales = macro.variablesLocales();
 		ArrayList<String> etiquetas = macro.etiquetas();

 		for (int i = 0; i < vEntrada.size(); ++i) {

 			String variable = vEntrada.get(i);
 			String nuevaVariable = Variable.get(Variable.EVariable.LOCAL).id();

 			expansion = expansion.replace(variable, nuevaVariable);

 			if (i < parametros.size()) {

 				asignaciones += nuevaVariable + " <- " + parametros.get(i) + "\n";
 			}
 		}

 		for (String variable : vLocales) {

 			String nuevaVariable = Variable.get(Variable.EVariable.LOCAL).id();

 			expansion = expansion.replace(variable, nuevaVariable);
 		}

 		for (String etiqueta : etiquetas) {

 			Etiqueta nuevaEtiqueta = Etiqueta.get();
 			
 			expansion = expansion.replace(etiqueta, nuevaEtiqueta.id());
 		}

 		expansion = asignaciones + expansion.replace("VY", vSalida.toUpperCase());

 		return expansion;
 	}

 	public Macro(String id) {

 		this._id = id;
 	}
 	
 	public String id() {

 		return _id;
 	}

 	public String cuerpo() {

 		return _cuerpo;
 	}

 	public void cuerpo(String cuerpo) {

 		this._cuerpo = cuerpo.toUpperCase();
 	}

 	public ArrayList<String> variablesEntrada() {

 		return _entrada;
 	}

 	public ArrayList<String> variablesLocales() {

 		return _locales;
 	}

 	public ArrayList<String> etiquetas() {

 		return _etiquetas;
 	}

 	public void nuevaVariable(String id) {

 		char tipo = id.charAt(1);
 		
		switch (tipo) {
			case 'X':
				if (!_entrada.contains(id)) {
					
					_entrada.add(id);
				}
				break;
			
			case 'Z':
				if (!_locales.contains(id)) {
					
					_locales.add(id);
				}
				break;
				
			case 'Y':
				break;
				
			default:
				System.err.println("Error: Tipo de variable '" + id + "' desconocido.");
		}
 	}

 	public void nuevaEtiqueta(String id) {

 		if (!_etiquetas.contains(id)) {

 			_etiquetas.add(id);
 		}
 	}
 	
 	@Override
 	public String toString() {
 		StringBuilder sb = new StringBuilder();

 		sb.append("ID: " + _id + "\n");
 		sb.append("Cuerpo: " + _cuerpo + "\n");

 		sb.append("\tVariables de entrada:");
 		for (int i = 0; i < _entrada.size(); ++i)
 			sb.append(_entrada.get(i) + " ");
 		sb.append("\n");

 		sb.append("\tVariables locales:");
 		for (int i = 0; i < _locales.size(); ++i)
 			sb.append(_locales.get(i) + " ");
 		sb.append("\n");

 		sb.append("\tEtiquetas:");
 		for (int i = 0; i < _etiquetas.size(); ++i)
 			sb.append(_etiquetas.get(i) + " ");
 		sb.append("\n");

 		return sb.toString();
 	}

	public static void pintar() {
		
		System.out.println("Macros");
 		_macros.forEach( (k, v) -> System.out.println(v) );
 		System.out.println();
 	}
	
}
