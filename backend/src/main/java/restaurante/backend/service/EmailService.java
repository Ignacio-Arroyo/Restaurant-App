package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import restaurante.backend.entity.Order;
import restaurante.backend.entity.OrderMeal;
import restaurante.backend.entity.OrderDrink;
import restaurante.backend.entity.User;
import restaurante.backend.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsentService consentService;

    @Value("${restaurant.email:noreply@restaurant.com}")
    private String fromEmail;

    @Value("${restaurant.name:Restaurant App}")
    private String restaurantName;

    @Value("${restaurant.phone:+1 (555) 123-4567}")
    private String restaurantPhone;

    @Value("${restaurant.address:123 Restaurant Street, Food City, FC 12345}")
    private String restaurantAddress;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Async("emailTaskExecutor")
    public void sendWelcomeEmail(String toEmail, String firstName, String lastName) {
        try {
            logger.info("Sending welcome email to: {}", toEmail);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("¡Bienvenido a " + restaurantName + "!");

            String htmlContent = buildWelcomeEmailContent(firstName, lastName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Welcome email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Error sending welcome email to {}: {}", toEmail, e.getMessage());
            // No lanzamos la excepción para que no afecte el registro del usuario
        } catch (Exception e) {
            logger.error("Unexpected error sending welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async("emailTaskExecutor")
    public void sendSimpleWelcomeEmail(String toEmail, String firstName, String lastName) {
        try {
            logger.info("Sending simple welcome email to: {}", toEmail);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("¡Bienvenido a " + restaurantName + "!");
            
            String content = buildSimpleWelcomeContent(firstName, lastName);
            message.setText(content);

            mailSender.send(message);
            logger.info("Simple welcome email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Error sending simple welcome email to {}: {}", toEmail, e.getMessage());
            // No lanzamos la excepción para que no afecte el registro del usuario
        }
    }

    private String buildWelcomeEmailContent(String firstName, String lastName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #d32f2f; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { background-color: #333; color: white; padding: 15px; text-align: center; font-size: 12px; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #d32f2f; color: white; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Bienvenido a %s!</h1>
                    </div>
                    <div class="content">
                        <h2>¡Hola %s %s!</h2>
                        <p>¡Gracias por registrarte en <strong>%s</strong>! Nos complace tenerte como parte de nuestra familia.</p>
                        
                        <p>Con tu cuenta ahora puedes:</p>
                        <ul>
                            <li>🍽️ Explorar nuestro delicioso menú</li>
                            <li>🛒 Realizar pedidos fácilmente</li>
                            <li>📱 Hacer seguimiento a tus órdenes</li>
                            <li>⭐ Disfrutar de ofertas exclusivas</li>
                        </ul>
                        
                        <p style="text-align: center;">
                            <a href="http://192.168.1.152:3000/menu" class="button">Ver Nuestro Menú</a>
                        </p>
                        
                        <p>Si tienes alguna pregunta o necesitas ayuda, no dudes en contactarnos.</p>
                        
                        <p>¡Esperamos verte pronto!</p>
                        <p><strong>El equipo de %s</strong></p>
                    </div>
                    <div class="footer">
                        <p><strong>%s</strong></p>
                        <p>📍 %s</p>
                        <p>📞 %s</p>
                        <p>Este es un correo automático, por favor no responder directamente a este mensaje.</p>
                    </div>
                </div>
            </body>
            </html>
            """, 
            restaurantName, firstName, lastName, restaurantName, restaurantName,
            restaurantName, restaurantAddress, restaurantPhone);
    }

    private String buildSimpleWelcomeContent(String firstName, String lastName) {
        return String.format("""
            ¡Hola %s %s!
            
            ¡Bienvenido a %s!
            
            Gracias por registrarte en nuestra plataforma. Ahora puedes:
            
            • Explorar nuestro menú completo
            • Realizar pedidos fácilmente
            • Hacer seguimiento a tus órdenes
            • Disfrutar de ofertas exclusivas
            
            Visita nuestro menú en: http://192.168.1.152:3000/menu
            
            Si tienes alguna pregunta, contáctanos:
            📍 %s
            📞 %s
            
            ¡Esperamos verte pronto!
            
            Saludos,
            El equipo de %s
            
            ---
            Este es un correo automático, por favor no responder.
            """, 
            firstName, lastName, restaurantName, restaurantAddress, 
            restaurantPhone, restaurantName);
    }

    @Async("emailTaskExecutor")
    public void sendOrderConfirmationEmail(Order order) {
        try {
            if (order.getUser() == null || order.getUser().getEmail() == null) {
                logger.warn("Cannot send order confirmation email: user or email is null for order {}", order.getId());
                return;
            }

            logger.info("Sending order confirmation email to: {} for order {}", order.getUser().getEmail(), order.getId());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("¡Pedido Confirmado! - Orden #" + order.getId());

            String htmlContent = buildOrderConfirmationContent(order);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Order confirmation email sent successfully to: {} for order {}", order.getUser().getEmail(), order.getId());

        } catch (MessagingException e) {
            logger.error("Error sending order confirmation email to {} for order {}: {}", 
                order.getUser() != null ? order.getUser().getEmail() : "null", order.getId(), e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending order confirmation email to {} for order {}: {}", 
                order.getUser() != null ? order.getUser().getEmail() : "null", order.getId(), e.getMessage());
        }
    }

    @Async("emailTaskExecutor")
    public void sendOrderReadyEmail(Order order) {
        try {
            if (order.getUser() == null || order.getUser().getEmail() == null) {
                logger.warn("Cannot send order ready email: user or email is null for order {}", order.getId());
                return;
            }

            logger.info("Sending order ready email to: {} for order {}", order.getUser().getEmail(), order.getId());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("¡Tu Pedido Está Listo! - Orden #" + order.getId());

            String htmlContent = buildOrderReadyContent(order);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Order ready email sent successfully to: {} for order {}", order.getUser().getEmail(), order.getId());

        } catch (MessagingException e) {
            logger.error("Error sending order ready email to {} for order {}: {}", 
                order.getUser() != null ? order.getUser().getEmail() : "null", order.getId(), e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending order ready email to {} for order {}: {}", 
                order.getUser() != null ? order.getUser().getEmail() : "null", order.getId(), e.getMessage());
        }
    }

    private String buildOrderConfirmationContent(Order order) {
        StringBuilder itemsHtml = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;

        // Agregar comidas
        if (order.getOrderMeals() != null) {
            for (OrderMeal orderMeal : order.getOrderMeals()) {
                BigDecimal itemTotal = orderMeal.getMeal().getPrice().multiply(BigDecimal.valueOf(orderMeal.getQuantity()));
                itemsHtml.append(String.format("""
                    <tr>
                        <td style="padding: 8px; border-bottom: 1px solid #eee;">🍽️ %s</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: center;">%d</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: right;">$%.2f</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: right;">$%.2f</td>
                    </tr>
                    """, 
                    orderMeal.getMeal().getName(), 
                    orderMeal.getQuantity(), 
                    orderMeal.getMeal().getPrice(), 
                    itemTotal));
                total = total.add(itemTotal);
            }
        }

        // Agregar bebidas
        if (order.getOrderDrinks() != null) {
            for (OrderDrink orderDrink : order.getOrderDrinks()) {
                BigDecimal itemTotal = orderDrink.getDrink().getPrice().multiply(BigDecimal.valueOf(orderDrink.getQuantity()));
                itemsHtml.append(String.format("""
                    <tr>
                        <td style="padding: 8px; border-bottom: 1px solid #eee;">🥤 %s</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: center;">%d</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: right;">$%.2f</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: right;">$%.2f</td>
                    </tr>
                    """, 
                    orderDrink.getDrink().getName(), 
                    orderDrink.getQuantity(), 
                    orderDrink.getDrink().getPrice(), 
                    itemTotal));
                total = total.add(itemTotal);
            }
        }

        String orderTypeText = order.getOrderType().toString().equals("DINE_IN") ? 
            "Para comer en el restaurante" + (order.getTableNumber() != null ? " (Mesa " + order.getTableNumber() + ")" : "") :
            "Para llevar";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #d32f2f; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { background-color: #333; color: white; padding: 15px; text-align: center; font-size: 12px; }
                    .order-table { width: 100%%; border-collapse: collapse; margin: 15px 0; }
                    .order-table th { background-color: #d32f2f; color: white; padding: 10px; text-align: left; }
                    .total-row { font-weight: bold; background-color: #f0f0f0; }
                    .status-badge { background-color: #ff9800; color: white; padding: 5px 10px; border-radius: 15px; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Pedido Confirmado!</h1>
                        <p>Orden #%d</p>
                    </div>
                    <div class="content">
                        <h2>¡Hola %s!</h2>
                        <p>¡Gracias por tu pedido! Hemos recibido tu orden y ya estamos <strong>preparándola</strong> con mucho cariño.</p>
                        
                        <div style="text-align: center; margin: 20px 0;">
                            <span class="status-badge">🍳 PREPARANDO</span>
                        </div>

                        <h3>📋 Detalles de tu pedido:</h3>
                        <p><strong>Tipo de orden:</strong> %s</p>
                        <p><strong>Fecha:</strong> %s</p>
                        
                        <table class="order-table">
                            <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th style="text-align: center;">Cantidad</th>
                                    <th style="text-align: right;">Precio Unit.</th>
                                    <th style="text-align: right;">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                %s
                                <tr class="total-row">
                                    <td colspan="3" style="padding: 12px; text-align: right;"><strong>TOTAL:</strong></td>
                                    <td style="padding: 12px; text-align: right;"><strong>$%.2f</strong></td>
                                </tr>
                            </tbody>
                        </table>

                        <p><strong>⏱️ Tiempo estimado de preparación:</strong> 15-25 minutos</p>
                        
                        <p>Te notificaremos por email cuando tu pedido esté listo para recoger.</p>
                        
                        <p>¡Gracias por elegirnos!</p>
                        <p><strong>El equipo de %s</strong></p>
                    </div>
                    <div class="footer">
                        <p><strong>%s</strong></p>
                        <p>📍 %s</p>
                        <p>📞 %s</p>
                    </div>
                </div>
            </body>
            </html>
            """, 
            order.getId(),
            order.getUser().getFirstName(),
            orderTypeText,
            order.getOrderDate().toString().substring(0, 16).replace("T", " a las "),
            itemsHtml.toString(),
            order.getTotalCost(),
            restaurantName,
            restaurantName,
            restaurantAddress,
            restaurantPhone);
    }

    private String buildOrderReadyContent(Order order) {
        String orderTypeText = order.getOrderType().toString().equals("DINE_IN") ? 
            "en el restaurante" + (order.getTableNumber() != null ? " (Mesa " + order.getTableNumber() + ")" : "") :
            "en el mostrador para llevar";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4caf50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { background-color: #333; color: white; padding: 15px; text-align: center; font-size: 12px; }
                    .status-badge { background-color: #4caf50; color: white; padding: 8px 15px; border-radius: 20px; font-size: 14px; font-weight: bold; }
                    .highlight-box { background-color: #e8f5e8; border-left: 4px solid #4caf50; padding: 15px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 ¡Tu Pedido Está Listo!</h1>
                        <p>Orden #%d</p>
                    </div>
                    <div class="content">
                        <h2>¡Hola %s!</h2>
                        
                        <div style="text-align: center; margin: 20px 0;">
                            <span class="status-badge">✅ LISTO PARA RECOGER</span>
                        </div>

                        <div class="highlight-box">
                            <h3>🎯 Tu pedido está listo para recoger %s</h3>
                            <p><strong>Total:</strong> $%.2f</p>
                        </div>

                        <p>¡Tu deliciosa comida te está esperando! Por favor dirígete a nuestro restaurante para recoger tu orden.</p>
                        
                        <p><strong>📍 Ubicación:</strong> %s</p>
                        <p><strong>📞 Teléfono:</strong> %s</p>
                        
                        <p><strong>⏰ Horario de retiro:</strong> Lunes a Domingo, 11:00 AM - 10:00 PM</p>

                        <p><strong>🕐 Nota importante:</strong> Tu pedido se mantendrá caliente por 30 minutos. Si no puedes venir en ese tiempo, por favor llámanos.</p>
                        
                        <p>¡Esperamos verte pronto y que disfrutes mucho tu comida!</p>
                        <p><strong>El equipo de %s</strong></p>
                    </div>
                    <div class="footer">
                        <p><strong>%s</strong></p>
                        <p>📍 %s</p>
                        <p>📞 %s</p>
                        <p>¡Gracias por elegirnos!</p>
                    </div>
                </div>
            </body>
            </html>
            """, 
            order.getId(),
            order.getUser().getFirstName(),
            orderTypeText,
            order.getTotalCost(),
            restaurantAddress,
            restaurantPhone,
            restaurantName,
            restaurantName,
            restaurantAddress,
            restaurantPhone);
    }

    @Async("emailTaskExecutor")
    public void sendMarketingEmail(String subject, String content, List<String> recipients) {
        logger.info("Starting marketing email campaign to {} recipients", recipients.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (String email : recipients) {
            try {
                logger.info("Sending marketing email to: {}", email);
                
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(email);
                helper.setSubject(subject);

                String htmlContent = buildMarketingEmailContent(subject, content, email);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                successCount++;
                logger.info("Marketing email sent successfully to: {}", email);

                // Pequeña pausa entre emails para no sobrecargar el servidor SMTP
                Thread.sleep(100);

            } catch (Exception e) {
                failureCount++;
                logger.error("Error sending marketing email to {}: {}", email, e.getMessage());
            }
        }
        
        logger.info("Marketing campaign completed. Success: {}, Failures: {}", successCount, failureCount);
    }

    public void sendMarketingEmailToAllCustomers(String subject, String content) {
        try {
            // Obtener solo usuarios que han dado consentimiento para marketing
            List<User> customers = consentService.getUsersWithMarketingConsent();
            List<String> emails = customers.stream()
                .map(User::getEmail)
                .filter(email -> email != null && !email.isEmpty())
                .toList();
            
            if (emails.isEmpty()) {
                logger.warn("No customers with marketing consent found for campaign");
                return;
            }
            
            logger.info("Starting marketing campaign to {} customers with marketing consent", emails.size());
            sendMarketingEmail(subject, content, emails);
            
        } catch (Exception e) {
            logger.error("Error retrieving customers with marketing consent: {}", e.getMessage());
        }
    }

    private String buildMarketingEmailContent(String subject, String content, String recipientEmail) {
        // Convertir saltos de línea a <br> para HTML
        String htmlContent = content.replace("\n", "<br>");
        
        // Crear URL de unsubscribe
        String unsubscribeUrl = frontendUrl + "/unsubscribe?email=" + recipientEmail;
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #d32f2f; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px 20px; background-color: #f9f9f9; }
                    .footer { background-color: #333; color: white; padding: 15px; text-align: center; font-size: 12px; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #d32f2f; color: white; text-decoration: none; border-radius: 5px; margin: 15px 0; }
                    .unsubscribe { font-size: 11px; color: #666; margin-top: 20px; padding: 15px; background-color: #f0f0f0; border-radius: 5px; }
                    .unsubscribe a { color: #d32f2f; text-decoration: none; }
                    .gdpr-notice { font-size: 10px; color: #999; margin-top: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                        <p>%s</p>
                    </div>
                    <div class="content">
                        <div style="font-size: 16px; line-height: 1.8;">
                            %s
                        </div>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s/menu" class="button">Ver Nuestro Menú</a>
                        </div>
                        
                        <p style="text-align: center; margin-top: 30px;">
                            <strong>¡Gracias por ser parte de nuestra familia!</strong><br>
                            El equipo de %s
                        </p>
                        
                        <div class="unsubscribe">
                            <p><strong>Gestión de Suscripción</strong></p>
                            <p>Has recibido este email porque has dado tu consentimiento para recibir comunicaciones promocionales de %s.</p>
                            <p>Si no deseas recibir más emails promocionales, puedes <a href="%s">darte de baja aquí</a>.</p>
                            <p>También puedes gestionar tus preferencias de comunicación desde tu <a href="%s/profile">perfil de usuario</a>.</p>
                            
                            <div class="gdpr-notice">
                                <p><strong>Cumplimiento GDPR:</strong> Procesamos tus datos conforme a nuestra <a href="%s/legal/privacy">Política de Privacidad</a>. 
                                Tienes derecho a acceder, rectificar, eliminar o portar tus datos. Para ejercer estos derechos, contáctanos en privacy@restaurant.com</p>
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>%s</strong></p>
                        <p>📍 %s</p>
                        <p>📞 %s</p>
                        <p>Email: %s</p>
                        <p><a href="%s/legal/terms" style="color: #ccc;">Términos y Condiciones</a> | 
                           <a href="%s/legal/privacy" style="color: #ccc;">Política de Privacidad</a></p>
                    </div>
                </div>
            </body>
            </html>
            """, 
            subject,
            restaurantName,
            htmlContent,
            frontendUrl,
            restaurantName,
            restaurantName,
            unsubscribeUrl,
            frontendUrl,
            frontendUrl,
            restaurantName,
            restaurantAddress,
            restaurantPhone,
            fromEmail,
            frontendUrl,
            frontendUrl);
    }
}
