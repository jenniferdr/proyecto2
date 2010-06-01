import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Programa que dada la informacion contenida en un archivo
 * construye un grafo y le aplica el algoritmo Roy-Warshall.
 * Usa la implementacion DiGrafoMatrix si el numero de nodos
 * es mayor al numero de arcos, en caso contrario usa la 
 * implementacion DiGrafoList.
 *
 * @author José A. Goncalves y Jennifer Dos Reis
 * @version 1.0
 * @since 1.6
**/

public class Main {
    public static void main(String[] args){
	
	if (args.length != 2 && args.length !=1) {
	    System.err.println("Sintaxis: java Main <fileName_entrada> <fileName_salida>");
	    return;
	}

	BufferedReader in;
	String[] nombresNodos = null;
	DiGraph grafoInput = null;
	int numNodos = 0;

	try {
	    grafo = new DiGraphMatrix(args[0]);
	    String linea = in.readLine();
	    numNodos = Integer.parseInt(linea);
	    nombresNodos = new String[numNodos];
	    for (int i=0; i<numNodos; i++) {
		nombresNodos[i] = in.readLine();
	    }
	    ordenar(nombresNodos);

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
		
	} catch (FileNotFoundException fnfe) {
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
	DiGraph grafoReducido = grafoAlcance.clone();
	Lista<Integer>[] predInmediatos = new Lista<Integer>[numNodos];
	for (int i=0; i<numNodos; i++) {
	    Lista<Integer> predecesores = grafoAlcance.getPredecesors(i);
	    Lista<Integer> inmediatos = predecesores.clone();
	    for (int j=0; j<predecesores.size(); j++) {
		int nodoOrigen = predecesores.get(j).getValue();
		boolean listo = false;
		for (int k=0; k<predecesores.size() && !listo; k++) {
		    int nodoDestino = predecesores.get(k).getValue();
		    if (nodoDestino==nodoOrigen) {
			continue;
		    }
		    if (grafoAlcance.isArc(nodoOrigen,nodoDestino)) {
			Arc arco = grafoReducido.delArc(nodoOrigen,i);
			Integer borrado = inmediatos.remove(nodoOrigen);
			listo = true;
		    }
		}
	    }
	    predInmediatos[i] = inmediatos;
	}

	imprimirGrafo o Lista()

	}
}



