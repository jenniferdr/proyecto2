import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.Integer;

/**
 * Programa que recibe como entrada un archivo (.input) con
 * un conjunto de nombres de cursos junto con sus
 * prerequisitos.
 * Computa los prerequisitos inmediatos  para cada
 * curso y las escribe en el archivo de salida. 
 * Sintaxis: java Main <archivo.input> <archivo.output>
 *
 * @author José A. Goncalves y Jennifer Dos Reis
 * @version 1.0
 * @since 1.6
**/

public class 

    private static int busqueda(String s, String[] a){
	int ini = 0;
	int fin = a.length-1;
	while (ini <= fin) {
	    int medio = (ini+fin)/2;
	    if (a[medio].equals(s)) {
		return medio;
	    } else if (a[medio].compareTo(s) < 0) {
		ini = medio+1;
	    } else {
		fin = medio-1;
	    }
	}
	return -1;
    }

    public static void ordenar (String[] arreglo) {
	quicksort(arreglo, 0, arreglo.length);
    }

    public static void quicksort (String[] arreglo, int ini, int fin) {
	if ( fin-ini <= 1 ) {
	    return;
	}
	int limite = ini-1;
	String pivote = arreglo[fin-1];
        for (int j=ini; j<fin; j++) {
	    if (arreglo[j].compareTo(pivote) <= 0) {
		limite++;
		String temp = arreglo[j];
		arreglo[j] = arreglo[limite];
		arreglo[limite] = temp;
	    }
	}
        quicksort(arreglo, ini, limite);
	quicksort(arreglo, limite+1, fin);
	return;
    }

    public static void main(String[] args){
	
	if (args.length != 2 && args.length !=1) {
	    System.err.println("Sintaxis: java Main <fileName_entrada> <fileName_salida>");
	    return;
	}

	BufferedReader in = null;
	String[] nombresNodos = null;
	DiGraph grafoInput = null;
	int numNodos = 0;

	try {
	    in = new BufferedReader(new FileReader(args[0]));

	    String linea = in.readLine();
	    numNodos = Integer.parseInt(linea);
	    nombresNodos = new String[numNodos];

	    // Leer nombres de los cursos 
	    for (int i=0; i<numNodos; i++) {
		nombresNodos[i] = in.readLine();
	    }
	    ordenar(nombresNodos);

	    // Crear grafo y agregar los arcos correspondientes 
	    grafoInput = new DiGraphMatrix(numNodos);
	    linea = in.readLine();
	    int numLineas = Integer.parseInt(linea);
	    String[] partes;

	    for (int i=0; i<numLineas; i++) {
		linea = in.readLine(); 	
		partes = linea.split(" ");
		
		int nodoDestino = busqueda(partes[0],nombresNodos);
		int numArcos = Integer.parseInt(partes[1]);

		for (int j=0; j<numArcos; j++) {
		    int nodoOrigen = busqueda(partes[j+2],nombresNodos);
		    Arc arco = grafoInput.addArc(nodoOrigen,nodoDestino);
		}
	    } 
	    
	}catch (FileNotFoundException fnfe) {
	    System.err.println("Error al cargar archivo, verifique el nombre");
	    return;
	} catch(IOException ioe) {
	    System.err.println("Error: " + ioe);
	    return;
	} catch(NumberFormatException e) {
	    System.err.println("Error de formato en el archivo especificado");
	    return;
	}

	DiGraph grafoAlcance = grafoInput.royWarshall();
	DiGraph grafoReducido = (DiGraph)grafoAlcance.clone();
	
	// Calcular grafo excluyendo arcos de transitividad 
	for (int i=0; i<numNodos; i++) {
	    Arc borrado = grafoAlcance.delArc(i,i);
	    borrado = grafoReducido.delArc(i,i);
	    List<Integer> predecesores = grafoAlcance.getPredecesors(i);
	   
	    for (int j=0; j<predecesores.size(); j++) {
		int nodoOrigen = predecesores.get(j).intValue();
		boolean listo = false;
		for (int k=0; k<predecesores.size() && !listo; k++) {
		    int nodoDestino = predecesores.get(k).intValue();
		    if (nodoDestino==nodoOrigen || nodoDestino==i) {
			continue;
		    }
		    if (grafoAlcance.isArc(nodoOrigen,nodoDestino)) {
			Arc arco = grafoReducido.delArc(nodoOrigen,i);
			listo = true;
		    }
		}
	    }
	}

	
	// Imprimir Grafo 
	try {
	    PrintStream out = new PrintStream(args[1]);

	    //No ordenamos los predecesores porque DiGraphMatrix
	    //ya devuelve los devuelve en el orden que se necesita
	    for (int i=0; i<numNodos; i++) {
		String salida = nombresNodos[i]+" ";
		List<Integer> pred = grafoReducido.getPredecesors(i);
		salida += pred.size();
		for (int j=0; j<pred.size(); j++) {
		    salida += " "+nombresNodos[pred.get(j).intValue()];
		}
		out.print(salida+"\n");
	    }

	} catch (IOException ioe) {
	    System.err.println("No se puede escribir el archivo");
	}

    }

}





