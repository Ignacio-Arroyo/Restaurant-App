# Sistema de Control de Acceso Basado en Roles (RBAC)

## Resumen de Implementación

Se ha implementado un sistema completo de control de acceso basado en roles para el Restaurant App, con permisos específicos para cada tipo de empleado.

## Roles y Permisos

### GERENTE (Gerente)
**Acceso completo a:**
- ✅ Menú (ver)
- ✅ Sales (ver y gestionar)
- ✅ Inventario (ver y gestionar)
- ✅ Time Clock (usar)
- ✅ Time History (ver y gestionar)
- ✅ Order Management (ver y gestionar)
- ✅ Worker Management (solo ADMIN)
- ✅ Admin Panel (solo ADMIN)
- ✅ Coupons (solo ADMIN)
- ✅ My Permissions (ver sus permisos)

### COCINERO (Cocinero)
**Acceso limitado a:**
- ✅ Menú (ver)
- ✅ Inventario (SOLO VER, sin modificar)
- ✅ Order Management (ver y gestionar órdenes)
- ✅ Time Clock (usar)
- ✅ My Permissions (ver sus permisos)

### MESERO, CAJERO, AFANADOR (Otros empleados)
**Acceso básico a:**
- ✅ Menú (ver)
- ✅ Time Clock (usar)
- ✅ My Permissions (ver sus permisos)

### CUSTOMER (Clientes)
**Acceso público:**
- ✅ Menú (ver)
- ✅ Realizar órdenes
- ✅ Ver sus propias órdenes
- ❌ NO tienen acceso a "My Permissions"

### ADMIN (Administrador del Sistema)
**Acceso completo a todo el sistema**

## Archivos Modificados

### Frontend

1. **`utils/permissions.ts`** - Nuevo archivo
   - Define enum de permisos
   - Mapea roles a permisos específicos
   - Funciones de utilidad para verificar permisos

2. **`components/ProtectedRoute.tsx`** - Nuevo archivo
   - Componente para proteger rutas basado en permisos
   - Muestra mensajes de acceso denegado
   - Maneja carga y autenticación

3. **`components/UserPermissions.tsx`** - Nuevo archivo
   - Página para mostrar permisos del usuario actual
   - Solo accesible por empleados (no clientes)
   - Agrupa permisos por categorías

4. **`components/Navigation.tsx`** - Actualizado
   - Usa sistema de permisos para mostrar/ocultar enlaces
   - Enlace "My Permissions" solo para empleados
   - Muestra rol del usuario en barra

5. **`App.tsx`** - Actualizado
   - Rutas protegidas con permisos específicos
   - Importa nuevo sistema de permisos
   - Nueva ruta /permissions

6. **`pages/Inventory.tsx`** - Actualizado
   - Cocineros pueden ver pero no modificar
   - Oculta botones de edición según permisos
   - Mensaje informativo para modo solo lectura

7. **`components/ProductList.tsx`** - Actualizado
   - Soporte para modo solo lectura (readOnly)
   - Oculta columna "Actions" en modo solo lectura
   - Botones de edición opcionales

### Backend

8. **`service/AuthService.java`** - Actualizado
   - Autenticación dual: Users y Workers
   - Prioriza Workers activos sobre Users
   - Cifra contraseñas de Workers
   - Verificación de emails duplicados

## Flujo de Autenticación

1. **Usuario ingresa email/password**
2. **Sistema verifica primero en tabla Workers**
   - Si es Worker activo y password coincide → Login como Worker
3. **Si no es Worker, verifica en tabla Users**
   - Si es User válido → Login como User
4. **JWT incluye rol en respuesta**
5. **Frontend almacena user data con rol**
6. **Sistema de permisos evalúa accesos según rol**

## Características Implementadas

### ✅ Control de Acceso
- Navegación dinámica según permisos
- Rutas protegidas por permisos específicos
- Páginas con funcionalidades limitadas por rol

### ✅ Modo Solo Lectura
- Inventario: Cocinero puede ver sin modificar
- Mensajes informativos para usuarios con permisos limitados
- UI adaptativa según permisos

### ✅ Autenticación Híbrida
- Workers y Users en el mismo endpoint
- Contraseñas cifradas para Workers
- JWT con información de rol

### ✅ UX/UI Mejorado
- Indicadores visuales de permisos
- Mensajes claros de acceso denegado
- Rol visible en navegación

## Testing del Sistema

### Para probar cada rol:

1. **Como GERENTE:**
   - Crear Worker con rol GERENTE
   - Login con email/password del Worker
   - Verificar acceso a: Menú, Sales, Inventario (editable), Time Clock, Time History, Order Management

2. **Como COCINERO:**
   - Crear Worker con rol COCINERO  
   - Login con email/password del Worker
   - Verificar acceso a: Menú, Inventario (solo lectura), Order Management, Time Clock

3. **Como MESERO/CAJERO/AFANADOR:**
   - Crear Worker con rol respectivo
   - Login con email/password del Worker
   - Verificar acceso solo a: Menú, Time Clock

4. **Como CUSTOMER:**
   - Register/Login normal
   - Verificar acceso a: Menú, Orders
   - Verificar NO acceso a "My Permissions"

## Próximos Pasos

1. **Testear sistema completo**
2. **Verificar Order Management con permisos**
3. **Implementar logs de acceso (opcional)**
4. **Documentar API endpoints con roles**
