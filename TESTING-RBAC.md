# Guía de Pruebas - Sistema de Control de Acceso Basado en Roles

## Problema Identificado

El usuario reporta: **"la cuenta del gerente no puede acceder a lo que había pedido"**

## Empleados de Prueba Creados

Se han agregado los siguientes empleados al DataInitializer para facilitar las pruebas:

### 1. Gerente
- **Email:** gerente@restaurant.com  
- **Password:** gerente123
- **Rol:** GERENTE
- **Número de Empleado:** 000001

### 2. Cocinero  
- **Email:** cocinero@restaurant.com
- **Password:** cocinero123  
- **Rol:** COCINERO
- **Número de Empleado:** 000002

### 3. Mesero
- **Email:** mesero@restaurant.com
- **Password:** mesero123
- **Rol:** MESERO  
- **Número de Empleado:** 000003

### 4. Cajero
- **Email:** cajero@restaurant.com
- **Password:** cajero123
- **Rol:** CAJERO
- **Número de Empleado:** 000004

### 5. Afanador
- **Email:** afanador@restaurant.com
- **Password:** afanador123
- **Rol:** AFANADOR
- **Número de Empleado:** 000005

## Permisos Esperados para GERENTE

Según la especificación, el GERENTE debería tener acceso a:

✅ **Menú** (VIEW_MENU)
✅ **Sales** (VIEW_SALES, MANAGE_SALES)  
✅ **Inventario** (VIEW_INVENTORY, MANAGE_INVENTORY)
✅ **Time Clock** (VIEW_TIME_CLOCK, USE_TIME_CLOCK)
✅ **Time History** (VIEW_TIME_HISTORY, MANAGE_TIME_ENTRIES)
✅ **Order Management** (VIEW_ORDERS, MANAGE_ORDERS, CREATE_ORDERS)
✅ **My Permissions** (VIEW_USER_PERMISSIONS)

## Pasos de Verificación

### 1. Iniciar el Backend
```bash
cd backend
./mvnw spring-boot:run
```

### 2. Probar Login del Gerente
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "gerente@restaurant.com",
    "password": "gerente123"
  }'
```

**Respuesta esperada:**
```json
{
  "token": "eyJ...",
  "type": "Bearer",
  "email": "gerente@restaurant.com",
  "firstName": "Carlos",
  "lastName": "Rodríguez",
  "role": "GERENTE"
}
```

### 3. Probar en Frontend
1. Ir a http://localhost:3000/login
2. Ingresar:
   - Email: `gerente@restaurant.com`
   - Password: `gerente123`
3. Verificar que aparezcan los enlaces de navegación para:
   - Menu
   - Sales  
   - Inventory
   - Order Management
   - Time Clock
   - Time History
   - My Permissions

## Posibles Problemas y Soluciones

### Problema 1: Enlaces de Navegación No Aparecen
**Causa:** Error en la función `hasPermission`
**Solución:** Verificar que:
- El rol se normaliza correctamente (`GERENTE`)
- El enum `WorkerRole` incluye `GERENTE`
- Los permisos están asignados correctamente

### Problema 2: Acceso Denegado a Páginas
**Causa:** ProtectedRoute no reconoce los permisos  
**Solución:** Verificar:
- Las rutas usan los permisos correctos
- La función `checkPermission` funciona

### Problema 3: Login Falla
**Causa:** 
- Servidor no está corriendo
- Base de datos no inicializada
- Contraseña no cifrada correctamente

**Solución:**
- Verificar que el servidor esté en puerto 8080
- Comprobar logs del servidor
- Verificar que DataInitializer se ejecutó

## Debug del Sistema de Permisos

### En el navegador, abrir consola y ejecutar:
```javascript
// Verificar usuario actual
console.log('User:', JSON.parse(localStorage.getItem('user')));

// Verificar permisos (en página My Permissions)
// Debería mostrar todos los permisos del gerente
```

### Verificar en el código:
1. `utils/permissions.ts` - función `hasPermission`
2. `components/Navigation.tsx` - uso de `checkPermission`  
3. App.tsx - ProtectedRoute con permisos correctos

## Código de Verificación Rápida

Para verificar que el sistema funciona, agregar temporalmente en Navigation.tsx:

```typescript
// Debug temporal
console.log('User role:', user?.role);
console.log('Can view inventory:', checkPermission(Permission.VIEW_INVENTORY));
console.log('Can view sales:', checkPermission(Permission.VIEW_SALES));
console.log('All permissions:', usePermissions(user?.role).permissions);
```

## Estado Actual de Implementación

### ✅ Completado:
- Sistema de permisos definido
- ProtectedRoute implementado
- Navegación condicional
- Autenticación híbrida Workers/Users
- Empleados de prueba en DataInitializer

### 🔍 Por Verificar:
- Login del gerente funciona
- Navegación muestra enlaces correctos
- Acceso a páginas específicas
- Modo solo lectura en inventario para cocinero

## Próximos Pasos

1. **Iniciar servidor backend**
2. **Probar login con gerente@restaurant.com**
3. **Verificar navegación en frontend**
4. **Corregir cualquier problema encontrado**
5. **Probar otros roles para comparar**
