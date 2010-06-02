#!/bin/bash

#Probar los archivos en dimacs
base=/home/gmljosea/Codigo/Algoritmos3/proyecto2

cd $base/random
for i in $(ls)
do
	cd $base/random/$i
	for j in $(ls *.input)
	do
		nombre=$(echo $j | cut -d . -f 1)
		#echo $nombre
		cd $base
		rm ./random/$i/$nombre.salida
		touch ./random/$i/$nombre.salida
		java Main ./random/$i/$nombre.input ./random/$i/$nombre.salida 2> ./random/$i/$nombre.salida
		sort ./random/$i/$nombre.output >> ordenado
		sort ./random/$i/$nombre.salida >> ordenado2
		if (diff ordenado ordenado2 >> /dev/null)
		then
			echo "Exito en el caso $nombre"
		else
			echo "Error en el caso $nombre"
		fi
	done
done
