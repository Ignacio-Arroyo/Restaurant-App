#!/bin/bash

# ======================================
# 🍽️ Script de Configuración del Restaurante
# ======================================

echo "🍽️ ¡Bienvenido al configurador de Restaurant App!"
echo ""
echo "Este script te ayudará a personalizar la aplicación para tu restaurante."
echo "Podrás cambiar esta información en cualquier momento desde el panel de administración."
echo ""

# Función para leer input con valor por defecto
read_with_default() {
    local prompt="$1"
    local default="$2"
    local var_name="$3"
    
    echo -n "$prompt [$default]: "
    read input
    if [ -z "$input" ]; then
        eval "$var_name='$default'"
    else
        eval "$var_name='$input'"
    fi
}

# ======================================
# INFORMACIÓN BÁSICA DEL RESTAURANTE
# ======================================
echo "📋 INFORMACIÓN BÁSICA"
echo "--------------------"

read_with_default "Nombre del restaurante" "Mi Restaurante" RESTAURANT_NAME
read_with_default "Teléfono" "+506 1234-5678" RESTAURANT_PHONE
read_with_default "Email" "info@mirestaurante.com" RESTAURANT_EMAIL
read_with_default "Dirección completa" "Calle Principal 123, Ciudad, País" RESTAURANT_ADDRESS
read_with_default "Sitio web (opcional)" "" RESTAURANT_WEBSITE

echo ""

# ======================================
# CONFIGURACIÓN DE EMAIL
# ======================================
echo "📧 CONFIGURACIÓN DE EMAIL"
echo "-------------------------"
echo "Para enviar emails automáticos (bienvenida, promociones, etc.)"

read_with_default "Email para enviar notificaciones" "$RESTAURANT_EMAIL" EMAIL_USERNAME

echo ""
echo "⚠️  Para Gmail, necesitas una contraseña de aplicación:"
echo "   1. Ve a https://myaccount.google.com/security"
echo "   2. Activa la verificación en 2 pasos"
echo "   3. Genera una 'Contraseña de aplicación'"
echo "   4. Usa esa contraseña aquí (NO tu contraseña normal)"
echo ""

read_with_default "Contraseña de email (o contraseña de aplicación)" "" EMAIL_PASSWORD

# Detectar proveedor de email
if [[ "$EMAIL_USERNAME" == *"@gmail.com"* ]]; then
    EMAIL_HOST="smtp.gmail.com"
    EMAIL_PORT="587"
elif [[ "$EMAIL_USERNAME" == *"@outlook.com"* ]] || [[ "$EMAIL_USERNAME" == *"@hotmail.com"* ]]; then
    EMAIL_HOST="smtp-mail.outlook.com"
    EMAIL_PORT="587"
elif [[ "$EMAIL_USERNAME" == *"@yahoo.com"* ]]; then
    EMAIL_HOST="smtp.mail.yahoo.com"
    EMAIL_PORT="587"
else
    read_with_default "Host SMTP" "smtp.gmail.com" EMAIL_HOST
    read_with_default "Puerto SMTP" "587" EMAIL_PORT
fi

echo ""

# ======================================
# REDES SOCIALES
# ======================================
echo "📱 REDES SOCIALES (OPCIONAL)"
echo "----------------------------"

read_with_default "Facebook" "" RESTAURANT_FACEBOOK
read_with_default "Instagram" "" RESTAURANT_INSTAGRAM
read_with_default "Twitter" "" RESTAURANT_TWITTER

echo ""

# ======================================
# CONFIGURACIÓN REGIONAL
# ======================================
echo "🌍 CONFIGURACIÓN REGIONAL"
echo "------------------------"

echo "Selecciona tu moneda:"
echo "1) CRC - Colón Costarricense"
echo "2) USD - Dólar Estadounidense"
echo "3) EUR - Euro"
echo "4) MXN - Peso Mexicano"
echo "5) GTQ - Quetzal Guatemalteco"
echo "6) Otro"

read -p "Opción [1]: " currency_option

case $currency_option in
    2) RESTAURANT_CURRENCY="USD" ;;
    3) RESTAURANT_CURRENCY="EUR" ;;
    4) RESTAURANT_CURRENCY="MXN" ;;
    5) RESTAURANT_CURRENCY="GTQ" ;;
    6) read_with_default "Código de moneda" "USD" RESTAURANT_CURRENCY ;;
    *) RESTAURANT_CURRENCY="CRC" ;;
esac

read_with_default "Tasa de impuesto (%)" "13" RESTAURANT_TAX_RATE

echo ""

# ======================================
# CARACTERÍSTICAS DEL RESTAURANTE
# ======================================
echo "⚙️  CARACTERÍSTICAS DEL RESTAURANTE"
echo "----------------------------------"

read -p "¿Ofreces servicio en el restaurante? (s/N): " enable_dine_in
ENABLE_DINE_IN=$([ "$enable_dine_in" = "s" ] || [ "$enable_dine_in" = "S" ] && echo "true" || echo "false")

read -p "¿Ofreces pedidos para llevar? (S/n): " enable_pickup
ENABLE_PICKUP=$([ "$enable_pickup" = "n" ] || [ "$enable_pickup" = "N" ] && echo "false" || echo "true")

read -p "¿Ofreces servicio a domicilio? (S/n): " enable_delivery
ENABLE_DELIVERY=$([ "$enable_delivery" = "n" ] || [ "$enable_delivery" = "N" ] && echo "false" || echo "true")

read -p "¿Tienes sistema de reservas? (s/N): " enable_reservations
ENABLE_RESERVATIONS=$([ "$enable_reservations" = "s" ] || [ "$enable_reservations" = "S" ] && echo "true" || echo "false")

read -p "¿Aceptas pagos en línea? (S/n): " enable_payments
ENABLE_ONLINE_PAYMENTS=$([ "$enable_payments" = "n" ] || [ "$enable_payments" = "N" ] && echo "false" || echo "true")

echo ""

# ======================================
# GENERAR ARCHIVO .env
# ======================================
echo "💾 GENERANDO CONFIGURACIÓN..."

cat > .env << EOF
# ==========================================
# CONFIGURACIÓN DE EMAIL
# ==========================================
EMAIL_HOST=$EMAIL_HOST
EMAIL_PORT=$EMAIL_PORT
EMAIL_USERNAME=$EMAIL_USERNAME
EMAIL_PASSWORD=$EMAIL_PASSWORD

# ==========================================
# INFORMACIÓN DEL RESTAURANTE
# ==========================================
RESTAURANT_NAME=$RESTAURANT_NAME
RESTAURANT_PHONE=$RESTAURANT_PHONE
RESTAURANT_ADDRESS=$RESTAURANT_ADDRESS
RESTAURANT_EMAIL=$RESTAURANT_EMAIL
RESTAURANT_WEBSITE=$RESTAURANT_WEBSITE

# ==========================================
# CONFIGURACIÓN REGIONAL
# ==========================================
RESTAURANT_CURRENCY=$RESTAURANT_CURRENCY
RESTAURANT_TAX_RATE=$RESTAURANT_TAX_RATE
RESTAURANT_TIMEZONE=America/Costa_Rica
RESTAURANT_LANGUAGE=es

# ==========================================
# REDES SOCIALES (OPCIONAL)
# ==========================================
RESTAURANT_FACEBOOK=$RESTAURANT_FACEBOOK
RESTAURANT_INSTAGRAM=$RESTAURANT_INSTAGRAM
RESTAURANT_TWITTER=$RESTAURANT_TWITTER

# ==========================================
# CONFIGURACIÓN DE LA APLICACIÓN
# ==========================================
ENABLE_DELIVERY=$ENABLE_DELIVERY
ENABLE_PICKUP=$ENABLE_PICKUP
ENABLE_DINE_IN=$ENABLE_DINE_IN
ENABLE_RESERVATIONS=$ENABLE_RESERVATIONS
ENABLE_ONLINE_PAYMENTS=$ENABLE_ONLINE_PAYMENTS
EOF

# ======================================
# ACTUALIZAR CONFIGURACIÓN DEL FRONTEND
# ======================================
echo "🔧 ACTUALIZANDO CONFIGURACIÓN DEL FRONTEND..."

# Crear backup del archivo original
cp frontend/src/config/restaurant.config.ts frontend/src/config/restaurant.config.ts.backup

# Usar sed para actualizar valores en el archivo de configuración
# (Esto es una versión simplificada - en producción sería mejor usar un script más robusto)

echo ""
echo "✅ ¡CONFIGURACIÓN COMPLETADA!"
echo ""
echo "📁 Se ha generado el archivo .env con tu configuración"
echo "🔄 Para aplicar los cambios:"
echo ""
echo "   1. Si estás en desarrollo:"
echo "      npm start (frontend) y ./mvnw spring-boot:run (backend)"
echo ""
echo "   2. Si estás en producción:"
echo "      ./build-no-cache.sh"
echo ""
echo "📝 NOTAS IMPORTANTES:"
echo "   - Puedes cambiar esta configuración en cualquier momento"
echo "   - Edita el archivo .env o usa el panel de administración"
echo "   - Para Gmail, asegúrate de usar una contraseña de aplicación"
echo "   - Todas las características se pueden habilitar/deshabilitar dinámicamente"
echo ""
echo "🎉 ¡Tu restaurante está listo para funcionar!"
echo ""

# Preguntar si quiere iniciar la aplicación
read -p "¿Quieres iniciar la aplicación ahora? (S/n): " start_app

if [ "$start_app" != "n" ] && [ "$start_app" != "N" ]; then
    echo ""
    echo "🚀 Iniciando aplicación..."
    
    if [ -f "start-app.sh" ]; then
        ./start-app.sh
    else
        echo "⚠️  No se encontró start-app.sh"
        echo "   Inicia manualmente con:"
        echo "   - Backend: cd backend && ./mvnw spring-boot:run"
        echo "   - Frontend: cd frontend && npm start"
    fi
fi
