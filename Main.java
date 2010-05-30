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
	DiGraph grafo= null;

	try {
		grafo = new DiGraphMatrix(args[0]);

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


	try {
	    if(args.length ==2){
	        grafo.write(args[1]);
	    }else{
	    	System.out.print(grafo);
	    }
	} catch (IOException ioe) {
	    System.err.println("No se puede escribir el archivo");
	}
    }
}



