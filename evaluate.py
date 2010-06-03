#!/usr/bin/env python

import sys
import os
import copy
import fnmatch
import re

version = "1.0"
banner_msg = "Evaluador para el Proyecto 2, ver. " + version + "."
help_msg = banner_msg + "\n\n\
El script compila las fuentes en java, corre el proyecto sobre los\n\
casos de prueba e imprime estadisticas de los resultados. Sintaxis:\n\n\
  evaluate <camino-a-repositorio-de-casos-de-prueba>+\n\n\
El repositorio contiene los casos de prueba y sus soluciones. Cada\n\
caso de prueba debe tener extension \".input\" y su solucion debe\n\
tener extension \".output\".\n"

tmp_dir = "tmp.results.p2"

def banner():
  print banner_msg + "\n\n"

def usage():
  print help_msg

def clean():
  os.system("rm -fr *.class *~ %s" % tmp_dir)

def populate_tmp_dir(path,tmp_dir):
  if os.path.isdir(path):
    ext_tmp_dir = tmp_dir + "/" + os.path.basename(path)
    os.system("mkdir %s" % ext_tmp_dir)
    for file in os.listdir(path):
      ext_path = path + "/" + file
      populate_tmp_dir(ext_path,ext_tmp_dir)
  else:
    output_file = re.sub(".input",".output",path)
    if output_file != path:
      os.system("ln %s %s" % (path,tmp_dir))
      os.system("ln %s %s" % (output_file,tmp_dir))

def make_tmp_dir(repository,tmp_dir):
  os.system("mkdir %s" % tmp_dir)
  for path in repository:
    populate_tmp_dir(path,tmp_dir)

def compile():
  status = os.system("javac -cp . *.java 2> %s/compile.err" % tmp_dir)
  #return status
  return 0

def evaluate_file(path):
  if fnmatch.fnmatch(path,"*.input"):
    sys.stdout.write("    Archivo " + path + " ... ")
    sys.stdout.flush()
    try:
      output = re.sub(".input",".output",path)
      files = { 'in' : path, 'out' : output+"_tmp" }
      status = os.system("(ulimit -c 0; ulimit -t 600; java -cp . Main %(in)s %(out)s 2>/dev/null)" % files)
      print "[status=%d]" % status
    except RuntimeError:
      print "[excepcion de corrida]"

def evaluate_dir(path):
  print "    Visitando directorio " + path
  sys.stdout.flush()
  for fname in os.listdir(path):
    efname = path + "/" + fname
    evaluate(efname)

def evaluate(path):
  if os.path.isdir(path):
    evaluate_dir(path)
  else:
    evaluate_file(path)

def compare_file(fname1,fname2):
  if not os.path.exists(fname1):
    print "Error: %s debe existir en repositorio" % fname1
    return (0,0)

  if not os.path.exists(fname2):
    return (0,1)

  status = os.system("diff %s %s > /dev/null" % (fname1,fname2))
  if status == 0:
    return (1,1)
  else:
    return (0,1)

def stats_file(path):
  if fnmatch.fnmatch(path,"*.input"):
    fname1 = re.sub(".input",".output",path)
    fname2 = re.sub(".input",".output_tmp",path)
    return compare_file(fname1,fname2)
  else:
    return (0,0)

def stats_dir(path):
  num_match = 0.0
  num_files = 0.0
  for fname in os.listdir(path):
    efname = path + "/" + fname
    (m,f) = stats(efname)
    num_match += m
    num_files += f
  return (num_match,num_files)

def stats(path):
  if os.path.isdir(path):
    return stats_dir(path)
  else:
    return stats_file(path)


# main
if len(sys.argv) < 2:
  print "Error: debe especificar al menos un camino a repositorios de casos de prueba."
  raise SystemExit

if sys.argv[1] == "-ayuda" or sys.argv[1] == "-help" or sys.argv[1] == "-?":
  usage()
  raise SystemExit

# verify repository
repository = sys.argv[1:]

sys.stdout.write("Verificando los repositorios:")
for path in copy.copy(repository):
  if os.path.exists(path):
    sys.stdout.write(" ")
    sys.stdout.write(path)
    sys.stdout.write(" [ok]")
  else:
    sys.stdout.write(" ")
    sys.stdout.write(path)
    sys.stdout.write(" [err]")
    repository.remove(path)
sys.stdout.write("\n")

# perform clean
sys.stdout.write("Eliminando todos los .class y archivos temporales... ")
sys.stdout.flush()
clean()
print "hecho!"

# make tmp subdir
sys.stdout.write("Creando directorio temporal de resultados... ")
sys.stdout.flush()
make_tmp_dir(repository,tmp_dir)
print "hecho!"

# compile
sys.stdout.write("Compilando... ")
sys.stdout.flush()
compile_status = compile()
print "hecho!"

if compile_status != 0:
  print "Error durante la compilacion. Ver %s/compile.err" % tmp_dir
  raise SystemExit

# evaluate
print "Evaluando los repositorios:"
for path in repository:
  fname = tmp_dir + "/" + os.path.basename(path)
  evaluate(fname)

# compute stats
sys.stdout.write("Computando estadisticas... ")
sys.stdout.flush()
num_match = 0.0
num_files = 0.0
for path in repository:
  fname = tmp_dir + "/" + os.path.basename(path)
  (m,f) = stats(fname)
  num_match += m
  num_files += f

if num_files > 0:
  print "resultado=(%f,%f), %%=%f" % (num_match,num_files,100.0*num_match/num_files)
else:
  print "[error]"


