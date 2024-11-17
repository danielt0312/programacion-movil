# Proyecto que extrae todos los datos necesarios de un proyecto AndroidStudio para ser
# reconstruido desde CERO, incluye:
# Los archivos "*.png" "*.jpg" "*.java" "*.xml" "*.jar" 
# Funciona igual tanto para los proyectos en JAVA como en KOTLIN
# Genera dos archivos como resultado (en el directorio actual)
# 1) El archivo instalador de la aplicación
# 2) El archivo zip con los archivos necesarios para la reconstrucción de la aplicación - De menor tamaño en comparación con el ZIP generado al comprimir todo el proyecto

NombreProyecto=Z_ITI271304_U3_TORRES_COLORADO_JUAN_DANIEL

NombreCarpeta=Temporal_${NombreProyecto}
ApellidoPaternoEstudiante=torres
ApellidoMaternoEstudiante=colorado
NombreEstudiante=juan_daniel
ClaveGrupoEstudiante=iti-271304_u3

# Los siguientes casos son exclusivos (En español: es uno o es otro, pero no ambos)
# Caso 1: Uso del profesor para compartir un fuente con la clase
# NombreArchivo=$NombreProyecto
# Caso 2: Estudiante prepara entrega con lo solicitado por el profesor (Modificar el formato de acuerdo con los estandares establecidos en la presentación del curso)
NombreArchivo=${ClaveGrupoEstudiante}_${ApellidoPaternoEstudiante}_${ApellidoMaternoEstudiante}_${NombreEstudiante}


declare -a arr=("*.kts")
## Para cada extension agregar al .ZIP
for i in "${arr[@]}" 
do
    echo "$i"
    rm $i
    find $NombreProyecto/app -name $i -exec cp {} . \;
    zip $NombreArchivo.zip $i
    rm $i     
done

## Arreglo de extensiones de interés
declare -a arr=("*.png" "*.jpg" "*.java" "*.kt" "*.kts" "*.xml" "*.jar" "*.obj" "*.glsl" "*.mp3" "*.shader")
## Para cada extension agregar al .ZIP
for i in "${arr[@]}" 
do
    echo "$i"
    rm $i
    find $NombreProyecto/app/src -name $i -exec cp {} . \;
    zip $NombreArchivo.zip $i
    rm $i
done

# INCLUIR .JARs adicionales (de necesitarlso
declare -a arr=("*.jar" )
## Para cada extension agregar al .ZIP
for i in "${arr[@]}" 
do
    echo "$i"
    rm $i
    find $NombreProyecto/app/libs -name $i -exec cp {} . \;
    zip $NombreArchivo.zip $i
    rm $i
done


# INCLUIR EL APK
#F=`find $NombreProyecto/app/build/outputs -name "*.apk"`
F=`find $NombreProyecto/app/build/intermediates/apk/debug -name "*.apk"`
cp $F ./$NombreProyecto.apk
mv ./$NombreProyecto.apk ./$NombreArchivo.apk

rm *.gradle
cp $NombreProyecto/build.gradle .
mv build.gradle build_raiz.gradle
zip $NombreArchivo.zip build_raiz.gradle
rm *.gradle


cp $NombreProyecto/app/build.gradle .
mv build.gradle build_app.gradle
zip $NombreArchivo.zip build_app.gradle
rm *.gradle
