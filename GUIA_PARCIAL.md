# Guía rápida para el parcial de Programación Móvil

Esta aplicación consume publicaciones de JSONPlaceholder y reúne los cinco temas
del parcial en un ejemplo pequeño.

## 1. Consumo de API REST con Retrofit

Una API REST expone recursos mediante URLs y verbos HTTP. En este ejemplo:

- `PostApi` declara `GET /posts`.
- Retrofit construye la petición.
- Gson convierte el JSON recibido en objetos `Post`.
- `PostRepository` oculta de dónde vienen los datos.
- La función es `suspend`, por lo que se ejecuta desde una corrutina sin bloquear la UI.

Flujo: `ViewModel -> Repository -> Retrofit -> API`.

## 2. UI, estilo y componentes reutilizables

En Compose cada función marcada con `@Composable` describe una parte de la interfaz.
`PostCard` es reutilizable: recibe datos y un evento, pero no conoce Retrofit ni la
navegación. `MaterialTheme` mantiene tipografía y colores consistentes.

Idea clave: elevar el estado. La pantalla recibe el estado del ViewModel y envía
eventos como `updateQuery`; así la UI sigue siendo fácil de probar.

## 3. Búsqueda y filtrado

El ViewModel conserva dos estados: texto de búsqueda y usuario seleccionado. `combine`
une esos estados con la lista original y produce automáticamente una lista filtrada.
La búsqueda usa `contains(..., ignoreCase = true)`.

Conviene conservar la lista original y crear una lista derivada. Si se modifica la
lista original en cada búsqueda, luego es difícil recuperar todos los elementos.

## 4. Navegación

`NavHost` contiene dos destinos:

- `posts`: lista.
- `detail/{postId}`: detalle con un argumento entero.

Al tocar una tarjeta se navega enviando únicamente el identificador. La pantalla de
detalle recupera el objeto desde el ViewModel. En proyectos reales también podría
pedir el elemento al repositorio usando ese ID.

## 5. Arquitectura MVVM

- Model/Data: `Post`, `PostApi`, `PostRepository`.
- ViewModel: obtiene datos, maneja errores y contiene el estado de pantalla.
- View: composables que observan `StateFlow` y dibujan la interfaz.

La View no llama directamente a Retrofit. Esta separación facilita cambiar la API,
hacer pruebas y evitar perder el estado durante cambios de configuración.

`PostsUiState` representa Loading, Success y Error. Es importante modelar los tres
casos: una aplicación no debería asumir que la red siempre responde correctamente.

## Preguntas que podrían aparecer

1. ¿Por qué Retrofit requiere una URL base terminada en `/`?
2. ¿Qué diferencia existe entre `LazyColumn` y `Column`?
3. ¿Por qué las llamadas de red se realizan con `suspend`?
4. ¿Qué responsabilidad tiene el Repository?
5. ¿Por qué el ViewModel no debería contener composables?
6. ¿Qué ocurre con la UI cuando cambia un `StateFlow` observado?
7. ¿Por qué se utiliza una `key` estable en los elementos de `LazyColumn`?
