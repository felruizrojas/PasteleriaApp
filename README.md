# PASTELERIA MIL SABORES - APP
# Pastelería Mil Sabores — App Móvil

> **Credenciales de prueba**
> - Superadmin: `ana@duoc.cl` / `123q` (verificar descuento duoc torta gratis cumpleaños)
> - Administrador: `luis@gmail.com` / `123q` (verificar descuento 50% por edad >=50 años)
> - Vendedor: `marcela@gmail.com` / `123q` (verificar descuento por codigo promocional)
> - Cliente: `claudia.fernandez@gmail.com` / `123q`


## 🔗 Enlaces
- Repositorio GitHub: https://github.com/felruizrojas/PasteleriaApp.git
- Tablero Trello: https://trello.com/invite/b/68ffbb12ffdab6e0d9e1d482/ATTI5b1666790b7b98cc5ac0cefb41b937290A472219/pasteleria-app

## 📝 Descripción
Aplicación Android Jetpack Compose para la pastelería “Mil Sabores”. Permite a clientes navegar el catálogo, gestionar compras y seguir pedidos, mientras que el personal autorizado administra inventario, usuarios y órdenes en un panel integral.

## 🎯 Objetivo del proyecto
Digitalizar el flujo de venta y administración de la pastelería, ofreciendo una experiencia moderna tanto para clientes como para el equipo de ventas y gestión.

## ✅ Funcionalidades principales
1. Registro, autenticación y recuperación de sesión del cliente.
2. Exploración de categorías, productos y detalle con acciones de compartir.
3. Carrito, checkout y seguimiento de pedidos.
4. Panel administrativo con gestión de pedidos, catálogo y usuarios (según rol).
5. Integración con acciones rápidas como compartir productos y controles accesibles por voz en formularios.

## 🛠️ Tecnologías y herramientas
- Kotlin + Jetpack Compose Material 3
- Arquitectura MVVM con ViewModel y StateFlow
- Room / SQLite y DataStore para persistencia
- Coroutines para operaciones asíncronas
- Navigation Compose, Ktor HTTP Client (si aplica), utilidades de AndroidX
- Gradle (KTS) y KSP

## 📁 Estructura del proyecto (`app/src/main`)
```text
app/src/main/
├── java/com/example/pasteleriaapp/
│   ├── core/                                   # Utilidades compartidas (p.ej. seguridad, helpers transversales)
│   ├── data/                                   # Capa de datos (persistencia local)
│   │   ├── local/                              # Persistencia local con Room
│   │   │   ├── dao/                            # Data Access Objects (consultas SQL)
│   │   │   ├── entity/                         # Entidades de base de datos
│   │   │   ├── AppDatabase.kt                  # Clase principal de la base de datos
│   │   │   └── TypeConverters.kt               # Conversores para tipos personalizados
│   │   └── repository/                         # Implementaciones concretas de repositorios
│   ├── domain/                                 # Capa de dominio (reglas de negocio)
│   │   ├── model/                              # Modelos de negocio independientes de UI o DB
│   │   └── repository/                         # Interfaces abstractas para los repositorios
│   ├── ui/                                     # Capa de presentación (Jetpack Compose)
│   │   ├── components/                         # Componentes reutilizables (Scaffold, botones, etc.)
│   │   ├── navigation/                         # NavGraph y rutas de navegación
│   │   ├── screen/                             # Pantallas de la app
│   │   │   ├── admin/                          # Pantalla o módulo de administración
│   │   │   ├── auth/                           # Login, registro y autenticación
│   │   │   ├── blog/                           # Sección de noticias o blog
│   │   │   ├── carrito/                        # Carrito de compras
│   │   │   ├── home/                           # Pantalla principal
│   │   │   ├── nosotros/                       # Sección "Sobre nosotros"
│   │   │   ├── pedidos/                        # Historial y gestión de pedidos
│   │   │   ├── productos/                      # Catálogo y detalle de productos
│   │   │   └── profile/                        # Perfil del usuario
│   │   ├── state/                              # Clases de estado (UIState, Event, etc.)
│   │   ├── theme/                              # Paleta, tipografías y estilos Material3
│   │   └── viewmodel/                          # ViewModels que manejan la lógica de UI
│   └── MainActivity.kt                         # Punto de entrada principal de la aplicación
└── res/                                        # Recursos XML (colores, layouts, drawables, strings, etc.)

```

## 🧭 Mapa del sitio / flujo de pantallas
- Inicio → Catálogo → Detalle de producto → Carrito → Checkout.
- Inicio → Blog / Nosotros.
- Perfil → Edición, pedidos propios y accesos administrativos (según rol).
- Panel de administración → Pedidos | Productos y categorías | Usuarios.

## 🎨 Paleta de colores
| Nombre | Hex | Uso |
| --- | --- | --- |
| TituloMain | `#D67BA8` | Acentos y encabezados principales |
| TituloSecondary | `#5AA58D` | Botones secundarios / enlaces |
| TituloTertiary | `#C4A35A` | Destacados y estados especiales |
| PastelStrawberry | `#F7B7D1` | Fondos suaves |
| PastelMint | `#BFE2D5` | Secciones informativas |
| Ink | `#2A2A2A` | Texto principal |
| InkMuted | `#6C757D` | Texto secundario |

## 🧠 Validaciones clave
- RUN chileno: formato numérico con guion y dígito verificador (`0-9` o `k`).
- Nombre, apellidos, región y comuna: solo letras y espacios.
- Correo electrónico: requiere `@` y validación adicional en el ViewModel.
- Contraseña: protegida con hash seguro (no se almacena en texto plano) y validada por coincidencia entre contraseña y confirmación.
- Reglas de negocio adicionales en checkout (descuentos por edad, código promocional y dominio DUOC).

## 📜 Licencia
Este proyecto fue desarrollado con fines académicos.
