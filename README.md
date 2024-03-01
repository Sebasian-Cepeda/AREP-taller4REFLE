# taller 4, ARQUITECTURAS DE SERVIDORES DE APLICACIONES, META PROTOCOLOS DE OBJETOS, PATRÓN IOC, REFLEXIÓN

# Descripción 
## DESARROLLADO CON
* [Java version 17](https://www.oracle.com/co/java/technologies/downloads/) - Lenguaje de programación usado.
* [Maven](https://maven.apache.org/download.cgi) - Gestor de dependencias del proyecto
* [Git](https://git-scm.com/downloads) - Gestion de versiones del proyecto
* [omdbapi](https://www.omdbapi.com) - API externa para realizar consultas

# pasos
1. Clonar el repositorio y abrirlo en su IDE de preferenccia, entrar a la clase Main.java y con ayuda de su IDE ejecutarla (generalmente es f5)

2. poner la siguiente ruta en el browser: http://localhost:35000
   tendremos la pagina para consultar peliculas
![image](https://github.com/Sebasian-Cepeda/AREP-taller4REFLE/assets/89321404/775dcf01-62a0-4a79-9a70-347f126e367a)

y su usamos http://localhost:35000/component/spring tendremos la reflexión de un metodo llamado spring dentro de HelloController

![image](https://github.com/Sebasian-Cepeda/AREP-taller4REFLE/assets/89321404/7793abbb-27f7-4a10-a223-cf5f55587606)

![image](https://github.com/Sebasian-Cepeda/AREP-taller4REFLE/assets/89321404/3be3d7e3-daf1-4dfc-900b-44faf090f384)

esto con ayuda de las siguientes interfaces:
>![image](https://github.com/Sebasian-Cepeda/AREP-taller4REFLE/assets/89321404/3791d201-f030-4dd8-ac1b-bc35a8c66f5c)
>
>![image](https://github.com/Sebasian-Cepeda/AREP-taller4REFLE/assets/89321404/442e5a26-00c9-4ab7-bf3a-2ae87246b9f5)
>

tambien podemos usar http://localhost:35000/component/potencia?query=5 para calcular la x ^ 4 siendo x la query que daremos en el path 
![image](https://github.com/Sebasian-Cepeda/AREP-taller4REFLE/assets/89321404/9b0db792-4e4d-41e2-8bbc-1ce17148eafb)

y por ultimo podemos usar http://localhost:35000/component/compoQuery?query=soy%20una%20query para obtener otro metodo
![image](https://github.com/Sebasian-Cepeda/AREP-taller4REFLE/assets/89321404/8fd90564-a3b4-4e90-b8ce-4b847d007fa4)

---
# Autor
Juan Sebastian Cepeda Saray


