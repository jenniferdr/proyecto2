import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.Integer;

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

    /*private static int busqueda(String materia, String[] arreglo){
	return busquedaBi(0,arreglo.length, materia, arreglo);
	}*/
    
    //Recursivo? Busqueda binaria se puede implementar iterativo
    //Hacer recursión es ineficiente si hay un método iterativo
    private static int busqueda(String s, String[] a){
	/*if (ini==fin) {
	    return ini;
	} else {
	    int n = (fin+ini)/2;
	    if (s.compareTo(a[n])<0) {
		busquedaBi(ini,n,s,a);
	    } else {
		if (s.compareTo(a[n])>0) {
		    busquedaBi(n+1,fin,s,a);
		} else {
		    return n;
		}
	    }
	    }*/
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
	//List<Integer> predInmediatos[] = new List[numNodos];
	for (int i=0; i<numNodos; i++) {
	    Arc borrado = grafoAlcance.delArc(i,i);
	    borrado = grafoReducido.delArc(i,i);
	    List<Integer> predecesores = grafoAlcance.getPredecesors(i);
	    //List<Integer> inmediatos = predecesores.clone();
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
			//Integer elem = inmediatos.remove(nodoOrigen);
			listo = true;
		    }
		}
	    }
	    // predInmediatos[i] = inmediatos;
	}

	
	//imprimirGrafo o Lista()
	try {
	    PrintStream out = new PrintStream(args[1]);
	    for (int i=0; i<numNodos; i++) {
		String salida = nombresNodos[i]+" ";
		List<Integer> pred = grafoReducido.getPredecesors(i);
		salida += pred.size();
		for (int j=0; j<pred.size(); j++) {
		    salida += " "+nombresNodos[pred.get(j).intValue()];
		}
		/*if (i!=numNodos-1) {
		    salida += "\n";
		}*/
		out.print(salida+"\n");
	    }
	} catch (IOException ioe) {
	    System.err.println("No se puede escribir el archivo");
	}

    }

	
}





