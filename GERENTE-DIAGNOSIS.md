# Diagnóstico - Problema con Cuenta de Gerente

## Problema Reportado

**Issue:** "no he probado todas las cuentas de empleados pero la del gerente no funciona. Desde la cuenta admin, cree una cuenta de gerente y no tiene acceso a las paginas que deberia"

## Posibles Causas

### 1. **Problema en la Creación del Worker**
- El rol no se está guardando correctamente como 'GERENTE'
- La contraseña no se está cifrando bien
- El worker se está creando inactivo

### 2. **Problema en el Login**
- AuthService no está encontrando al worker
- El rol no se está devolviendo correctamente en la respuesta
- JWT no incluye el rol correcto

### 3. **Problema en el Frontend**
- El rol no se está leyendo correctamente del token/respuesta
- La función hasPermission no reconoce 'GERENTE'
- Los permisos no están mapeados correctamente

## Pasos de Diagnóstico

### 1. Verificar Creación del Worker

**Desde Admin Panel, crear gerente y revisar:**
- ¿Se envía el rol como 'GERENTE'?
- ¿La API responde exitosamente?
- ¿Se guarda en la base de datos?

### 2. Verificar Login

**Intentar login y revisar consola del navegador:**
```javascript
// Debería mostrar:
🔐 LOGIN RESPONSE: {
  token: "eyJ...",
  email: "gerente@test.com",
  firstName: "...",
  lastName: "...",
  role: "GERENTE"  // ← Esto debe ser exactamente "GERENTE"
}
```

### 3. Verificar Permisos

**Después del login, revisar consola:**
```javascript
// Debería mostrar:
🔍 DEBUG GERENTE: {
  originalRole: "GERENTE",
  normalizedRole: "GERENTE", 
  requestedPermission: "VIEW_INVENTORY",
  availablePermissions: [...],
  hasPermission: true
}
```

### 4. Verificar Navegación

**En la página principal, revisar consola:**
```javascript
// Debería mostrar:
🎯 NAVIGATION DEBUG - GERENTE DETECTED: {
  user: {...},
  role: "GERENTE",
  canViewInventory: true,
  canViewSales: true,
  canViewTimeHistory: true,
  canAccessAdmin: false  // Gerente NO debe ser admin
}
```

## Debug Steps (Para el usuario)

### Paso 1: Crear Gerente desde Admin
1. Login como admin@restaurant.com / admin123
2. Ir a Worker Management
3. Crear nuevo worker con rol GERENTE
4. **Anotar el email y password usado**

### Paso 2: Intentar Login como Gerente
1. Logout del admin
2. Intentar login con el gerente creado
3. **Abrir DevTools → Console**
4. Ver los mensajes de debug

### Paso 3: Verificar Navegación
1. Si el login fue exitoso
2. En la página principal
3. **Verificar qué enlaces aparecen**
4. **Revisar consola para debug messages**

## Resultados Esperados vs. Actuales

### ✅ Login Exitoso del Gerente
- **Esperado:** Login exitoso, usuario loggeado
- **Actual:** ?

### ✅ Enlaces de Navegación Visibles
- **Esperado:** Menu, Sales, Inventory, Order Management, Time Clock, Time History
- **Actual:** Solo algunos o ninguno?

### ✅ Acceso a Páginas
- **Esperado:** Puede acceder a /inventory, /sales, etc.
- **Actual:** Access denied?

## Debug Information Agregada

He agregado logs temporales en:
- `utils/permissions.ts` - Debug específico para GERENTE
- `context/AuthContext.tsx` - Info del login response
- `components/Navigation.tsx` - Debug de permisos en navegación

## Siguiente Paso

**Ejecutar el diagnóstico y reportar:**
1. ¿Qué muestra la consola durante el login?
2. ¿Qué enlaces aparecen en la navegación?
3. ¿Cuáles son los mensajes de debug?

Con esta información podré identificar exactamente dónde está el problema y corregirlo.
