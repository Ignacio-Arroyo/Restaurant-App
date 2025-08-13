# 🔐 Security Assessment & Guidelines

## Restaurant Management Application Security Report

**Report Date**: August 13, 2025  
**Application**: Restaurant Management System (Spring Boot + React)  
**Assessment Status**: ⚠️ **NEEDS IMMEDIATE ATTENTION**

---

## 🚨 **CRITICAL VULNERABILITIES IDENTIFIED**

### 1. Hardcoded JWT Secret (HIGH RISK - PRIORITY 1)
**Location**: `backend/src/main/resources/application.properties`
```properties
# VULNERABLE - Current configuration
jwt.secret=mySecretKey12345678901234567890123456789012
```

**Risk**: Anyone with access to the source code can forge JWT tokens and impersonate any user, including administrators.

**Solution**:
```properties
# SECURE - Use environment variables
jwt.secret=${JWT_SECRET:defaultSecretForDevelopmentOnly}
```

**Implementation**:
- Set `JWT_SECRET` environment variable in production
- Generate a strong 256-bit secret key
- Rotate secrets regularly

### 2. Hardcoded Database Credentials (HIGH RISK - PRIORITY 1)
**Locations**: 
- `backend/src/main/resources/application.properties`
- `docker-compose.yml`

```properties
# VULNERABLE - Current configuration
spring.datasource.username=postgres
spring.datasource.password=password
```

**Risk**: Database credentials exposure leads to complete data compromise.

**Solution**:
```properties
# SECURE - Environment-based configuration
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:changeme}
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/restaurante_db}
```

**Docker Compose Security**:
```yaml
# Use Docker secrets or environment files
backend:
  environment:
    - DB_USERNAME=${DB_USERNAME}
    - DB_PASSWORD=${DB_PASSWORD}
    - JWT_SECRET=${JWT_SECRET}
```

---

## ⚠️ **MEDIUM-HIGH RISK VULNERABILITIES**

### 3. Insecure JWT Token Storage (MEDIUM-HIGH RISK)
**Location**: Frontend uses `localStorage` for JWT tokens

**Files Affected**:
- `frontend/src/services/api.ts`
- `frontend/src/context/AuthContext.tsx`

**Risk**: XSS attacks can steal authentication tokens from localStorage.

**Current Vulnerable Code**:
```typescript
// VULNERABLE - localStorage is accessible to XSS
localStorage.setItem('token', response.token);
const token = localStorage.getItem('token');
```

**Recommended Solution**:
```typescript
// SECURE - Use HTTP-only cookies
// Backend: Set cookies with HttpOnly, Secure, SameSite attributes
// Frontend: Remove localStorage token handling
```

### 4. Permissive CORS Configuration (MEDIUM RISK)
**Location**: `backend/src/main/java/restaurante/backend/config/SecurityConfig.java`

**Current Configuration**:
```java
// PERMISSIVE - Allows multiple origins
.allowedOrigins("http://localhost:3000", "http://localhost:8080")
```

**Secure Configuration**:
```java
// RESTRICTIVE - Production-ready CORS
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of(
        System.getProperty("cors.allowed.origins", "http://localhost:3000")
    ));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    // Add security headers
    configuration.setExposedHeaders(List.of("Authorization"));
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## 🛡️ **SECURITY IMPLEMENTATION ROADMAP**

### Phase 1: Immediate Critical Fixes (Within 24 hours)

#### 1.1 Environment Variables Setup
Create `.env` files for different environments:

**`.env.development`**:
```bash
# Development Environment
JWT_SECRET=dev_secret_key_minimum_256_bits_long_for_hmac_sha256_algorithm
DB_USERNAME=postgres
DB_PASSWORD=dev_password
DB_URL=jdbc:postgresql://localhost:5432/restaurante_db
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

**`.env.production`**:
```bash
# Production Environment (DO NOT COMMIT THIS FILE)
JWT_SECRET=prod_super_secure_randomly_generated_256_bit_secret_key_here
DB_USERNAME=prod_db_user
DB_PASSWORD=prod_super_secure_database_password
DB_URL=jdbc:postgresql://prod-db-server:5432/restaurante_db
CORS_ALLOWED_ORIGINS=https://your-domain.com
```

#### 1.2 Update Application Configuration
**`application.properties`**:
```properties
# Security Configuration
jwt.secret=${JWT_SECRET:defaultSecretForDevelopmentOnly}
jwt.expiration-ms=${JWT_EXPIRATION:86400000}

# Database Configuration
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/restaurante_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:changeme}

# CORS Configuration
cors.allowed.origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000}

# Security Headers
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.same-site=strict

# Disable unnecessary endpoints
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never
```

### Phase 2: Enhanced Security Features (Within 1 week)

#### 2.1 Implement Secure Authentication
**Backend Changes**:
```java
// Add to SecurityConfig.java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .headers(headers -> headers
            .frameOptions().deny()
            .contentTypeOptions().and()
            .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                .maxAgeInSeconds(31536000)
                .includeSubdomains(true)
                .preload(true)
            )
            .and()
            .addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection", "1; mode=block"))
            .addHeaderWriter(new StaticHeadersWriter("Referrer-Policy", "strict-origin-when-cross-origin"))
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false)
        );
}
```

#### 2.2 Add Rate Limiting
```java
// Add dependency to pom.xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>

// Implement rate limiting for login attempts
@Component
public class RateLimitingFilter implements Filter {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        if (httpRequest.getRequestURI().contains("/auth/login")) {
            String clientId = getClientId(httpRequest);
            Bucket bucket = resolveBucket(clientId);
            
            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(429);
                httpResponse.getWriter().write("{\"error\":\"Too many requests\"}");
                return;
            }
        } else {
            chain.doFilter(request, response);
        }
    }
    
    private Bucket resolveBucket(String clientId) {
        return cache.computeIfAbsent(clientId, this::newBucket);
    }
    
    private Bucket newBucket(String clientId) {
        return Bucket4j.builder()
            .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
            .build();
    }
}
```

#### 2.3 Input Validation Enhancements
```java
// Add custom validators
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {
    String message() default "Password must contain at least 8 characters, including uppercase, lowercase, number and special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Update DTOs with stronger validation
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", 
             message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @StrongPassword
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
}
```

### Phase 3: Production Security (Within 2 weeks)

#### 3.1 HTTPS Configuration
```yaml
# docker-compose.prod.yml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "443:443"
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/ssl/certs
    depends_on:
      - frontend
      - backend
```

**Nginx Configuration**:
```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /etc/ssl/certs/cert.pem;
    ssl_certificate_key /etc/ssl/certs/key.pem;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self';" always;
}
```

#### 3.2 Database Security
```sql
-- Create dedicated application user
CREATE USER restaurant_app WITH ENCRYPTED PASSWORD 'secure_generated_password';
GRANT CONNECT ON DATABASE restaurante_db TO restaurant_app;
GRANT USAGE ON SCHEMA public TO restaurant_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO restaurant_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO restaurant_app;

-- Revoke unnecessary permissions
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE restaurante_db FROM PUBLIC;
```

#### 3.3 Monitoring & Logging
```properties
# Add to application.properties
logging.level.org.springframework.security=DEBUG
logging.level.restaurante.backend.security=INFO

# Custom security event logging
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{userId}] %logger{36} - %msg%n

# Enable audit events
management.auditevents.enabled=true
```

---

## 🔍 **SECURITY TESTING CHECKLIST**

### Manual Testing
- [ ] Test JWT token tampering
- [ ] Verify CORS policy enforcement
- [ ] Test SQL injection attempts
- [ ] Check XSS vulnerability in forms
- [ ] Verify rate limiting works
- [ ] Test authentication bypass attempts
- [ ] Verify secure headers are present
- [ ] Test session management

### Automated Security Testing
```bash
# Install OWASP ZAP for security scanning
docker run -t owasp/zap2docker-stable zap-baseline.py -t http://localhost:8080

# Use npm audit for frontend dependencies
cd frontend
npm audit --audit-level moderate

# Use Maven security plugin for backend
cd backend
mvn org.owasp:dependency-check-maven:check
```

---

## 📋 **DEPLOYMENT SECURITY CHECKLIST**

### Pre-Production Checklist
- [ ] All secrets moved to environment variables
- [ ] JWT secret is 256-bit random string
- [ ] Database credentials are unique and strong
- [ ] HTTPS is enforced
- [ ] Security headers are configured
- [ ] Rate limiting is implemented
- [ ] Input validation is comprehensive
- [ ] CORS is restrictively configured
- [ ] Unnecessary endpoints are disabled
- [ ] Logging and monitoring are configured

### Production Environment Variables
Create these environment variables in your production environment:
```bash
export JWT_SECRET="your-256-bit-secret-key-here"
export DB_USERNAME="restaurant_app"
export DB_PASSWORD="your-secure-db-password"
export DB_URL="jdbc:postgresql://your-db-server:5432/restaurante_db"
export CORS_ALLOWED_ORIGINS="https://your-domain.com"
```

---

## 🚨 **INCIDENT RESPONSE PLAN**

### If Security Breach Detected:
1. **Immediate Actions**:
   - Rotate all JWT secrets immediately
   - Change database passwords
   - Review access logs
   - Disable compromised user accounts

2. **Investigation**:
   - Identify attack vectors
   - Assess data exposure
   - Document findings

3. **Recovery**:
   - Apply security patches
   - Notify affected users
   - Implement additional monitoring

---

## 📞 **SECURITY CONTACTS**

- **Security Team**: [security@yourcompany.com]
- **Emergency Contact**: [emergency@yourcompany.com]
- **Incident Reporting**: [incidents@yourcompany.com]

---

## 📚 **ADDITIONAL RESOURCES**

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Security Best Practices](https://tools.ietf.org/html/rfc8725)
- [React Security Best Practices](https://reactjs.org/docs/dom-elements.html#dangerouslysetinnerhtml)

---

**Last Updated**: August 13, 2025  
**Next Review**: September 13, 2025  
**Document Version**: 1.0
