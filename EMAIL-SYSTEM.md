# 📧 Sistema de Envío de Correos Electrónicos

## Descripción
El sistema ahora incluye funcionalidad para enviar correos electrónicos automáticos a los usuarios cuando se registran en la plataforma. Los emails de bienvenida ayudan a:

- ✅ Confirmar el registro exitoso
- 🎯 Guiar a los usuarios hacia el menú
- 📱 Mejorar la experiencia del usuario
- 🍽️ Aumentar el engagement con el restaurante

## ✨ Características

### 📨 Email de Bienvenida Automático
- Se envía automáticamente cuando un usuario se registra
- Incluye información del restaurante
- Diseño responsivo con HTML y CSS
- Fallback a texto plano si HTML falla
- Procesamiento asíncrono (no bloquea el registro)

### 🎨 Contenido del Email
- Mensaje de bienvenida personalizado
- Links directos al menú de la aplicación
- Información de contacto del restaurante
- Diseño profesional con colores del brand

## 🛠️ Configuración

### 1. Variables de Entorno
Crea un archivo `.env` en la raíz del proyecto con:

```bash
# Email Configuration
EMAIL_USERNAME=tu-email@gmail.com
EMAIL_PASSWORD=tu-contraseña-de-aplicacion

# Restaurant Information
RESTAURANT_NAME=Mi Restaurante
RESTAURANT_PHONE=+1 (555) 123-4567
RESTAURANT_ADDRESS=123 Calle del Restaurante, Ciudad, CP 12345
```

### 2. Configuración de Gmail
Para usar Gmail como proveedor de email:

1. **Habilitar verificación en 2 pasos**:
   - Ve a https://myaccount.google.com/security
   - Activa la verificación en 2 pasos

2. **Generar contraseña de aplicación**:
   - Ve a "Contraseñas de aplicaciones"
   - Genera una nueva contraseña para "Correo"
   - Usa esa contraseña de 16 caracteres en `EMAIL_PASSWORD`

3. **⚠️ IMPORTANTE**: NO uses tu contraseña normal de Gmail, solo la contraseña de aplicación

### 3. Otros Proveedores de Email
Para usar otros proveedores, modifica estas variables en `application-docker.properties`:

```properties
# Para Outlook/Hotmail
EMAIL_HOST=smtp-mail.outlook.com
EMAIL_PORT=587

# Para Yahoo
EMAIL_HOST=smtp.mail.yahoo.com
EMAIL_PORT=587

# Para servidor SMTP personalizado
EMAIL_HOST=tu-smtp-server.com
EMAIL_PORT=587
```

## 🚀 Implementación Técnica

### Componentes Principales

1. **EmailService.java**
   - Manejo de envío de emails
   - Plantillas HTML y texto plano
   - Procesamiento asíncrono

2. **AuthService.java**
   - Integración con el registro de usuarios
   - Manejo de errores sin afectar el registro

3. **AsyncConfig.java**
   - Configuración para procesos asíncronos
   - Pool de threads dedicado para emails

4. **EmailController.java**
   - Endpoints de prueba para administradores
   - Testing de funcionalidad de email

### 📋 Endpoints de Prueba

#### POST `/api/email/test-welcome`
Envía un email de prueba (solo ADMIN/MANAGER):
```bash
curl -X POST "http://192.168.1.152:8082/api/email/test-welcome?email=test@example.com&firstName=Test&lastName=User" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

#### POST `/api/email/test-simple-welcome`
Envía un email simple de prueba:
```bash
curl -X POST "http://192.168.1.152:8082/api/email/test-simple-welcome?email=test@example.com" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

## 🔧 Cómo Funciona

### Flujo de Registro con Email
1. Usuario completa el formulario de registro
2. Sistema valida datos y crea la cuenta
3. **Paralelamente** (sin retrasar la respuesta):
   - Se envía email de bienvenida
   - Si falla el email, se registra el error pero no afecta el registro
4. Usuario recibe token JWT y acceso inmediato
5. Usuario recibe email de bienvenida (por lo general en segundos)

### Características de Seguridad
- ✅ Emails se procesan de forma asíncrona
- ✅ Errores de email no interrumpen el registro
- ✅ Logs detallados para debugging
- ✅ Configuración segura con variables de entorno
- ✅ Solo administradores pueden enviar emails de prueba

## 🎨 Personalización

### Modificar Plantilla de Email
Edita el método `buildWelcomeEmailContent()` en `EmailService.java`:

```java
private String buildWelcomeEmailContent(String firstName, String lastName) {
    return String.format("""
        // Tu plantilla HTML personalizada aquí
        <h1>¡Bienvenido %s!</h1>
        // Más contenido...
        """, firstName);
}
```

### Cambiar Información del Restaurante
Modifica las variables de entorno:
- `RESTAURANT_NAME`: Nombre del restaurante
- `RESTAURANT_PHONE`: Teléfono de contacto
- `RESTAURANT_ADDRESS`: Dirección física

## 🐛 Troubleshooting

### Email no se envía
1. **Verificar logs**:
   ```bash
   docker-compose logs backend | grep -i email
   ```

2. **Verificar configuración**:
   - Contraseña de aplicación correcta
   - Variables de entorno configuradas
   - Proveedor de email correcto

3. **Errores comunes**:
   - `Authentication failed`: Contraseña incorrecta
   - `Connection timeout`: Host/puerto incorrectos
   - `Permission denied`: 2FA no habilitado en Gmail

### Testear configuración
```bash
# Verificar variables de entorno en el contenedor
docker exec restaurante-backend printenv | grep EMAIL

# Testear conectividad SMTP
docker exec restaurante-backend telnet smtp.gmail.com 587
```

## 📊 Monitoreo

### Logs de Email
Los logs incluyen:
- ✅ Intentos de envío exitosos
- ❌ Errores detallados
- ⏱️ Tiempos de procesamiento
- 📧 Direcciones de destino

### Ejemplo de log exitoso:
```
INFO EmailService - Sending welcome email to: user@example.com
INFO EmailService - Welcome email sent successfully to: user@example.com
```

### Ejemplo de log con error:
```
ERROR EmailService - Error sending welcome email to user@example.com: Authentication failed
```

## 🚀 Próximas Mejoras

### Posibles extensiones:
- 📬 Emails de confirmación de pedidos
- 🎂 Emails de cumpleaños con descuentos
- 📰 Newsletter con ofertas especiales
- 🔔 Notificaciones de estado de pedidos
- 📊 Emails con resúmenes mensuales

## 📝 Notas Importantes

1. **Privacidad**: Los emails solo se envían con consentimiento implícito al registrarse
2. **Rendimiento**: El procesamiento asíncrono no afecta el tiempo de respuesta
3. **Escalabilidad**: El pool de threads se puede ajustar según el volumen
4. **Costo**: Gmail permite ~100 emails/día gratis, considera servicios como SendGrid para volumen alto

---

**¡El sistema de emails está listo para usar!** 🎉

Para activar la funcionalidad, simplemente configura las variables de entorno y reinicia la aplicación.
