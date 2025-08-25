# 🍽️ Configuración Modular del Restaurante

Esta aplicación está diseñada para ser fácilmente adaptable a diferentes restaurantes. Todo se puede personalizar desde archivos de configuración centralizados.

## 📋 Configuración Rápida

### 1. Información del Restaurante

Edita el archivo `frontend/src/config/restaurant.config.ts` para personalizar:

- **Nombre del restaurante** y eslogan
- **Información de contacto** (teléfono, email, sitio web)
- **Dirección completa**
- **Horarios de operación**
- **Redes sociales**
- **Colores de marca**
- **Características habilitadas** (delivery, pickup, etc.)

### 2. Configuración del Backend

Edita el archivo `.env` en la raíz del proyecto:

```bash
# INFORMACIÓN DEL RESTAURANTE
RESTAURANT_NAME=Tu Restaurante
RESTAURANT_PHONE=+506 1234-5678
RESTAURANT_ADDRESS=Tu Dirección Completa
RESTAURANT_EMAIL=info@turestaurante.com

# CONFIGURACIÓN DE EMAIL
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=tu-email@gmail.com
EMAIL_PASSWORD=tu-contraseña-de-aplicacion
```

## 🎨 Personalización Visual

### Colores de Marca

En `restaurant.config.ts`, modifica la sección `branding`:

```typescript
branding: {
  primaryColor: "#tu-color-primario",
  secondaryColor: "#tu-color-secundario", 
  accentColor: "#tu-color-de-acento"
}
```

### Logo y Favicon

1. Coloca tu logo en `frontend/public/assets/logo.png`
2. Coloca tu favicon en `frontend/public/assets/favicon.ico`
3. Actualiza las rutas en `restaurant.config.ts`:

```typescript
branding: {
  logo: "/assets/logo.png",
  favicon: "/assets/favicon.ico"
}
```

## 📱 Características del Restaurante

Habilita o deshabilita funcionalidades según tu negocio:

```typescript
features: {
  enableDelivery: true,        // Servicio a domicilio
  enablePickup: true,          // Pedidos para recoger
  enableDineIn: true,          // Comer en el restaurante
  enableReservations: false,   // Sistema de reservas
  enableLoyaltyProgram: false, // Programa de lealtad
  enableOnlinePayments: true   // Pagos en línea
}
```

## 🌍 Configuración Regional

### Moneda e Impuestos

```typescript
legal: {
  currency: "CRC",     // Código de moneda (CRC, USD, EUR, etc.)
  taxRate: 13          // Porcentaje de impuestos
}
```

### Horarios por País

Ajusta los horarios según las costumbres locales:

```typescript
hours: {
  monday: "11:00 AM - 10:00 PM",
  tuesday: "11:00 AM - 10:00 PM",
  // ... resto de días
}
```

## 📧 Configuración de Emails

### Para Gmail:

1. **Habilita la verificación en 2 pasos** en tu cuenta de Gmail
2. **Genera una contraseña de aplicación**:
   - Ve a [Google Account Security](https://myaccount.google.com/security)
   - Busca "Contraseñas de aplicaciones"
   - Genera una nueva para "Correo"
3. **Configura el archivo .env**:

```bash
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=tu-email@gmail.com
EMAIL_PASSWORD=tu-contraseña-de-aplicacion-de-16-caracteres
```

### Para Outlook/Hotmail:

```bash
EMAIL_HOST=smtp-mail.outlook.com
EMAIL_PORT=587
EMAIL_USERNAME=tu-email@outlook.com
EMAIL_PASSWORD=tu-contraseña
```

### Para otros proveedores:

Consulta la documentación de tu proveedor de email para obtener los valores de `EMAIL_HOST` y `EMAIL_PORT`.

## 🔄 Aplicar Cambios

### Desarrollo:
1. Modifica los archivos de configuración
2. Reinicia el servidor de desarrollo: `npm start`

### Producción:
1. Modifica los archivos de configuración
2. Reconstruye la aplicación: `npm run build`
3. Reinicia los servicios

## 📁 Archivos de Configuración

```
Restaurant-App/
├── .env                                    # Configuración del backend
├── .env.example                           # Plantilla de configuración
├── frontend/src/config/
│   └── restaurant.config.ts              # Configuración del frontend
└── RESTAURANT-CONFIG.md                  # Esta documentación
```

## ✅ Lista de Verificación

Antes de desplegar para un nuevo restaurante:

- [ ] ✏️ Actualizar nombre y descripción del restaurante
- [ ] 📞 Configurar información de contacto
- [ ] 📍 Actualizar dirección completa
- [ ] ⏰ Establecer horarios de operación
- [ ] 🎨 Personalizar colores de marca
- [ ] 📧 Configurar email para notificaciones
- [ ] 🌐 Actualizar enlaces de redes sociales
- [ ] 💰 Configurar moneda e impuestos locales
- [ ] 🔧 Habilitar/deshabilitar características según el negocio
- [ ] 🖼️ Subir logo y favicon personalizados
- [ ] 🧪 Probar envío de emails
- [ ] 📱 Verificar diseño en dispositivos móviles

## 🆘 Soporte

Si necesitas ayuda con la configuración:

1. Revisa que todos los archivos estén correctamente editados
2. Verifica que las variables de entorno estén cargadas
3. Comprueba que los servicios estén reiniciados
4. Consulta los logs para detectar errores

## 🔮 Futuras Mejoras

Esta configuración está diseñada para ser extensible:

- **Configuración desde base de datos**: Cargar configuración desde una API
- **Multi-restaurante**: Soporte para múltiples restaurantes en una instalación
- **Temas dinámicos**: Cambio de temas desde el panel de administración
- **Configuración por geolocalización**: Ajustes automáticos según la ubicación

---

¡Con estos pasos tendrás tu restaurante completamente personalizado! 🎉
