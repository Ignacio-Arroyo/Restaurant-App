# 📧 Sistema de Notificaciones por Email para Pedidos

## 🎯 Descripción
El sistema ahora envía automáticamente emails a los clientes para mantenerlos informados sobre el estado de sus pedidos. Esto mejora significativamente la experiencia del cliente y reduce la necesidad de llamadas telefónicas.

## ✨ Funcionalidades Implementadas

### 📨 Email de Confirmación de Pedido
**Cuándo se envía:** Inmediatamente después de que el cliente realiza un pedido

**Contenido del email:**
- ✅ Confirmación del pedido con número de orden
- 🍽️ Detalle completo de productos ordenados (comidas y bebidas)
- 💰 Cantidades, precios unitarios y total
- 📍 Tipo de orden (para comer en el lugar o para llevar)
- ⏱️ Tiempo estimado de preparación (15-25 minutos)
- 🎨 Diseño profesional con colores del restaurante

### 🎉 Email de Pedido Listo
**Cuándo se envía:** Cuando el personal del restaurante marca el pedido como "READY"

**Contenido del email:**
- ✅ Notificación de que el pedido está listo
- 🏪 Instrucciones claras de dónde recoger
- 📍 Dirección y teléfono del restaurante
- ⏰ Horarios de atención
- ⚠️ Nota importante sobre tiempo de retiro (30 minutos)

## 🔄 Flujo de Notificaciones

### 1. **Cliente Realiza Pedido**
```
Cliente hace pedido → Sistema crea orden → Email de confirmación enviado
```

### 2. **Pedido en Preparación**
```
Staff marca como "PREPARING" → (Sin email, ya se notificó en la confirmación)
```

### 3. **Pedido Listo**
```
Staff marca como "READY" → Email de pedido listo enviado
```

## 🛠️ Implementación Técnica

### Componentes Modificados

#### 1. **EmailService.java**
- ✅ `sendOrderConfirmationEmail()` - Email de confirmación
- ✅ `sendOrderReadyEmail()` - Email de pedido listo
- ✅ `buildOrderConfirmationContent()` - Template HTML confirmación
- ✅ `buildOrderReadyContent()` - Template HTML pedido listo
- ✅ Procesamiento asíncrono para no afectar el rendimiento

#### 2. **OrderService.java**
- ✅ Integración con EmailService
- ✅ Envío automático en `createOrder()`
- ✅ Envío automático en `updateOrderStatus()` cuando status = READY
- ✅ Manejo robusto de errores (fallos de email no afectan las operaciones)

#### 3. **Estados de Orden**
```java
public enum OrderStatus {
    PENDING,    // Pedido pendiente
    CONFIRMED,  // Pedido confirmado
    PREPARING,  // En preparación (Email de confirmación ya enviado)
    READY,      // Listo (Envía email de "pedido listo")
    DELIVERED,  // Entregado
    CANCELLED   // Cancelado
}
```

## 📧 Ejemplos de Emails

### Email de Confirmación
```html
¡Pedido Confirmado!
Orden #123

¡Hola Juan!

Gracias por tu pedido! Hemos recibido tu orden y ya estamos preparándola con mucho cariño.

🍳 PREPARANDO

📋 Detalles de tu pedido:
Tipo de orden: Para llevar
Fecha: 2025-08-21 a las 16:30

+-----------------------------------+
| Producto    | Cant | Precio | Sub |
+-----------------------------------+
| 🍽️ Hamburguesa|  2  | $12.99 | $25.98 |
| 🥤 Coca Cola  |  2  |  $2.50 |  $5.00 |
+-----------------------------------+
| TOTAL:                    | $30.98 |
+-----------------------------------+

⏱️ Tiempo estimado: 15-25 minutos
Te notificaremos cuando esté listo!
```

### Email de Pedido Listo
```html
🎉 ¡Tu Pedido Está Listo!
Orden #123

¡Hola Juan!

✅ LISTO PARA RECOGER

🎯 Tu pedido está listo para recoger en el mostrador para llevar
Total: $30.98

📍 Ubicación: 123 Restaurant Street, Food City
📞 Teléfono: +1 (555) 123-4567
⏰ Horario: Lunes a Domingo, 11:00 AM - 10:00 PM

🕐 Nota importante: Tu pedido se mantendrá caliente por 30 minutos.
```

## 🎨 Características de Diseño

### Emails Responsive
- ✅ Diseño optimizado para móviles y desktop
- ✅ Colores consistentes con el brand del restaurante
- ✅ Iconos y emojis para mejor legibilidad
- ✅ Estructura clara y fácil de leer

### Información Completa
- ✅ Número de orden prominente
- ✅ Detalles completos del pedido
- ✅ Precios y totales claros
- ✅ Información de contacto
- ✅ Instrucciones específicas

## 🔧 Configuración

### Variables de Entorno (.env)
```bash
# Email ya configurado
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=[email]
EMAIL_PASSWORD=[password]

# Información del restaurante
RESTAURANT_NAME=Restaurant App
RESTAURANT_PHONE=+1 (555) 123-4567
RESTAURANT_ADDRESS=123 Restaurant Street, Food City, FC 12345
```

## 🧪 Cómo Probar el Sistema

### 1. **Probar Email de Confirmación**
```bash
# Registrar un usuario nuevo
curl -X POST "http://192.168.1.152:8082/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "password123"
  }'

# Crear un pedido (recibirás email de confirmación)
curl -X POST "http://192.168.1.152:8082/api/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "meals": [{"itemId": 1, "quantity": 2}],
    "drinks": [{"itemId": 1, "quantity": 1}],
    "totalCost": 25.50,
    "orderType": "TAKEAWAY"
  }'
```

### 2. **Probar Email de Pedido Listo**
```bash
# Como administrador, cambiar estado a READY
curl -X PUT "http://192.168.1.152:8082/api/admin/orders/1/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{"status": "READY"}'
```

## 📊 Monitoreo y Logs

### Logs de Email
```bash
# Ver logs de emails enviados
docker-compose logs backend | grep -i "email\|EmailService"

# Ejemplo de logs exitosos:
# INFO EmailService - Sending order confirmation email to: user@example.com for order 1
# INFO EmailService - Order confirmation email sent successfully to: user@example.com for order 1
# INFO EmailService - Sending order ready email to: user@example.com for order 1  
# INFO EmailService - Order ready email sent successfully to: user@example.com for order 1
```

## 🔒 Características de Seguridad

### Manejo Robusto de Errores
- ✅ **Fallos de email no afectan operaciones**: Si el email falla, el pedido se crea/actualiza normalmente
- ✅ **Logs detallados**: Todos los intentos y errores se registran
- ✅ **Procesamiento asíncrono**: No bloquea las operaciones principales
- ✅ **Validaciones**: Verifica que el usuario y email existan antes de enviar

### Seguridad de Datos
- ✅ **Solo usuarios propietarios**: Solo el cliente que hizo el pedido recibe los emails
- ✅ **Información mínima**: Los emails contienen solo información necesaria
- ✅ **Configuración segura**: Credenciales de email en variables de entorno

## 🚀 Beneficios para el Negocio

### Para los Clientes
- 📱 **Notificaciones automáticas** - No necesitan llamar para preguntar
- ⏰ **Transparencia** - Saben exactamente cuándo recoger su pedido
- 📧 **Confirmación detallada** - Tienen registro completo de lo que ordenaron
- 🎯 **Mejor experiencia** - Se sienten más informados y atendidos

### Para el Restaurante
- ☎️ **Menos llamadas telefónicas** - Reduce interrupciones al staff
- 📊 **Mejor comunicación** - Clientes más informados
- 💼 **Imagen profesional** - Emails con diseño profesional
- ⚡ **Eficiencia operativa** - Automatización de comunicaciones

## 🔄 Estados que Generan Emails

| Estado | Email | Descripción |
|--------|-------|-------------|
| PENDING → CONFIRMED | ✅ Confirmación | Cuando se crea el pedido |
| CONFIRMED → PREPARING | ❌ Ninguno | Ya se notificó en confirmación |
| PREPARING → READY | ✅ Pedido Listo | Cliente debe recoger |
| READY → DELIVERED | ❌ Ninguno | Transacción completada |
| Cualquier → CANCELLED | ❌ Ninguno | (Futuro: email de cancelación) |

## 📈 Próximas Mejoras Sugeridas

### Funcionalidades Adicionales
- 📧 **Email de cancelación** cuando se cancela un pedido
- ⏰ **Recordatorios** si el pedido listo no se recoge en 30 min
- 📊 **Encuestas de satisfacción** después de la entrega
- 🎁 **Ofertas personalizadas** basadas en historial de pedidos
- 📱 **SMS notifications** como alternativa a email

### Mejoras Técnicas
- 🔄 **Templates personalizables** desde el admin panel
- 📊 **Dashboard de emails** para ver estadísticas de envío
- 🌐 **Soporte multi-idioma** para emails
- 📧 **Emails con PDF** de recibo adjunto

---

**¡El sistema de notificaciones por email está completamente funcional!** 🎉

Los clientes ahora recibirán automáticamente:
1. **Email de confirmación** cuando hagan un pedido
2. **Email de pedido listo** cuando puedan recogerlo

Esto mejora significativamente la experiencia del cliente y la eficiencia operativa del restaurante.
