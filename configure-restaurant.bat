@echo off
setlocal enabledelayedexpansion

REM ======================================
REM 🍽️ Script de Configuración del Restaurante
REM ======================================

echo 🍽️ ¡Bienvenido al configurador de Restaurant App!
echo.
echo Este script te ayudará a personalizar la aplicación para tu restaurante.
echo Podrás cambiar esta información en cualquier momento desde el panel de administración.
echo.

REM ======================================
REM INFORMACIÓN BÁSICA DEL RESTAURANTE
REM ======================================
echo 📋 INFORMACIÓN BÁSICA
echo --------------------

set /p "RESTAURANT_NAME=Nombre del restaurante [Mi Restaurante]: "
if "!RESTAURANT_NAME!"=="" set "RESTAURANT_NAME=Mi Restaurante"

set /p "RESTAURANT_PHONE=Teléfono [+506 1234-5678]: "
if "!RESTAURANT_PHONE!"=="" set "RESTAURANT_PHONE=+506 1234-5678"

set /p "RESTAURANT_EMAIL=Email [info@mirestaurante.com]: "
if "!RESTAURANT_EMAIL!"=="" set "RESTAURANT_EMAIL=info@mirestaurante.com"

set /p "RESTAURANT_ADDRESS=Dirección completa [Calle Principal 123, Ciudad, País]: "
if "!RESTAURANT_ADDRESS!"=="" set "RESTAURANT_ADDRESS=Calle Principal 123, Ciudad, País"

set /p "RESTAURANT_WEBSITE=Sitio web (opcional): "

echo.

REM ======================================
REM CONFIGURACIÓN DE EMAIL
REM ======================================
echo 📧 CONFIGURACIÓN DE EMAIL
echo -------------------------
echo Para enviar emails automáticos (bienvenida, promociones, etc.)

set /p "EMAIL_USERNAME=Email para enviar notificaciones [!RESTAURANT_EMAIL!]: "
if "!EMAIL_USERNAME!"=="" set "EMAIL_USERNAME=!RESTAURANT_EMAIL!"

echo.
echo ⚠️  Para Gmail, necesitas una contraseña de aplicación:
echo    1. Ve a https://myaccount.google.com/security
echo    2. Activa la verificación en 2 pasos
echo    3. Genera una 'Contraseña de aplicación'
echo    4. Usa esa contraseña aquí (NO tu contraseña normal)
echo.

set /p "EMAIL_PASSWORD=Contraseña de email (o contraseña de aplicación): "

REM Detectar proveedor de email
echo !EMAIL_USERNAME! | findstr "@gmail.com" >nul
if !errorlevel!==0 (
    set "EMAIL_HOST=smtp.gmail.com"
    set "EMAIL_PORT=587"
) else (
    echo !EMAIL_USERNAME! | findstr "@outlook.com @hotmail.com" >nul
    if !errorlevel!==0 (
        set "EMAIL_HOST=smtp-mail.outlook.com"
        set "EMAIL_PORT=587"
    ) else (
        echo !EMAIL_USERNAME! | findstr "@yahoo.com" >nul
        if !errorlevel!==0 (
            set "EMAIL_HOST=smtp.mail.yahoo.com"
            set "EMAIL_PORT=587"
        ) else (
            set /p "EMAIL_HOST=Host SMTP [smtp.gmail.com]: "
            if "!EMAIL_HOST!"=="" set "EMAIL_HOST=smtp.gmail.com"
            set /p "EMAIL_PORT=Puerto SMTP [587]: "
            if "!EMAIL_PORT!"=="" set "EMAIL_PORT=587"
        )
    )
)

echo.

REM ======================================
REM REDES SOCIALES
REM ======================================
echo 📱 REDES SOCIALES (OPCIONAL)
echo ----------------------------

set /p "RESTAURANT_FACEBOOK=Facebook: "
set /p "RESTAURANT_INSTAGRAM=Instagram: "
set /p "RESTAURANT_TWITTER=Twitter: "

echo.

REM ======================================
REM CONFIGURACIÓN REGIONAL
REM ======================================
echo 🌍 CONFIGURACIÓN REGIONAL
echo ------------------------

echo Selecciona tu moneda:
echo 1) CRC - Colón Costarricense
echo 2) USD - Dólar Estadounidense
echo 3) EUR - Euro
echo 4) MXN - Peso Mexicano
echo 5) GTQ - Quetzal Guatemalteco
echo 6) Otro

set /p "currency_option=Opción [1]: "
if "!currency_option!"=="" set "currency_option=1"

if "!currency_option!"=="2" set "RESTAURANT_CURRENCY=USD"
if "!currency_option!"=="3" set "RESTAURANT_CURRENCY=EUR"
if "!currency_option!"=="4" set "RESTAURANT_CURRENCY=MXN"
if "!currency_option!"=="5" set "RESTAURANT_CURRENCY=GTQ"
if "!currency_option!"=="6" (
    set /p "RESTAURANT_CURRENCY=Código de moneda [USD]: "
    if "!RESTAURANT_CURRENCY!"=="" set "RESTAURANT_CURRENCY=USD"
)
if "!currency_option!"=="1" set "RESTAURANT_CURRENCY=CRC"

set /p "RESTAURANT_TAX_RATE=Tasa de impuesto (%%) [13]: "
if "!RESTAURANT_TAX_RATE!"=="" set "RESTAURANT_TAX_RATE=13"

echo.

REM ======================================
REM CARACTERÍSTICAS DEL RESTAURANTE
REM ======================================
echo ⚙️  CARACTERÍSTICAS DEL RESTAURANTE
echo ----------------------------------

set /p "enable_dine_in=¿Ofreces servicio en el restaurante? (s/N): "
if /i "!enable_dine_in!"=="s" (set "ENABLE_DINE_IN=true") else (set "ENABLE_DINE_IN=false")

set /p "enable_pickup=¿Ofreces pedidos para llevar? (S/n): "
if /i "!enable_pickup!"=="n" (set "ENABLE_PICKUP=false") else (set "ENABLE_PICKUP=true")

set /p "enable_delivery=¿Ofreces servicio a domicilio? (S/n): "
if /i "!enable_delivery!"=="n" (set "ENABLE_DELIVERY=false") else (set "ENABLE_DELIVERY=true")

set /p "enable_reservations=¿Tienes sistema de reservas? (s/N): "
if /i "!enable_reservations!"=="s" (set "ENABLE_RESERVATIONS=true") else (set "ENABLE_RESERVATIONS=false")

set /p "enable_payments=¿Aceptas pagos en línea? (S/n): "
if /i "!enable_payments!"=="n" (set "ENABLE_ONLINE_PAYMENTS=false") else (set "ENABLE_ONLINE_PAYMENTS=true")

echo.

REM ======================================
REM GENERAR ARCHIVO .env
REM ======================================
echo 💾 GENERANDO CONFIGURACIÓN...

(
echo # ==========================================
echo # CONFIGURACIÓN DE EMAIL
echo # ==========================================
echo EMAIL_HOST=!EMAIL_HOST!
echo EMAIL_PORT=!EMAIL_PORT!
echo EMAIL_USERNAME=!EMAIL_USERNAME!
echo EMAIL_PASSWORD=!EMAIL_PASSWORD!
echo.
echo # ==========================================
echo # INFORMACIÓN DEL RESTAURANTE
echo # ==========================================
echo RESTAURANT_NAME=!RESTAURANT_NAME!
echo RESTAURANT_PHONE=!RESTAURANT_PHONE!
echo RESTAURANT_ADDRESS=!RESTAURANT_ADDRESS!
echo RESTAURANT_EMAIL=!RESTAURANT_EMAIL!
echo RESTAURANT_WEBSITE=!RESTAURANT_WEBSITE!
echo.
echo # ==========================================
echo # CONFIGURACIÓN REGIONAL
echo # ==========================================
echo RESTAURANT_CURRENCY=!RESTAURANT_CURRENCY!
echo RESTAURANT_TAX_RATE=!RESTAURANT_TAX_RATE!
echo RESTAURANT_TIMEZONE=America/Costa_Rica
echo RESTAURANT_LANGUAGE=es
echo.
echo # ==========================================
echo # REDES SOCIALES (OPCIONAL^)
echo # ==========================================
echo RESTAURANT_FACEBOOK=!RESTAURANT_FACEBOOK!
echo RESTAURANT_INSTAGRAM=!RESTAURANT_INSTAGRAM!
echo RESTAURANT_TWITTER=!RESTAURANT_TWITTER!
echo.
echo # ==========================================
echo # CONFIGURACIÓN DE LA APLICACIÓN
echo # ==========================================
echo ENABLE_DELIVERY=!ENABLE_DELIVERY!
echo ENABLE_PICKUP=!ENABLE_PICKUP!
echo ENABLE_DINE_IN=!ENABLE_DINE_IN!
echo ENABLE_RESERVATIONS=!ENABLE_RESERVATIONS!
echo ENABLE_ONLINE_PAYMENTS=!ENABLE_ONLINE_PAYMENTS!
) > .env

echo.
echo ✅ ¡CONFIGURACIÓN COMPLETADA!
echo.
echo 📁 Se ha generado el archivo .env con tu configuración
echo 🔄 Para aplicar los cambios:
echo.
echo    1. Si estás en desarrollo:
echo       npm start (frontend) y ./mvnw spring-boot:run (backend)
echo.
echo    2. Si estás en producción:
echo       build-no-cache.bat
echo.
echo 📝 NOTAS IMPORTANTES:
echo    - Puedes cambiar esta configuración en cualquier momento
echo    - Edita el archivo .env o usa el panel de administración
echo    - Para Gmail, asegúrate de usar una contraseña de aplicación
echo    - Todas las características se pueden habilitar/deshabilitar dinámicamente
echo.
echo 🎉 ¡Tu restaurante está listo para funcionar!
echo.

REM Preguntar si quiere iniciar la aplicación
set /p "start_app=¿Quieres iniciar la aplicación ahora? (S/n): "

if /i not "!start_app!"=="n" (
    echo.
    echo 🚀 Iniciando aplicación...
    
    if exist "start-app.bat" (
        call start-app.bat
    ) else (
        echo ⚠️  No se encontró start-app.bat
        echo    Inicia manualmente con:
        echo    - Backend: cd backend ^&^& mvnw spring-boot:run
        echo    - Frontend: cd frontend ^&^& npm start
    )
)

pause
