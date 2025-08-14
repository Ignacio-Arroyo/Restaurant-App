package restaurante.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import restaurante.backend.entity.*;
import restaurante.backend.repository.CouponRepository;
import restaurante.backend.repository.DrinkRepository;
import restaurante.backend.repository.MealRepository;
import restaurante.backend.repository.ProductRepository;
import restaurante.backend.repository.UserRepository;
import restaurante.backend.repository.WorkerRepository;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private DrinkRepository drinkRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Create admin user
        if (userRepository.findByEmail("admin@restaurant.com").isEmpty()) {
            User admin = new User("Admin", "User", "admin@restaurant.com", passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        // Create sample workers for testing roles
        if (workerRepository.count() == 0) {
            // Gerente de ejemplo
            if (workerRepository.findByEmail("gerente@restaurant.com").isEmpty()) {
                Worker gerente = new Worker("Carlos", "Rodríguez", "San José", "8888-1234", 
                    "gerente@restaurant.com", WorkerRole.GERENTE, "000001", "Costa Rica", 
                    passwordEncoder.encode("gerente123"));
                workerRepository.save(gerente);
            }

            // Cocinero de ejemplo
            if (workerRepository.findByEmail("cocinero@restaurant.com").isEmpty()) {
                Worker cocinero = new Worker("María", "González", "Cartago", "8888-5678", 
                    "cocinero@restaurant.com", WorkerRole.COCINERO, "000002", "Costa Rica", 
                    passwordEncoder.encode("cocinero123"));
                workerRepository.save(cocinero);
            }

            // Mesero de ejemplo
            if (workerRepository.findByEmail("mesero@restaurant.com").isEmpty()) {
                Worker mesero = new Worker("José", "Mora", "Alajuela", "8888-9012", 
                    "mesero@restaurant.com", WorkerRole.MESERO, "000003", "Costa Rica", 
                    passwordEncoder.encode("mesero123"));
                workerRepository.save(mesero);
            }

            // Cajero de ejemplo
            if (workerRepository.findByEmail("cajero@restaurant.com").isEmpty()) {
                Worker cajero = new Worker("Ana", "Castro", "Heredia", "8888-3456", 
                    "cajero@restaurant.com", WorkerRole.CAJERO, "000004", "Costa Rica", 
                    passwordEncoder.encode("cajero123"));
                workerRepository.save(cajero);
            }

            // Afanador de ejemplo
            if (workerRepository.findByEmail("afanador@restaurant.com").isEmpty()) {
                Worker afanador = new Worker("Luis", "Vargas", "Puntarenas", "8888-7890", 
                    "afanador@restaurant.com", WorkerRole.AFANADOR, "000005", "Costa Rica", 
                    passwordEncoder.encode("afanador123"));
                workerRepository.save(afanador);
            }
        }

        // Create sample meals
        if (mealRepository.count() == 0) {
            mealRepository.save(new Meal("Margherita Pizza", "Classic Italian pizza with tomato sauce, mozzarella, and fresh basil", 
                "Tomato sauce, mozzarella cheese, fresh basil, olive oil", "Gluten, Dairy", new BigDecimal("12.99"), MealType.VEGETARIAN));
            
            mealRepository.save(new Meal("Grilled Chicken Breast", "Tender grilled chicken breast with herbs and spices", 
                "Chicken breast, herbs, spices, olive oil", "None", new BigDecimal("16.99"), MealType.MEAT));
            
            mealRepository.save(new Meal("Vegan Buddha Bowl", "Nutritious bowl with quinoa, vegetables, and tahini dressing", 
                "Quinoa, mixed vegetables, tahini, lemon, herbs", "Sesame", new BigDecimal("14.99"), MealType.VEGAN));
            
            mealRepository.save(new Meal("Organic Salmon", "Fresh organic salmon with seasonal vegetables", 
                "Organic salmon, seasonal vegetables, herbs", "Fish", new BigDecimal("22.99"), MealType.BIO));
        }

        // Create sample drinks
        if (drinkRepository.count() == 0) {
            Drink cocaCola = new Drink("Coca Cola", "Classic cola soft drink", new BigDecimal("3.99"), DrinkType.SODA);
            cocaCola.setSize("MEDIUM");
            drinkRepository.save(cocaCola);
            
            Drink orangeJuice = new Drink("Fresh Orange Juice", "Freshly squeezed orange juice", new BigDecimal("5.99"), DrinkType.NATURAL);
            orangeJuice.setSize("LARGE");
            drinkRepository.save(orangeJuice);
            
            Drink houseWine = new Drink("House Wine", "Red house wine selection", new BigDecimal("8.99"), DrinkType.ALCOHOL);
            houseWine.setSize("SMALL");
            drinkRepository.save(houseWine);
            
            Drink sparklingWater = new Drink("Sparkling Water", "Premium sparkling water", new BigDecimal("2.99"), DrinkType.NATURAL);
            sparklingWater.setSize("MEDIUM");
            drinkRepository.save(sparklingWater);
        }

        // Create sample products for inventory
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Tomatoes", "Fresh organic tomatoes", new BigDecimal("3.50"), 50, "kg"));
            productRepository.save(new Product("Chicken Breast", "Fresh chicken breast", new BigDecimal("8.99"), 25, "kg"));
            productRepository.save(new Product("Mozzarella Cheese", "Fresh mozzarella cheese", new BigDecimal("12.99"), 15, "kg"));
            productRepository.save(new Product("Quinoa", "Organic quinoa", new BigDecimal("6.99"), 20, "kg"));
        }

        // Create sample coupons
        if (couponRepository.count() == 0) {
            // Cupón de bienvenida - 10% de descuento con compra mínima de ₡5,000
            Coupon welcomeCoupon = new Coupon("Descuento Bienvenida", "WELCOME2024", 
                Coupon.DiscountType.PERCENTAGE, new BigDecimal("10.00"));
            welcomeCoupon.setMinimumPurchase(new BigDecimal("5000.00"));
            couponRepository.save(welcomeCoupon);

            // Oferta especial - ₡2,500 de descuento con compra mínima de ₡10,000
            Coupon specialCoupon = new Coupon("Oferta Especial", "SPECIAL50OFF", 
                Coupon.DiscountType.FIXED, new BigDecimal("2500.00"));
            specialCoupon.setMinimumPurchase(new BigDecimal("10000.00"));
            couponRepository.save(specialCoupon);

            // Descuento VIP - 20% de descuento con compra mínima de ₡15,000
            Coupon vipCoupon = new Coupon("Descuento VIP", "VIP20PERCENT", 
                Coupon.DiscountType.PERCENTAGE, new BigDecimal("20.00"));
            vipCoupon.setMinimumPurchase(new BigDecimal("15000.00"));
            couponRepository.save(vipCoupon);

            // Descuento sin mínimo - 5% de descuento sin compra mínima
            Coupon noMinimumCoupon = new Coupon("Descuento Libre", "FREESAVE5PC", 
                Coupon.DiscountType.PERCENTAGE, new BigDecimal("5.00"));
            // Sin compra mínima (null)
            couponRepository.save(noMinimumCoupon);

            // Cupón inactivo para pruebas
            Coupon inactiveCoupon = new Coupon("Cupón de Prueba", "TESTINACTIVE01", 
                Coupon.DiscountType.PERCENTAGE, new BigDecimal("15.00"));
            inactiveCoupon.setActive(false);
            couponRepository.save(inactiveCoupon);
        }
    }
}
