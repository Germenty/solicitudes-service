Crear 
build.gradle

Agregar

plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '3.24.0'
}


gradle ca --package=co.com.powerup --type=reactive --name=solicitudes-service --lombok=true --javaVersion=VERSION_17 

.\gradlew gep --type webflux

.\gradlew bootRun

Generar Modelos

gradle gm --name Solicitud
gradle gm --name TipoPrestamo  
gradle gm --name Estado
gradle gm --name User


Generar Use Case

gradle guc --name Solicitud
gradle guc --name TipoPrestamo  
gradle guc --name Estado

Generar Driven Adapter

gradle gda --type r2dbc

Generar Entry Point

gradle gep --type webflux


test Domain
./gradlew :usecase:test --tests "co.com.powerup.usecase.user.UserUseCaseTest"


# Opción 1: Ejecutar tests normales del módulo usecase
./gradlew :usecase:test

# Opción 2: Ejecutar tests con reporte de cobertura paso a paso
./gradlew :usecase:test :usecase:jacocoTestReport